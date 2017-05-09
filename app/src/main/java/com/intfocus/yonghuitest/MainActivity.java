package com.intfocus.yonghuitest;

import android.os.Bundle;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseActivity;
import com.intfocus.yonghuitest.base.BaseTableActivity;
import com.zbl.lib.baseframe.core.inject.Main;

@Main
public class MainActivity extends BaseTableActivity {

    @Override
    public int setLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreateFinish(Bundle bundle) {

    }
}
