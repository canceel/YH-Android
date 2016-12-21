package com.intfocus.yonghuitest;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.iflytek.cloud.SpeechSynthesizer;
import com.intfocus.yonghuitest.view.CircleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuruilin on 2016/11/30.
 */

public class SpeechActivity extends BaseActivity{
    private ListView mListView;
    public static TextView mCurrentSpeech, mSpeechData;
    private ArrayList<String> mSpeechList = new ArrayList<>();
    private SpeechSynthesizer mTts;
    private CircleImageView mPlayButton;
    private String speechAudio, userInfo;
    private JSONArray speechArray;
    private SpeechListAdapter mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        mPlayButton = (CircleImageView) findViewById(R.id.btn_play);
        mCurrentSpeech = (TextView) findViewById(R.id.txt_current_speech);
        mSpeechData = (TextView) findViewById(R.id.txt_speechdata);
        mPlayButton.setImageResource(R.drawable.btn_stop);
        mTts = SpeechReport.getmTts(mAppContext);
        mSpeechData.setOnTouchListener(mTxtOnTouchListener);
        mSpeechData.setFocusable(true);
        mSpeechData.setFocusableInTouchMode(true);
        mSpeechData.requestFocus();

        initSpeechInfo();
        initSpeechList();

        if (!mTts.isSpeaking()) {
            SpeechReport.speechNum = -1;
            SpeechReport.startSpeechPlayer(mAppContext,speechArray);
        }
    }

    private void initSpeechInfo() {
        try {
            Intent intent = getIntent();
            speechAudio = intent.getStringExtra("speechAudio");
            speechArray = new JSONArray(speechAudio);
            userInfo = "本次报表针对" + user.getString("role_name") + user.getString("group_name");
            SpeechReport.reportUser = userInfo;
            if (mTts.isSpeaking()) {
                SpeechReport.initReportAudio(speechArray.getJSONObject(SpeechReport.speechNum),SpeechReport.speechNum);
            }
            else {
                SpeechReport.initReportAudio(speechArray.getJSONObject(0),0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initSpeechList(){
        mSpeechList.add("播报列表初始化失败");
        try {
            if (speechArray != null && speechArray.length() > 0) {
                mSpeechList.clear();
                for (int i = 0, len = speechArray.length(); i < len; i++) {
                    JSONObject speechInfo = (JSONObject) speechArray.get(i);
                    mSpeechList.add(speechInfo.getString("title"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initSpeechMenu() {
        final View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_speech, null);
        mListView = (ListView) contentView.findViewById(R.id.pop_list_speech);
        mListView.setOnItemClickListener(mItemClickListener);
        mArrayAdapter = new SpeechListAdapter(this, R.layout.speech_list_item, mSpeechList);
        mListView.setAdapter(mArrayAdapter);
        mListView.setTextFilterEnabled(true);

        popupWindow = new PopupWindow(this);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(800);
        popupWindow.setContentView(contentView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
    }

    public void onClick(View v) throws JSONException{
        SpeechReport.initReportAudio(speechArray.getJSONObject(0),0);
        if (mTts.isSpeaking()){
            mTts.destroy();
            mPlayButton.setImageResource(R.drawable.btn_play);
        }
        else {
            SpeechReport.startSpeechPlayer(mAppContext,speechArray);
            mPlayButton.setImageResource(R.drawable.btn_stop);
        }
    }

    public void launchSpeechListMenu(View v) {
        initSpeechMenu();
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow.getHeight());
    }

    /*
     * 播报文本触摸事件
     */
    private View.OnTouchListener mTxtOnTouchListener = new View.OnTouchListener() {
        private int mode = 0;
        float oldDist;
        TextView textView = null;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            textView = (TextView) v;
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mode = 1;
                    break;

                case MotionEvent.ACTION_UP:
                    mode = 0;
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    mode -= 1;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    mode += 1;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode >= 2) {
                        float newDist = spacing(event);
                        if (newDist > oldDist + 10) {
                            zoom();
                        }
                        if (newDist < oldDist - 10) {
                            minZoom();
                        }
                    }
                    break;
            }
            return true;
        }

        private void zoom() {
            textView.setHeight(dip2px(mAppContext,800));
        }

        private void minZoom() {
            textView.setHeight(dip2px(mAppContext,300));
        }

        private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float)Math.sqrt(x * x + y * y);
        }
    };

    /*
     * listview 点击事件
     */
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                SpeechReport.initReportAudio(speechArray.getJSONObject(position),position);
                mTts.startSpeaking(SpeechReport.reportTitle + SpeechReport.reportAudioSum + SpeechReport.reportAudio,SpeechReport.mPlayListener);
                mPlayButton.setImageResource(R.drawable.btn_stop);
                mArrayAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /*
     * 返回
     */
    public void dismissActivity(View v) {
        SpeechActivity.this.onBackPressed();
        finish();
    }
}
