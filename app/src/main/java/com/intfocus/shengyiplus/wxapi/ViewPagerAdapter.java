package com.intfocus.shengyiplus.wxapi;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.intfocus.shengyiplus.LoginActivity;
import com.intfocus.shengyiplus.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuruilin on 2016/12/2.
 */

public class ViewPagerAdapter extends PagerAdapter implements View.OnClickListener {
    private List<View> views = new ArrayList<>();
    private Button button;
    private Context context;

    public ViewPagerAdapter(Context context,List<View> views) {
        this.context = context;
        this.views = views;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView(views.get(position));
    }

    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position), 0);
        button = (Button) views.get(position).findViewById(R.id.guideBtn);

        button.setOnClickListener(this);

        return views.get(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.guideBtn:
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                break;
        }
    }
}
