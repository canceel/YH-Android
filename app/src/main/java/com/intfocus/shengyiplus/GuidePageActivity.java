package com.intfocus.shengyiplus;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.intfocus.shengyiplus.wxapi.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.intfocus.shengyiplus.BaseActivity.dip2px;

/**
 * Created by liuruilin on 2016/12/2.
 */

public class GuidePageActivity extends Activity implements ViewPager.OnPageChangeListener{
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private List<View> views = new ArrayList<>();
    private LinearLayout linearDot;
    private List<ImageView> dots = new ArrayList<>();
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isGuide();
        setContentView(R.layout.activity_guide);
        inflater = LayoutInflater.from(this);

        linearDot = (LinearLayout) findViewById(R.id.linearDot);

        init();
        initDot();
    }

    public void init() {
        View view1 = setViewAttributes(R.drawable.text_guide_1,R.drawable.img_guide_1,false);
        View view2 = setViewAttributes(R.drawable.text_guide_2,R.drawable.img_guide_2,false);
        View view3 = setViewAttributes(R.drawable.text_guide_3,R.drawable.img_guide_3,true);

        views.add(view1);
        views.add(view2);
        views.add(view3);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(this,views);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(this);
    }

    public View setViewAttributes(int guideTextBg,int guideImgBg,boolean isVisible) {
        View view = inflater.inflate(R.layout.activity_guide_adapter, null);
        ImageView guideTextImg = (ImageView) view.findViewById(R.id.guideTextImg);
        ImageView guideImg = (ImageView) view.findViewById(R.id.guideImg);
        Button textGuideBtn = (Button) view.findViewById(R.id.guideBtn);

        guideTextImg.setBackgroundResource(guideTextBg);
        guideImg.setBackgroundResource(guideImgBg);
        if (isVisible) {
            textGuideBtn.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void initDot() {
        for (int i=0; i<views.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.image_shape_button);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dip2px(this, 10), dip2px(this, 10));
            layoutParams.setMargins(dip2px(this, 5), 0, 0, 0);
            imageView.setLayoutParams(layoutParams);
            dots.add(imageView);
            linearDot.addView(imageView);
        }
        dots.get(0).setImageResource(R.drawable.image_shape_circle);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int j=0; j<views.size(); j++) {
            if (position == j) {
                dots.get(j).setImageResource(R.drawable.image_shape_circle);
            }
            else {
                dots.get(j).setImageResource(R.drawable.image_shape_button);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void isGuide() {
        SharedPreferences sharedPreferences = getSharedPreferences("guideSetting",MODE_PRIVATE);

        if (sharedPreferences.getBoolean("boolean",false)) {
            Intent intent = new Intent(GuidePageActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("boolean",true);
            editor.commit();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
