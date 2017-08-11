package com.intfocus.yonghuitest.util;

import java.io.Serializable;

/**
 * api 链接，宏
 *
 * @author jay
 * @version 1.0
 * @created 2016-01-06
 * Created by lijunjie on 16/9/22.
 */
public class K extends PrivateURLs implements Serializable {

  public final static Integer kTimerInterval = 30;

  /**
   *  API#paths
   */
  public final static String kUserAuthenticateAPIPath = "%s/api/v1/%s/%s/%s/authentication";
  public final static String kUserForgetAPIPath       = "%s/api/v1/:platform/reset_password";
  public final static String kReportDataAPIPath       = "%s/api/v1/group/%s/template/%s/report/%s/zip";
  public final static String kReportJsonDataAPIPath   = "%s/api/v1/group/%s/template/%s/report/%s/jzip";
  public final static String kReportJsonAPIPath       = "%s/api/v1/group/%s/template/%s/report/%s/json";
  public final static String kCommentAPIPath          = "%s/api/v1/user/%d/id/%d/type/%d";
  public final static String kScreenLockAPIPath       = "%s/api/v1/user_device/%s/screen_lock";
  public final static String kDeviceStateAPIPath      = "%s/api/v1/user_device/%d/state";
  public final static String kRsetPwdAPIPath          = "%s/api/v1/update/%s/password";
  public final static String kActionLogAPIPath        = "%s/api/v1/android/logger";
  public final static String kPushDeviceTokenAPIPath  = "%s/api/v1/device/%s/push_token/%s";
  public final static String kBarCodeScanAPIPath      = "%s/api/v1/group/%s/role/%s/user/%s/store/%s/barcode_scan?code_info=%s&code_type=%s";
  public final static String kBarCodeScanAPIViewPath  = "%s/mobile/v2/store/%s/barcode/%s/view";
  public final static String kBarCodeScanAPIDataPath  = "%s/mobile/v2/store/%s/barcode/%s/attachment";
  public final static String kDownloadAssetsAPIPath   = "%s/api/v1/download/%s.zip";
  public final static String kDeviceTokenAPIPath      = "%s/api/v1/user/%s/devices";
  public final static String kDeleteDeviceIdAPIPath   = "%s/api/v1/android/%s/logout";

  /**
   *  Mobile#View Path
   */
  public final static String kKPIMobileDataPath        = "%s/mobile/%s/data/group/%s/role/%s/kpi";
  public final static String kKPIApiDataPath           = "%s/api/v1/group/%s/role/%s/kpi";
  public final static String kMessageDataMobilePath    = "%s/api/v1/role/%s/group/%s/user/%s/message";
  public final static String kCommentMobilePath        = "%s/mobile/%s/id/%s/type/%s/comment";
  public final static String kThursdaySayMobilePath    = "%s/mobile/%s/thursday_say";
  public final static String kNewKPIApiDataPath        = "/api/v1/group/{groupId}/role/{roleId}/kpi";
  public final static String kNewMsgDataMobilePath     = "/api/v1/role/{roleId}/group/{groupId}/user/{userId}/message";

  /**
   * MinePage#API
   */
  public final static String KUserInfoPath             = "%s/api/v1/user/%s/group/%s/role/%s/statistics";
  public final static String kNoticeListPath           = "%s/api/v1/user/%s/notices?type=%s&page=%s&limit=%s";
  public final static String KAppListPath              = "%s/api/v1/group/%s/role/%s/apps";
  public final static String KWorkBoxListPath          = "%s/api/v1/group/%s/role/%s/app_covers";
  public final static String KReportsListPath          = "%s/api/v1/group/%s/role/%s/analyses";
  public final static String KInstituteListPath        = "/api/v1/user/{userId}/page/{page}/limit/{pageSize}/articles";
  public final static String KInstituteCollectionPath  = "%s/api/v1/user/%s/article/%s/favourite_status/%s";
  public final static String KFavouriteArticlesPath    = "/api/v1/user/{userId}/page/{page}/limit/{pageSize}/favourite_articles";
  public final static String KArticleCollectionPath    = "/api/v1/user/{userId}/article/{articleId}/favourite_status/{status}";
  public final static String kNewNoticeListPath        = "/api/v1/user/{userId}/notices";
  public final static String kUserIconUploadPath = "/api/v1/device/{deviceId}/upload/user/{userId}/gravatar";

  /**
   * 筛选
   */
  public final static String KFilterMenuPath           = "/api/v1/report/menus";

  /**
   *  Config#Application
   */
  public final static String kConfigDirName             = "Configs";
  public final static String kSharedDirName             = "Shared";
  public final static String kCachedDirName             = "Cached";
  public final static String kHTMLDirName               = "HTML";
  public final static String kAssetsDirName             = "Assets";
  public final static String kReportDataFileName        = "group_%s_template_%s_report_%s.js";
  public final static String kUserConfigFileName        = "user.json";
  public final static String kPushMessageFileName       = "push_message.json";
  public final static String kSettingConfigFileName     = "setting.json";
  public final static String kTabIndexConfigFileName    = "page_tab_index.json";
  public final static String kGesturePwdConfigFileName  = "gesture_password.json";
  public final static String kLocalNotificationConfigFileName = "local_notification.json";
  public final static String kCachedHeaderConfigFileName      = "cached_header.json";
  public final static String kPgyerVersionConfigFileName      = "pgyer_version.json";
  public final static String kGravatarConfigFileName          = "gravatar.json";
  public final static String kBetaConfigFileName              = "beta_v0.json";
  public final static String kBarCodeResultFileName           = "barcode_result.json";
  public final static String kScanBarCodeHTMLName             = "scan_bar_code.html";
  public final static String kCurrentVersionFileName          = "current_version.txt";
  public final static String kBehaviorConfigFileName          = "behavior.json";

  /**
   *  Config#User Model
   */
  public final static String kAppVersion = "app_version";
  public final static String kFontsMd5 = "fonts_md5";
  public final static String kImagesMd5 = "images_md5";
  public final static String kAssetsMd5 = "assets_md5";
  public final static String kIconsMd5 = "icons_md5";
  public final static String kStylesheetsMd5 = "stylesheets_md5";
  public final static String kJavaScriptsMd5 = "javascripts_md5";
  public final static String kInfo = "info";
  public final static String kValid = "valid";
  public final static String kUserId = "user_id";
  public final static String kUserName = "user_name";
  public final static String kUserDeviceId = "user_device_id";
  public final static String kCurrentUIVersion = "current_ui_version";

  /**
   * Config#Push Message
   */
  public final static String kPushConfigFileName = "push_message_config.json";
  public final static String kPushIsValid        = "push_valid";
  public final static String kPushDeviceToken    = "push_device_token";
  public final static String kPushDeviceUUID     = "device_uuid";

  /**
   * Config#User DeviceRequest Info
   */
  public final static int kMinSdkVersion = 14;
  public final static int kMaxSdkVersion = 25;

  /**
   * 新API及所需api key
   */
  public final static String API_TOKEN="api_token";
  public final static String ANDROID_API_KEY ="578905f6e0c4189caa344ee4b1e460e5";//加密所需api key
  public final static String KNoticeList ="/api/v1.1/my/notices";//公告列表
  public final static String KNewLogin = "/api/v1.1/user/authentication";
  public final static String KNewDevice = "/api/v1.1/app/device";
  public final static String KNewLogout = "/api/v1.1/user/logout";

}
