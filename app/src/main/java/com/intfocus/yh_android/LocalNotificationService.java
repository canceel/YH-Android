package com.intfocus.yh_android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import com.intfocus.yh_android.util.FileUtil;
import com.intfocus.yh_android.util.HttpUtil;
import com.intfocus.yh_android.util.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lijunjie on 16/8/25.
 */
public class LocalNotificationService extends Service {
  private JSONObject notificationJSON;
  private JSONObject userJSON;
  private JSONObject pgyerJSON;
  private Timer timer;
  private TimerTask timerTask;
  private PackageInfo packageInfo;
  private String notificationPath, pgyerVersionPath, userConfigPath;
  private String kpiUrl, analyseUrl, appUrl, messageUrl, thursdaySayUrl;
  private String pgyerCode, versionCode;
  private int kpiCount, analyseCount, appCount, messageCount, updataCount, passwordCount, thursdaySayCount;
  private Context mContext;
  private Intent sendIntent;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mContext = this;

    notificationPath = FileUtil.dirPath(mContext, URLs.CACHED_DIRNAME, URLs.LOCAL_NOTIFICATION_FILENAME);
    userConfigPath = String.format("%s/%s", FileUtil.basePath(mContext), URLs.USER_CONFIG_FILENAME);
    pgyerVersionPath = String.format("%s/%s", FileUtil.basePath(mContext), URLs.PGYER_VERSION_FILENAME);

    //注册广播发送
    sendIntent = new Intent();
    sendIntent.setAction(DashboardActivity.ACTION_UPDATENOTIFITION);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    userJSON = FileUtil.readConfigFile(userConfigPath);
    pgyerJSON = FileUtil.readConfigFile(pgyerVersionPath);
    notificationJSON = FileUtil.readConfigFile(notificationPath);
    try {
      String currentUIVersion = URLs.currentUIVersion(mContext);
      kpiUrl = String.format(URLs.KPI_PATH, URLs.kBaseUrl, currentUIVersion, userJSON.getString("group_id"), userJSON.getString("role_id"));
      analyseUrl = String.format(URLs.ANALYSE_PATH, URLs.kBaseUrl, currentUIVersion, userJSON.getString("role_id"));
      appUrl = String.format(URLs.APPLICATION_PATH, URLs.kBaseUrl, currentUIVersion, userJSON.getString("role_id"));
      messageUrl = String.format(URLs.MESSAGE_PATH, URLs.kBaseUrl, currentUIVersion, userJSON.getString("role_id"), userJSON.getString("group_id"), userJSON.getString("user_id"));
      thursdaySayUrl = String.format(URLs.THURSDAY_SAY_PATH, URLs.kBaseUrl, currentUIVersion);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    notifitionTask();
    return super.onStartCommand(intent, flags, startId);
  }

  /*
   * 通知定时刷新任务,间隔 30 分钟发送一次广播
   */
  private void notifitionTask() {
    timer = new Timer();
    timerTask = new TimerTask() {
      @Override
      public void run() {
        processDataCount();//先计算通知的数量
        sendBroadcast(sendIntent);
      }
    };
    timer.schedule(timerTask, 0, 30 * 60 * 1000);
  }

  /*
   * 计算将要传递给 DashboardActivity 的通知数值
   */
  private void processDataCount() {
    try {
      kpiCount = getDataCount("tab_kpi", kpiUrl);
      analyseCount = getDataCount("tab_analyse", analyseUrl);
      appCount = getDataCount("tab_app", appUrl);
      messageCount = getDataCount("tab_message", messageUrl);
      thursdaySayCount = getDataCount("setting_thursday_say", thursdaySayUrl);

			/*
			 * 遍历获取 Tab 栏上需要显示的通知数量 ("tab_*" 的值)
			 */
      String[] typeString = {"tab_kpi", "tab_analyse", "tab_app", "tab_message", "setting_thursday_say"};
      int[] typeCount = {kpiCount, analyseCount, appCount, messageCount, thursdaySayCount};
      for (int i = 0; i < typeString.length; i++) {
        notificationJSON.put(typeString[i], Math.abs(typeCount[i] - notificationJSON.getInt(typeString[i] + "_last")));
        notificationJSON.put(typeString[i] + "_last", typeCount[i]);
      }

      if ((new File(pgyerVersionPath)).exists()) {
        pgyerJSON = FileUtil.readConfigFile(pgyerVersionPath);
        JSONObject responseData = pgyerJSON.getJSONObject("data");
        pgyerCode = responseData.getString("versionCode");
        packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        versionCode = String.valueOf(packageInfo.versionCode);
        updataCount = pgyerCode.equals(versionCode) ?  -1 : 1;
      }
      else {
        updataCount = -1;
      }

      passwordCount = userJSON.getString("password").equals(URLs.MD5(URLs.kInitPassword)) ? 1 : -1;
      notificationJSON.put("setting_password", passwordCount);
      notificationJSON.put("setting_pgyer", updataCount);

      int settingCount = (notificationJSON.getInt("setting_password") > 0 || notificationJSON.getInt("setting_pgyer") > 0 || notificationJSON.getInt("setting_thursday_say") > 0) ? 1 : 0;
      notificationJSON.put("setting", settingCount);

      FileUtil.writeFile(notificationPath, notificationJSON.toString());
    } catch (JSONException | IOException | PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }

  /*
   * 正则获取当前 DataCount，未获取到值则返回原数值
   */
  private int getDataCount(String keyName, String urlString) throws JSONException, IOException {
    Map<String, String> response = HttpUtil.httpGet(urlString, new HashMap<String, String>());
    int lastCount = notificationJSON.getInt(keyName + "_last");

    if (response.get("code").equals("200")) {
      String strRegex = "\\bMobileBridge.setDashboardDataCount.+";
      String countRegex = "\\d+";
      Pattern patternString = Pattern.compile(strRegex);
      Pattern patternCount = Pattern.compile(countRegex);
      Matcher matcherString = patternString.matcher(response.get("body"));
      matcherString.find();
      String str = matcherString.group();
      Matcher matcherCount = patternCount.matcher(str);
      if (matcherCount.find()) {
        int dataCount = Integer.parseInt(matcherCount.group());
				/*
				 * 如果tab_*_last 的值为 -1,表示第一次加载
				 */
        if (lastCount == -1) {
          notificationJSON.put(keyName + "_last", dataCount);
          notificationJSON.put(keyName, 1);
          FileUtil.writeFile(notificationPath, notificationJSON.toString());
        }
        return dataCount;
      } else {
        Log.i("notification", "未匹配到数值");
        return lastCount;
      }
    } else if (response.get("code").equals("304")) {
      Log.i("notification", "当前无通知");
      return lastCount;
    } else {
      Log.i("notification", "网络请求失败");
      return lastCount;
    }
  }
}