package com.intfocus.yonghuitest.subject.template_v2;

import android.os.Bundle;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseModeActivity;
import com.zbl.lib.baseframe.core.inject.Main;

@Main
public class MainModeActivity extends BaseModeActivity {

    @Override
    public int setLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreateFinish(Bundle bundle) {

    }
}
