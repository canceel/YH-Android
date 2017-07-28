package com.intfocus.yonghuitest.subject.template_v2.ui;

import android.os.Bundle;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.subject.template_v2.base.BaseActivity;
import com.zbl.lib.baseframe.core.inject.Main;

@Main
public class MainActivity extends BaseActivity {

    @Override
    public int setLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreateFinish(Bundle bundle) {

    }
}
