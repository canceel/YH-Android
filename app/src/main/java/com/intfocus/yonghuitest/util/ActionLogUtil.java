package com.intfocus.yonghuitest.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.intfocus.yonghuitest.util.K.kAppVersion;
import static com.intfocus.yonghuitest.util.K.kUserName;

/**
 * Created by liuruilin on 2017/8/1.
 */

public class ActionLogUtil {

    /**
     * 上传锁屏信息
     *
     * @param state    是否启用锁屏
     * @param deviceID 设备标识
     * @param password 锁屏密码
     */
    public static void screenLock(String deviceID, String password, boolean state) {
        String urlString = String.format(K.kScreenLockAPIPath, K.kBaseUrl, deviceID);

        Map<String, String> params = new HashMap<>();
        params.put("screen_lock_state", "1");
        params.put("screen_lock_type", "4位数字");
        params.put("screen_lock", password);

        HttpUtil.httpPost(urlString, params);
    }

    /**
     * 上传用户行为
     *
     * @param context 上下文
     * @param param   用户行为
     */
    public static void actionLog(final Context context, final JSONObject param) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SharedPreferences mUserSP = context.getApplicationContext().getSharedPreferences("UserBean", MODE_PRIVATE);

                    param.put(K.kUserId, mUserSP.getString(K.kUserId, ""));
                    param.put(URLs.kUserNum, mUserSP.getString(URLs.kUserNum,""));
                    param.put(kUserName, mUserSP.getString(K.kUserName, ""));
                    param.put(K.kUserDeviceId, mUserSP.getString(K.kUserDeviceId, ""));

                    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    param.put(kAppVersion, String.format("a%s", packageInfo.versionName));
                    param.put("coordinate", mUserSP.getString("coordinate", ""));

                    JSONObject params = new JSONObject();
                    params.put("action_log", param);

                    JSONObject userParams = new JSONObject();
                    userParams.put(kUserName, mUserSP.getString(kUserName, ""));
                    userParams.put("user_pass", mUserSP.getString(URLs.kPassword, ""));
                    params.put("user", userParams);

                    String urlString = String.format(K.kActionLogAPIPath, K.kBaseUrl);
                    HttpUtil.httpPost(urlString, params);
                } catch (JSONException | PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 上传登录失败行为
     *
     * @param context 上下文
     * @param param   用户行为
     */
    public static void actionLoginLog(final Context context, final JSONObject param) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SharedPreferences mUserSP = context.getApplicationContext().getSharedPreferences("UserBean", MODE_PRIVATE);

                    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    param.put(kAppVersion, String.format("a%s", packageInfo.versionName));
                    param.put("coordinate", mUserSP.getString("coordinate", ""));

                    JSONObject params = new JSONObject();
                    params.put("action_log", param);

                    Log.i("logger", params.toString());
                    String urlString = String.format(K.kActionLogAPIPath, K.kBaseUrl);
                    HttpUtil.httpPost(urlString, params);
                } catch (JSONException | PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 消息推送， 设备标识
     *
     * @param deviceUUID 设备ID
     * @return 服务器是否更新成功
     */
    public static boolean pushDeviceToken(Context context, String deviceUUID) {
        try {
            String pushConfigPath = String.format("%s/%s", FileUtil.basePath(context), K.kPushConfigFileName);
            JSONObject pushJSON = FileUtil.readConfigFile(pushConfigPath);

            if (!pushJSON.has(K.kPushDeviceToken) || pushJSON.getString(K.kPushDeviceToken).length() != 44)
                return false;
            if (pushJSON.has(K.kPushIsValid) && pushJSON.getBoolean(K.kPushIsValid)) return true;

            /**
             *  必须符合以下两条件:
             *  1. device_token 存在并且长度为 44
             *  2. is_valid = false
             */
            String urlString = String.format(K.kPushDeviceTokenAPIPath, K.kBaseUrl, deviceUUID, pushJSON.getString(K.kPushDeviceToken));
            Map<String, String> response = HttpUtil.httpPost(urlString, new JSONObject());
            JSONObject responseJSON = new JSONObject(response.get(URLs.kBody));

            pushJSON.put(K.kPushIsValid, responseJSON.has(K.kValid) && responseJSON.getBoolean(K.kValid));
            pushJSON.put(K.kPushDeviceUUID, deviceUUID);
            FileUtil.writeFile(pushConfigPath, pushJSON.toString());

            return pushJSON.has(K.kPushIsValid) && pushJSON.getBoolean(K.kPushIsValid);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
