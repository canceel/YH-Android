package com.intfocus.yonghuitest;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.intfocus.yonghuitest.util.ApiHelper;
import com.intfocus.yonghuitest.util.FileUtil;
import com.intfocus.yonghuitest.util.HttpUtil;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by liuruilin on 2016/11/15.
 */

public class SpeechReport {
    private static Context context;
    private static MediaPlayer mediaPlayer;
    private static SpeechSynthesizer mTts;                            // 语音合成对象
    public static JSONArray speechArray;
    public static int speechNum = 0;
    private static SpeechListAdapter.ListArrayAdapter mSpeechListAdapter;
    public static String reportTitle, reportAudio, reportAudio2Shwo, reportAudioSum;


    public static void startSpeechPlayer(Context mContext, JSONArray array, String userInfo) {
        context = mContext;
        speechArray = array;
        mTts = initSpeechSynthesizer(mContext);
        initTtsParms();
        String reportSum = "共" + speechArray.length() + "支报表播报";
        Log.i("speechError", reportSum);
        mTts.startSpeaking(userInfo + reportSum, mPlayListener);
    }

    private static SpeechSynthesizer initSpeechSynthesizer(Context mContext) {
        return SpeechSynthesizer.createSynthesizer(mContext, null);
    }


    public static SpeechSynthesizer getmTts(Context mContext) {
        if (mTts == null) {
            return initSpeechSynthesizer(mContext);
        } else {
            return mTts;
        }
    }

    /*
     * 语音合成参数
     */
    private static void initTtsParms() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);   //设置云端
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaofeng");                   //设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");                              //设置语速
        mTts.setParameter(SpeechConstant.PITCH, "50");                              //设置合成音调
        mTts.setParameter(SpeechConstant.VOLUME, "80");                             //设置音量，范围0~100
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
    }

    /*
     * 拼接语音播报内容
     */
    public static void initReportAudio(JSONObject speechInfo, int number) throws JSONException {
        speechNum = number;
        reportTitle = "报表名称：" + speechInfo.getString("title");
        reportAudio = speechInfo.getJSONArray("audio").toString();
        reportAudio2Shwo = reportAudio.replace("[\"", "").replace("\",\"", "\n").replace("\"]", "");
        SpeechListActivity.mSpeechData.setText(reportAudio2Shwo);
        SpeechListActivity.mCurrentSpeech.setText("正在播报: " + speechInfo.getString("title"));
        reportAudioSum = "共" + speechInfo.getJSONArray("audio").length() + "条";
    }

    /*
     * 语音合成回调 - 云端合成
     */
    public static SynthesizerListener mPlayListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
            try {
                mTts.destroy();
                if (speechNum <= speechArray.length() && error == null) {
                    mTts = initSpeechSynthesizer(context);
                    initTtsParms();
                    speechNum++;
                    JSONObject speechInfo = speechArray.getJSONObject(speechNum);
                    initReportAudio(speechInfo, speechNum);
                    mTts.startSpeaking(reportTitle + reportAudioSum + reportAudio, mPlayListener);
                    mSpeechListAdapter = SpeechListAdapter.getAdapter();
                    mSpeechListAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
        }

        //暂停播放
        public void onSpeakPaused() {
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };

    public static JSONArray infoProcess(final Context mContext, final String urlString) {
        String mAssetsPath = FileUtil.dirPath(mContext, K.kHTMLDirName);
        String speechArrayPath = FileUtil.dirPath(mContext, K.kHTMLDirName, "SpeechArray.plist");
        JSONArray speechArray = null;
        try {
            Map<String, String> responseHeader = ApiHelper.checkResponseHeader(urlString, mAssetsPath);
            Map<String, String> response = HttpUtil.httpGet(urlString, responseHeader);
            if (response.get("code").equals("200")) {
                JSONObject speechJson = new JSONObject(response.get("body"));
                speechArray = speechJson.getJSONArray("data");
                FileUtil.writeFile(speechArrayPath, speechArray.toString());
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return speechArray;
    }

}
