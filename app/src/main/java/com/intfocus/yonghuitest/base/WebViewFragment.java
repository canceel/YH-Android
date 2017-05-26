package com.intfocus.yonghuitest.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.intfocus.yonghuitest.HomeTricsActivity;
import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.SubjectActivity;
import com.intfocus.yonghuitest.TableActivity;
import com.intfocus.yonghuitest.dashboard.kpi.ui.MainActivity;
import com.intfocus.yonghuitest.util.ApiHelper;
import com.intfocus.yonghuitest.util.FileUtil;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.LogUtil;
import com.intfocus.yonghuitest.util.URLs;
import com.intfocus.yonghuitest.view.CustomWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by liuruilin on 2017/3/30.
 */

public class WebViewFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public void initSwipeLayout(View view) {
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setDistanceToTriggerSync(300);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setSize(SwipeRefreshLayout.DEFAULT);
    }

    @Override
    public void onRefresh() {
        if (urlString != null && !urlString.isEmpty()) {
            ApiHelper.clearResponseHeader(urlString, assetsPath);
        }
        new Thread(mRunnableForDetecting).start();
    }

    public void initWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setDrawingCacheEnabled(true);
        mWebView.addJavascriptInterface(new JavaScriptBase(), URLs.kJSInterfaceName);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogUtil.d("onPageStarted", String.format("%s - %s", URLs.timestamp(), url));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mAnimLoading.setVisibility(View.GONE);
                LogUtil.d("onPageFinished", String.format("%s - %s", URLs.timestamp(), url));
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                LogUtil.d("onReceivedError",
                        String.format("errorCode: %d, description: %s, url: %s", errorCode, description,
                                failingUrl));
            }
        });

        mWebView.setOnScrollChangedCallback(new CustomWebView.OnScrollChangedCallback() {
            public void onScroll(int horizontal, int vertical) {
                System.out.println("==" + horizontal + "---" + vertical);
                //this is to check webview scroll
                if (vertical < 50) {
                    mSwipeLayout.setEnabled(true);
                } else {
                    mSwipeLayout.setEnabled(false);
                }
            }
        });

        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
    }

    public class JavaScriptBase {
        /*
         * JS 接口，暴露给JS的方法使用@JavascriptInterface装饰
         */
        @JavascriptInterface
        public void refreshBrowser() {
            new Thread(mRunnableForDetecting).start();
        }

        @JavascriptInterface
        public void openURLWithSystemBrowser(final String url) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (url == null || (!url.startsWith("http://") && !url.startsWith("https://"))) {
                        return;
                    }
                    Intent browserIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            });
        }

        /*
         * JS 接口，暴露给JS的方法使用@JavascriptInterface装饰
         */
        @JavascriptInterface
        public void pageLink(final String bannerName, final String link, final int objectID) {
            if (null == link || link.equals("")) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = String.format("%s\n%s\n%d", bannerName, link, objectID);
                    LogUtil.d("JSClick", message);

                    if (link.indexOf("template") > 0 && link.indexOf("group") > 0) {
                        try {
                            String templateID = TextUtils.split(link, "/")[6];
                            int groupID = user.getInt(URLs.kGroupId);
                            String reportID = TextUtils.split(link, "/")[8];
                            String urlString = link;

                            if (templateID.equals("-1") || templateID.equals("2") || templateID.equals("4")) {
                                Intent intent = new Intent(getActivity(), SubjectActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.putExtra(URLs.kBannerName, bannerName);
                                intent.putExtra(URLs.kLink, link);
                                intent.putExtra(URLs.kObjectId, objectID);
                                intent.putExtra(URLs.kObjectType, 1);
                                startActivity(intent);
                            } else if (templateID.equals("3")) {
                                Intent homeTricsIntent = new Intent(mContext, HomeTricsActivity.class);
                                homeTricsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                urlString = String.format("%s/api/v1/group/%d/template/%s/report/%s/json", K.kBaseUrl, groupID, templateID, reportID);
                                homeTricsIntent.putExtra("urlString", urlString);
                                homeTricsIntent.putExtra(URLs.kBannerName, bannerName);
                                mContext.startActivity(homeTricsIntent);
                            } else if (templateID.equals("5")) {
                                Intent superTableIntent = new Intent(mContext, TableActivity.class);
                                superTableIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                superTableIntent.putExtra(URLs.kBannerName, bannerName);
                                superTableIntent.putExtra("groupID", groupID);
                                superTableIntent.putExtra("reportID", reportID);
                                mContext.startActivity(superTableIntent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("温馨提示")
                                        .setMessage("当前版本暂不支持该模板, 请升级应用后查看")
                                        .setPositiveButton("前去升级", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(K.kPgyerUrl));
                                                startActivity(browserIntent);
                                            }
                                        })
                                        .setNegativeButton("稍后升级", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // 返回 LoginActivity
                                            }
                                        });
                                builder.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent intent = new Intent(getActivity(), SubjectActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra(URLs.kBannerName, bannerName);
                        intent.putExtra(URLs.kLink, link);
                        intent.putExtra(URLs.kObjectId, objectID);
                        intent.putExtra(URLs.kObjectType, 1);
                        startActivity(intent);
                    }
                }
            });
        }

        @JavascriptInterface
        public void storeTabIndex(final String pageName, final int tabIndex) {
            try {
                String filePath = FileUtil.dirPath(mAppContext, K.kConfigDirName, K.kBehaviorConfigFileName);

                if ((new File(filePath).exists())) {
                    String fileContent = FileUtil.readFile(filePath);
                    JSONObject jsonObject = new JSONObject(fileContent);
                    JSONObject config = new JSONObject(jsonObject.getString("dashboard"));
                    config.put(pageName, tabIndex);
                    jsonObject.put("dashboard", config.toString());

                    FileUtil.writeFile(filePath, jsonObject.toString());
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public int restoreTabIndex(final String pageName) {
            int tabIndex = 0;
            try {
                String filePath = FileUtil.dirPath(mAppContext, K.kConfigDirName, K.kBehaviorConfigFileName);

                if ((new File(filePath).exists())) {
                    String fileContent = FileUtil.readFile(filePath);
                    JSONObject jsonObject = new JSONObject(fileContent);
                    JSONObject config = new JSONObject(jsonObject.getString("dashboard"));
                    if (config.has(pageName)) {
                        tabIndex = config.getInt(pageName);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return tabIndex < 0 ? 0 : tabIndex;
        }
    }
}
