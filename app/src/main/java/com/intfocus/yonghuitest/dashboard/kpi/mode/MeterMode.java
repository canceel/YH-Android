package com.intfocus.yonghuitest.dashboard.kpi.mode;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intfocus.yonghuitest.dashboard.kpi.entity.MererEntity;
import com.intfocus.yonghuitest.dashboard.kpi.entity.Message;
import com.intfocus.yonghuitest.dashboard.kpi.entity.msg.MeterRequestResult;
import com.intfocus.yonghuitest.util.HttpUtil;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.URLs;
import com.zbl.lib.baseframe.core.AbstractMode;
import com.zbl.lib.baseframe.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.intfocus.yonghuitest.util.K.kUserId;
import static com.intfocus.yonghuitest.util.URLs.kGroupId;

/**
 * 仪表盘-数据处理模块
 * Created by zbaoliang on 17-4-28.
 */
public class MeterMode extends AbstractMode {

    Context ctx;
    String urlString;
    String messageUrlString;
    JSONObject user;

    public MeterMode(Context ctx) {
        this.ctx = ctx;
    }

    public String getKpiUrl() {
        String url;
        try {
            String userConfigPath = String.format("%s/%s", com.intfocus.yonghuitest.util.FileUtil.basePath(ctx), K.kUserConfigFileName);
            if ((new File(userConfigPath)).exists()) {
                user = com.intfocus.yonghuitest.util.FileUtil.readConfigFile(userConfigPath);
            }
            String currentUIVersion = URLs.currentUIVersion(ctx);
            url = String.format(K.kKPIMobileDataPath, K.kBaseUrl, currentUIVersion, user.getString(
                    kGroupId), user.getString(URLs.kRoleId));
        } catch (JSONException e) {
            url = null;
            e.printStackTrace();
        }
        return url;
    }

    public String getMessageUrl() {
        String url;
        try {
            String userConfigPath = String.format("%s/%s", com.intfocus.yonghuitest.util.FileUtil.basePath(ctx), K.kUserConfigFileName);
            if ((new File(userConfigPath)).exists()) {
                user = com.intfocus.yonghuitest.util.FileUtil.readConfigFile(userConfigPath);
            }
            url = String.format(K.kMessageDataMobilePath, K.kBaseUrl, user.getString(URLs.kRoleId), user.getString(
                    kGroupId), user.getString(kUserId));
        } catch (JSONException e) {
            url = null;
            e.printStackTrace();
        }
        return url;
    }
    @Override
    public void requestData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                urlString = getKpiUrl();
                if (!urlString.isEmpty()) {
                    Log.i("kpiUrl", urlString);
                    Map<String, String> response = HttpUtil.httpGet(urlString, new HashMap<String, String>());
                    String result = response.get("body");
                    if (StringUtil.isEmpty(result)) {
                        MeterRequestResult result1 = new MeterRequestResult(true, 400);
                        EventBus.getDefault().post(result1);
                        return;
                    }
                    analysisData(result);
                }
                else {
                    MeterRequestResult result1 = new MeterRequestResult(true, 400);
                    EventBus.getDefault().post(result1);
                    return;
                }


            }
        }).start();
    }

    /**
     *
     * 获取公告栏消息
     * @return message
     */
    public String[] getMessage() {
        String[] message = null;
        messageUrlString = getMessageUrl();
        if (messageUrlString.isEmpty()) {
            return null;
        }

        Gson gson = new Gson();
        Map<String, String> response = HttpUtil.httpGet(messageUrlString, new HashMap<String, String>());
        JsonObject returnData = new JsonParser().parse(response.get("body")).getAsJsonObject();
        Message mMessage = gson.fromJson(returnData, Message.class);

        String messageNumber = String.format("消息(%d): ", mMessage.data.size());
        String firstMessage = messageNumber + mMessage.data.get(0).getTitle();
        String secondMessage = messageNumber + mMessage.data.get(1).getTitle();

        message = new String[]{firstMessage, secondMessage};
        return message;
    }

    /**
     * 解析数据
     *
     * @param result
     */
    private MeterRequestResult analysisData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has("code")) {
                int code = jsonObject.getInt("code");
                if (code != 200) {
                    MeterRequestResult result1 = new MeterRequestResult(true, code);
                    EventBus.getDefault().post(result1);
                    return result1;
                }
            }

            if (jsonObject.has("data")) {
                String data = jsonObject.getString("data");
                data = data.replace("null", "0");
                ArrayList<MererEntity> datas = (ArrayList<MererEntity>) JSON.parseArray(data, MererEntity.class);
                ArrayList<MererEntity> topData = new ArrayList<>();
                Iterator<MererEntity> iterator = datas.iterator();
                while (iterator.hasNext()) {
                    MererEntity entity = iterator.next();
                    if (entity.is_stick) {
                        topData.add(entity);
                        iterator.remove();
                    }
                }
                MeterRequestResult result1 = new MeterRequestResult(true, 200);
                result1.setTopDatas(topData);
                result1.setBodyDatas(datas);
                EventBus.getDefault().post(result1);
                return result1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            MeterRequestResult result1 = new MeterRequestResult(true, -1);
            EventBus.getDefault().post(result1);
        }
        MeterRequestResult result1 = new MeterRequestResult(true, 0);
        EventBus.getDefault().post(result1);
        return result1;
    }
}
