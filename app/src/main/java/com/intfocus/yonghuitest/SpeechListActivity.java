package com.intfocus.yonghuitest;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.JsonIOException;
import com.iflytek.cloud.SpeechSynthesizer;
import com.intfocus.yonghuitest.util.FileUtil;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.view.CircleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by liuruilin on 2016/11/30.
 */

public class SpeechListActivity extends BaseActivity{
    private ListView mListView;
    public static TextView mSpeechData, mCurrentSpeech;
    private ArrayList<String> mSpeechList = new ArrayList<>();
    private SpeechSynthesizer mTts;
    private CircleImageView mPlayButton;
    private String speechAudio, speechCachePath, userInfo;
    private JSONArray speechArray;
    private SpeechListAdapter.ListArrayAdapter mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_selector);

        mPlayButton = (CircleImageView) findViewById(R.id.btn_play);
        mCurrentSpeech = (TextView) findViewById(R.id.txt_current_speech);
        mSpeechData = (TextView) findViewById(R.id.txt_speechdata);
        mPlayButton.setImageResource(R.drawable.btn_stop);
        mTts = SpeechReport.getmTts(mAppContext);

        speechCachePath = FileUtil.dirPath(mAppContext, K.kHTMLDirName,"SpeechJson.plist");
        initSpeechList();
        initSpeechInfo();

        if (!mTts.isSpeaking()) {
            SpeechReport.speechNum = -1;
            SpeechReport.startSpeechPlayer(mAppContext,speechArray,userInfo);
        }
    }

    private void initSpeechInfo() {
        try {
            Intent intent = getIntent();
            speechAudio = intent.getStringExtra("speechAudio");
            speechArray = new JSONArray(speechAudio);
            userInfo = "本次报表针对" + user.getString("role_name") + user.getString("group_name");
            SpeechReport.initReportAudio(speechArray.getJSONObject(0),0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initSpeechList(){
        mSpeechList.add("播报列表初始化失败");
        try {
            if (new File(speechCachePath).exists()) {
                mSpeechList.clear();
                JSONObject speechJson = FileUtil.readConfigFile(speechCachePath);
                JSONArray speechArray = speechJson.getJSONArray("data");
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
        mArrayAdapter = SpeechListAdapter.SpeechListAdapter(this, R.layout.speech_list_item, mSpeechList);
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

    public void onClick(View v) throws JSONException{
        SpeechReport.initReportAudio(speechArray.getJSONObject(0),0);
        if (mTts.isSpeaking()){
            mTts.stopSpeaking();
            mPlayButton.setImageResource(R.drawable.btn_play);
        }
        else {
            SpeechReport.startSpeechPlayer(mAppContext,speechArray,userInfo);
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
     * 返回
     */
    public void dismissActivity(View v) {
        SpeechListActivity.this.onBackPressed();
        finish();
    }
}
