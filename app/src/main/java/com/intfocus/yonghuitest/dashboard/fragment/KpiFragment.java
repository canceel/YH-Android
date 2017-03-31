package com.intfocus.yonghuitest.dashboard.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.WebViewFragment;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.URLs;
import com.intfocus.yonghuitest.view.CustomWebView;

import org.json.JSONException;

import static com.intfocus.yonghuitest.util.URLs.kGroupId;

/**
 * Created by liuruilin on 2017/3/26.
 */

public class KpiFragment extends WebViewFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        mWebView = (CustomWebView) view.findViewById(R.id.browser);
        mAnimLoading = (RelativeLayout) view.findViewById(R.id.anim_loading);
        initWebView();
        initSwipeLayout(view);
        String currentUIVersion = URLs.currentUIVersion(mAppContext);
        try {
            urlString = String.format(K.kKPIMobilePath, K.kBaseUrl, currentUIVersion, user.getString(
                    kGroupId), user.getString(URLs.kRoleId));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(mRunnableForDetecting).start();
        return view;
    }
}
