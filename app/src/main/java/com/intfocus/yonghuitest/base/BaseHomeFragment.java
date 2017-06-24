package com.intfocus.yonghuitest.base;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.intfocus.yonghuitest.YHApplication;
import com.intfocus.yonghuitest.util.ApiHelper;
import com.intfocus.yonghuitest.util.FileUtil;
import com.intfocus.yonghuitest.util.HttpUtil;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.LogUtil;
import com.intfocus.yonghuitest.util.URLs;
import com.intfocus.yonghuitest.view.CustomWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by liuruilin on 2017/3/22.
 */

public class BaseHomeFragment extends BaseFragment {
    public SwipeRefreshLayout mSwipeLayout;
    public RelativeLayout mAnimLoading;
    public Context mContext;
    public YHApplication mMyApp;
    public Context mAppContext;
    public CustomWebView mWebView;
    public String urlString, sharedPath, assetsPath, urlStringForDetecting, relativeAssetsPath, urlStringForLoading;
    public JSONObject user;
    public int userID;
    public Activity act;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        act = (Activity) context;
        mMyApp = (YHApplication) getActivity().getApplication();
        mAppContext = mMyApp.getAppContext();
        sharedPath = FileUtil.sharedPath(mContext);
        assetsPath = sharedPath;
        urlStringForDetecting = K.kBaseUrl;
        relativeAssetsPath = "assets";
        urlStringForLoading = loadingPath("loading");

        String userConfigPath = String.format("%s/%s", FileUtil.basePath(mContext), K.kUserConfigFileName);
        if ((new File(userConfigPath)).exists()) {
            try {
                user = FileUtil.readConfigFile(userConfigPath);
                if (user.has(URLs.kIsLogin) && user.getBoolean(URLs.kIsLogin)) {
                    userID = user.getInt("user_id");
                    assetsPath = FileUtil.dirPath(mContext, K.kHTMLDirName);
                    urlStringForDetecting = String.format(K.kDeviceStateAPIPath, K.kBaseUrl, user.getInt("user_device_id"));
                    relativeAssetsPath = "../../Shared/assets";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected String loadingPath(String htmlName) {
        return String.format("file:///%s/loading/%s.html", sharedPath, htmlName);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    public void showWebViewExceptionForWithoutNetwork() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String urlStringForLoading = loadingPath("400");
                mWebView.loadUrl(urlStringForLoading);
            }
        });
    }

    protected final HandlerForDetecting mHandlerForDetecting = new HandlerForDetecting();
    protected final HandlerWithAPI mHandlerWithAPI = new HandlerWithAPI();

    protected final Runnable mRunnableForDetecting = new Runnable() {
        @Override
        public void run() {
            Map<String, String> response = HttpUtil.httpGet(urlStringForDetecting,
                    new HashMap<String, String>());

            int statusCode = Integer.parseInt(response.get(URLs.kCode));
            if (statusCode == 200 && !urlStringForDetecting.equals(K.kBaseUrl)) {
                try {
                    JSONObject json = new JSONObject(response.get("body"));
                    statusCode = json.getBoolean("device_state") ? 200 : 401;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mHandlerForDetecting.setVariables(mWebView, urlString, sharedPath, assetsPath, relativeAssetsPath);
            Message message = mHandlerForDetecting.obtainMessage();
            message.what = statusCode;
            mHandlerForDetecting.sendMessage(message);
        }
    };

    /**
     * Instances of static inner classes do not hold an implicit reference to their outer class.
     */
    public class HandlerForDetecting extends Handler {
        private WebView mWebView;
        private String mSharedPath;
        private String mUrlString;
        private String mAssetsPath;
        private String mRelativeAssetsPath;

        public HandlerForDetecting() {

        }

        public void setVariables(WebView webView, String urlString, String sharedPath, String assetsPath, String relativeAssetsPath) {
            mWebView = webView;
            mUrlString = urlString;
            mSharedPath = sharedPath;
            mUrlString = urlString;
            mAssetsPath = assetsPath;
            mRelativeAssetsPath = relativeAssetsPath;
        }

        protected String loadingPath(String htmlName) {
            return String.format("file:///%s/loading/%s.html", mSharedPath, htmlName);
        }

        private void showWebViewForWithoutNetwork() {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    String urlStringForLoading = loadingPath("400");
                    mWebView.loadUrl(urlStringForLoading);
                }
            });
        }

        private final Runnable mRunnableWithAPI = new Runnable() {
            @Override
            public void run() {
                LogUtil.d("httpGetWithHeader", String.format("url: %s, assets: %s, relativeAssets: %s", mUrlString, mAssetsPath, mRelativeAssetsPath));
                final Map<String, String> response = ApiHelper.httpGetWithHeader(mUrlString, mAssetsPath, mRelativeAssetsPath);
                Looper.prepare();
                HandlerWithAPI mHandlerWithAPI = new HandlerWithAPI();
                mHandlerWithAPI.setVariables(mWebView, mSharedPath, mAssetsPath);
                Message message = mHandlerWithAPI.obtainMessage();
                message.what = Integer.parseInt(response.get(URLs.kCode));
                message.obj = response.get("path");

                LogUtil.d("mRunnableWithAPI",
                        String.format("code: %s, path: %s", response.get(URLs.kCode), response.get("path")));
                mHandlerWithAPI.sendMessage(message);
                Looper.loop();
            }
        };

        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case 200:
                case 201:
                case 304:
                    new Thread(mRunnableWithAPI).start();
                    break;
                case 400:
                case 408:
                    showWebViewForWithoutNetwork();
                    break;
                case 401:
                    break;
                default:
                    showWebViewForWithoutNetwork();
                    LogUtil.d("UnkownCode", String.format("%d", message.what));
                    break;
            }
        }

    }

    public class HandlerWithAPI extends Handler {
        private WebView mWebView;
        private String mSharedPath;
        private String mAssetsPath;

        public void setVariables(WebView webView, String sharedPath, String assetsPath) {
            mWebView = webView;
            mSharedPath = sharedPath;
            mAssetsPath = assetsPath;
        }

        protected String loadingPath(String htmlName) {
            return String.format("file:///%s/loading/%s.html", mSharedPath, htmlName);
        }

        private void showWebViewForWithoutNetwork() {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    String urlStringForLoading = loadingPath("400");
                    mWebView.loadUrl(urlStringForLoading);
                }
            });
        }

        private void deleteHeadersFile() {
            String headersFilePath = String.format("%s/%s", mAssetsPath, K.kCachedHeaderConfigFileName);
            if ((new File(headersFilePath)).exists()) {
                new File(headersFilePath).delete();
            }
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 200:
                case 304:
                    final String localHtmlPath = String.format("file:///%s", (String) message.obj);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mWebView.loadUrl(localHtmlPath);
                        }
                    });
                    break;
                case 400:
                case 401:
                case 408:
                    showWebViewForWithoutNetwork();
                    deleteHeadersFile();
                    break;
                default:
                    String msg = String.format("访问服务器失败（%d)", message.what);
                    showWebViewForWithoutNetwork();
                    deleteHeadersFile();
                    break;
            }

            if (mSwipeLayout.isRefreshing()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeLayout.setRefreshing(false);
                    }
                });
            }
        }
    }
}
