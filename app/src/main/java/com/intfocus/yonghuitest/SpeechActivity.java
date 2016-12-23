package com.intfocus.yonghuitest;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.iflytek.cloud.SpeechSynthesizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuruilin on 2016/11/30.
 */

public class SpeechActivity extends BaseActivity {
    private ListView mListView;
    public static TextView mCurrentSpeech, mSpeechData;
    private ArrayList<String> mSpeechList = new ArrayList<>();
    public static ImageView mPlayButton;
    private String speechAudio, userInfo;
    private JSONArray speechArray;
    private SpeechListAdapter mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        mPlayButton = (ImageView) findViewById(R.id.btn_play);
        mCurrentSpeech = (TextView) findViewById(R.id.txt_current_speech);
        mSpeechData = (TextView) findViewById(R.id.txt_speechdata);
        mPlayButton.setImageResource(R.drawable.btn_stop);

        initSpeechInfo();
        initSpeechList();

        if (!SpeechReport.getmTts(mAppContext).isSpeaking()) {
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
            if (SpeechReport.getmTts(mAppContext).isSpeaking()) {
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
        popupWindow.setOutsideTouchable(true);
    }

    public void playButton(View v) throws JSONException {
        SpeechReport.initReportAudio(speechArray.getJSONObject(0),0);
        if (SpeechReport.getmTts(mAppContext).isSpeaking()){
            SpeechReport.getmTts(mAppContext).stopSpeaking();
            mPlayButton.setImageResource(R.drawable.btn_play);
        }
        else {
            SpeechReport.startSpeechPlayer(mAppContext,speechArray);
            mPlayButton.setImageResource(R.drawable.btn_stop);
        }
    }

    public void nextButton(View v) throws JSONException {
        int speechNum = SpeechReport.speechNum;
        int speechLength = speechArray.length();
        int position = speechNum < speechLength -1 ? speechNum + 1 : 0;
        SpeechReport.initReportAudio(speechArray.getJSONObject(position),position);
        SpeechReport.getmTts(mAppContext).startSpeaking(SpeechReport.reportTitle
                        + SpeechReport.reportAudioSum
                        + SpeechReport.reportAudio
                        + "以上是全部内容,谢谢收听"
                ,SpeechReport.mPlayListener);
        mPlayButton.setImageResource(R.drawable.btn_stop);
    }

    public void previousButton(View v) throws JSONException {
        int speechNum = SpeechReport.speechNum;
        if (speechNum > 0) {
            int position = speechNum - 1;
            SpeechReport.initReportAudio(speechArray.getJSONObject(position),position);
            SpeechReport.getmTts(mAppContext).startSpeaking(SpeechReport.reportTitle
                            + SpeechReport.reportAudioSum
                            + SpeechReport.reportAudio
                            + "以上是全部内容,谢谢收听"
                    ,SpeechReport.mPlayListener);
            mPlayButton.setImageResource(R.drawable.btn_stop);
        }
        else {
            toast("当前为首只报表");
        }

    }

    public void launchSpeechListMenu(View v) {
        initSpeechMenu();
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow.getHeight());
    }

    /*
     * listview 点击事件
     */
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                SpeechReport.initReportAudio(speechArray.getJSONObject(position),position);
                SpeechReport.getmTts(mAppContext).startSpeaking(SpeechReport.reportTitle
                                                           + SpeechReport.reportAudioSum
                                                           + SpeechReport.reportAudio
                                                           + "以上是全部内容,谢谢收听"
                                                           ,SpeechReport.mPlayListener);
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
