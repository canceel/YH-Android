package com.intfocus.yonghuitest.scanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.JsonParser;
import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.StoreSelectorActivity;
import com.intfocus.yonghuitest.base.BaseActivity;
import com.intfocus.yonghuitest.util.ApiHelper;
import com.intfocus.yonghuitest.util.FileUtil;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.LogUtil;
import com.intfocus.yonghuitest.util.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.webkit.WebView.enableSlowWholeDocumentDraw;

/**
 * Created by lijunjie on 16/6/10.
 */
public class BarCodeResultActivity extends BaseActivity {
//    private final static String kId = "id";
//    private String htmlContent, htmlPath, cachedPath;
//    private String codeInfo, codeType, groupID, roleID, userNum;
//    private String storeID;
//    private final ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
//    private TextView bannerTitle;
//    private JSONObject cachedJSON;
//    private ImageView mBannerSetting;
//
//    @Override
//    public void onCreate(Bundle state) {
//        super.onCreate(state);
//
//        /*
//         * 判断当前设备版本，5.0 以上 Android 系统使用才 enableSlowWholeDocumentDraw();
//         */
//        if (Build.VERSION.SDK_INT > 20) {
//            enableSlowWholeDocumentDraw();
//        }
//        setContentView(R.layout.activity_bar_code_result);
//
//        animLoading = (RelativeLayout) findViewById(R.id.anim_loading);
//        bannerTitle = (TextView) findViewById(R.id.bannerTitle);
//        mBannerSetting = (ImageView) findViewById(R.id.bannerSetting);
//
//        mWebView = (WebView) findViewById(R.id.barcode_browser);
//        WebSettings webSettings = mWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setDefaultTextEncodingName("utf-8");
//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        mWebView.addJavascriptInterface(new JavaScriptInterface(), URLs.kJSInterfaceName);
//        mWebView.setWebChromeClient(new WebChromeClient());
//        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
//                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
//                view.loadUrl(url);
//                return true;
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                LogUtil.d("onPageStarted", String.format("%s - %s", URLs.timestamp(), url));
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                animLoading.setVisibility(View.GONE);
//                isWeiXinShared = true;
//                LogUtil.d("onPageFinished", String.format("%s - %s", URLs.timestamp(), url));
//            }
//
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                LogUtil.d("onReceivedError", String.format("errorCode: %d, description: %s, url: %s", errorCode, description, failingUrl));
//            }
//        });
//        setWebViewLongListener(true);
//
//        initDropMenuItem();
//
//        List<ImageView> colorViews = new ArrayList<>();
//        colorViews.add((ImageView) findViewById(R.id.colorView0));
//        colorViews.add((ImageView) findViewById(R.id.colorView1));
//        colorViews.add((ImageView) findViewById(R.id.colorView2));
//        colorViews.add((ImageView) findViewById(R.id.colorView3));
//        colorViews.add((ImageView) findViewById(R.id.colorView4));
//        initColorView(colorViews);
//
//        String htmlOriginPath = String.format("%s/BarCodeScan/%s", sharedPath, K.kScanBarCodeHTMLName);
//        htmlContent = FileUtil.readFile(htmlOriginPath);
//        cachedPath = FileUtil.dirPath(mAppContext, K.kCachedDirName, K.kBarCodeResultFileName);
//        htmlPath = String.format("%s.tmp", htmlOriginPath);
//
//        try {
//            Intent intent = getIntent();
//            codeInfo = intent.getStringExtra(URLs.kCodeInfo);
//            codeType = intent.getStringExtra(URLs.kCodeType);
//            groupID = user.getString(URLs.kGroupId);
//            roleID = user.getString(URLs.kRoleId);
//            userNum = user.getString(URLs.kUserNum);
//
//            /*
//             * 商品条形码写入缓存
//             */
//            cachedJSON = FileUtil.readConfigFile(cachedPath);
//            JSONObject cachedCodeJSON = new JSONObject();
//            cachedCodeJSON.put(URLs.kCodeInfo, codeInfo);
//            cachedCodeJSON.put(URLs.kCodeType, codeType);
//            cachedJSON.put("barcode", cachedCodeJSON);
//            FileUtil.writeFile(cachedPath, cachedJSON.toString());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mMyApp.setCurrentActivity(this);
//        isWeiXinShared = false;
//        /*
//         * 初始化默认选中门店（第一家）
//         */
//        showBarCodeResult();
//    }
//
//    private void showBarCodeResult() {
//        try {
//            if (user.getJSONArray(URLs.kStoreIds).length() <= 0) {
//                // 若该数据为空,则该用户无门店权限
//                animLoading.setVisibility(View.GONE);
//                mWebView.setVisibility(View.GONE);
//                mBannerSetting.setVisibility(View.GONE);
//                TextView errorText = (TextView) findViewById(R.id.text_permission);
//                errorText.setVisibility(View.VISIBLE);
//                return;
//            }
//
//            selectStore();
//            loadBarCodeResult();
//        } catch (JSONException | IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void selectStore() throws JSONException,IOException {
//        cachedJSON = FileUtil.readConfigFile(cachedPath);
//        boolean flag = false;
//        String storeName = "";
//            if (cachedJSON.has(URLs.kStore) && cachedJSON.getJSONObject(URLs.kStore).has(kId) &&
//                    user.has(URLs.kStoreIds)) {
//                storeName = cachedJSON.getJSONObject(URLs.kStore).getString("name");
//                for (int i = 0; i < user.getJSONArray(URLs.kStoreIds).length(); i++) {
//                    if (user.getJSONArray(URLs.kStoreIds).getJSONObject(i).getString("name").equals(storeName)){
//                        flag = true;
//                    }
//                }
//            }
//
//            if (!flag) {
//                storeName = user.getJSONArray(URLs.kStoreIds).getJSONObject(0).getString("name");
//                cachedJSON.put(URLs.kStore, user.getJSONArray(URLs.kStoreIds).get(0));
//                FileUtil.writeFile(cachedPath, cachedJSON.toString());
//            }
//            bannerTitle.setText(storeName);
//            storeID = cachedJSON.getJSONObject(URLs.kStore).getString(kId);
//    }
//
//    private void loadBarCodeResult() {
//        animLoading.setVisibility(View.VISIBLE);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Map<String,String> response = ApiHelper.barCodeScan(storeID, codeInfo);
//                String responseCode = response.get(URLs.kCode);
//                String responseString = response.get(URLs.kBody);
//                Log.i("barcodeResult", responseString);
//                try{
//                    new JsonParser().parse(responseString).getAsJsonObject();
//                }catch (Exception e) {
//                    showWebViewExceptionForWithoutNetwork();
//                    return ;
//                }
//                updateHtmlContentTimetamp();
//
//                if (!responseCode.equals("200") || responseString.equals("{}")) {
//                    showWebViewExceptionForWithoutNetwork();
//                }
//                else {
//                    FileUtil.barCodeScanResult(mAppContext, responseString);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mWebView.loadUrl(String.format("file:///%s", htmlPath));
//                        }
//                    });
//                }
//            }
//        }).start();
//    }
//
//    /*
//     * 返回
//     */
//    public void dismissActivity(View v) {
//        BarCodeResultActivity.this.onBackPressed();
//    }
//
//    private void updateHtmlContentTimetamp() {
//        try {
//            String newHtmlContent = htmlContent.replaceAll("TIMESTAMP", String.format("%d", new Date().getTime()));
//            Log.i("HtmlContentTimetamp", newHtmlContent);
//            FileUtil.writeFile(htmlPath, newHtmlContent);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//

//
//    /*
//     * 初始化标题栏下拉菜单
//     */
//    private void initDropMenuItem() {
//        String[] itemName = {"筛选", "分享","刷新"};
//        int[] itemImage = {R.drawable.banner_search, R.drawable.banner_share,R.drawable.btn_refresh};
//        for (int i = 0; i < itemName.length; i++) {
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("ItemImage", itemImage[i]);
//            map.put("ItemText", itemName[i]);
//            listItem.add(map);
//        }
//
//        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.menu_list_items, new String[]{"ItemImage", "ItemText"}, new int[]{R.id.img_menu_item, R.id.text_menu_item});
//        initDropMenu(mSimpleAdapter, mDropMenuListener);
//    }
//
//    /*
//     * 标题栏设置按钮下拉菜单点击响应事件
//     */
//    private final AdapterView.OnItemClickListener mDropMenuListener = new AdapterView.OnItemClickListener() {
//        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                                long arg3) {
//            popupWindow.dismiss();
//
//            switch (listItem.get(arg2).get("ItemText").toString()) {
//                case "筛选":
//                    actionLaunchStoreSelectorActivity();
//                    break;
//
//                case "分享":
//                    actionShare2Weixin();
//                    break;
//
//                case "刷新":
//                    refresh();
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    };
//
//    private void refresh() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                showBarCodeResult();
//            }
//        });
//    }
//
//    /*
//     * 标题栏点击设置按钮显示下拉菜单
//     */
//    public void launchDropMenuActivity(View v) {
//        popupWindow.showAsDropDown(mBannerSetting, dip2px(this, -47), dip2px(this, 10));
//
//		/*
//         * 用户行为记录, 单独异常处理，不可影响用户体验
//		 */
//        try {
//            logParams = new JSONObject();
//            logParams.put("action", "点击/报表/下拉菜单");
//            new Thread(mRunnableForLogger).start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//

//
//    private class JavaScriptInterface{
//        /*
//         * JS 接口，暴露给JS的方法使用@JavascriptInterface装饰
//         */
//        @JavascriptInterface
//        public void refreshBrowser() {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    animLoading.setVisibility(View.VISIBLE);
//                    showBarCodeResult();
//                }
//            });
//        }
//    }
}
