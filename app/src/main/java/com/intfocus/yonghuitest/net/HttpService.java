package com.intfocus.yonghuitest.net;

import com.intfocus.yonghuitest.data.request.RequestFavourite;
import com.intfocus.yonghuitest.data.response.BaseResult;
import com.intfocus.yonghuitest.data.response.article.ArticleResult;
import com.intfocus.yonghuitest.data.response.filter.MenuResult;
import com.intfocus.yonghuitest.data.response.home.HomeMsgResult;
import com.intfocus.yonghuitest.data.response.home.KpiResult;
import com.intfocus.yonghuitest.data.response.home.ReportListResult;
import com.intfocus.yonghuitest.data.response.home.WorkBoxResult;
import com.intfocus.yonghuitest.data.response.mine_page.UserInfoResult;
import com.intfocus.yonghuitest.data.response.notice.NoticesResult;
import com.intfocus.yonghuitest.data.response.scanner.StoreListResult;
import com.intfocus.yonghuitest.util.K;

import java.util.Map;

import retrofit2.http.Body;
import okhttp3.MultipartBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by CANC on 2017/7/31.
 */

public interface HttpService {

    @GET (K.KWorkBoxList)
    Observable<WorkBoxResult> getWorkBox(@Query("group_id") String groupId, @Query("role_id") String roleId);

    /**
     * 报表页面列表
     * @param groupId
     * @param roleId
     * @return
     */
    @GET (K.KReportList)
    Observable<ReportListResult> getReportList(@Query("group_id") String groupId, @Query("role_id") String roleId);

    /**
     * 门店列表
     * @param userNum
     * @return
     */
    @GET (K.KStoreList)
    Observable<StoreListResult> getStoreList(@Query("user_num") String userNum);

    /**
     * 用户信息
     *
     * @param userNum
     * @return
     */
    @GET (K.KUserInfo)
    Observable<UserInfoResult> getUserInfo(@Query("user_num") String userNum);

    /**
     * 获取概况页公告列表
     * /api/v1.1/user/notifications
     * @param groupId
     * @param roleId
     * @return
     */
    @GET (K.KNotifications)
    Observable<HomeMsgResult> getNotifications(@Query("group_id") String groupId, @Query("role_id") String roleId);

    /**
     * 扫码结果
     * {{host}}/api/v1.1/scan/barcode?api_token=123&store_id=123&code_info=123
     * @param storeId
     * @param codeInfo
     * @return
     */
    @GET (K.KScannerResult)
    Observable<BaseResult> getScannerResult(@Query("store_id") String storeId, @Query("code_info") String codeInfo);

    /**
     * 获取文章收藏列表
     *
     * @param queryMap
     * @return
     */
    @GET(K.KMyFavouritedList)
    Observable<ArticleResult> getMyFavouritedList(@QueryMap Map<String, String> queryMap);

    /**
     * 收藏状态
     *
     * @param requestFavourite
     * @return
     */
    @POST(K.KFavouriteStatus)
    Observable<BaseResult> articleOperating(@Body RequestFavourite requestFavourite);

    /**
     * 获取数据学院文章列表
     *
     * @param queryMap
     * @return
     */
    @GET(K.KArticlesList)
    Observable<ArticleResult> getArticleList(@QueryMap Map<String, String> queryMap);


    /**
     * 获取首页概况数据
     *
     * @param queryMap
     * @return
     */
    @GET(K.KOverview)
    Observable<KpiResult> getHomeIndex(@QueryMap Map<String, String> queryMap);

    /**
     * 获取首页消息数据
     *
     * @param queryMap
     * @return
     */
    @GET(K.KNotifications)
    Observable<HomeMsgResult> getHomeMsg(@QueryMap Map<String, String> queryMap);

    /**
     * 公告预警列表
     *
     * @param queryMap
     * @return
     */
    @GET(K.KNoticeList)
    Observable<NoticesResult> getNoticeList(@QueryMap Map<String, String> queryMap);

    /**
     * 获取筛选菜单信息
     *
     * @return
     */
    @GET(K.KFilterMenuPath)
    Observable<MenuResult> getFilterMenu();


    /**
     * 头像上传
     * @param deviceId
     * @param userId
     * @param file
     * @return
     */
    @Multipart
    @POST(K.kUserIconUploadPath)
    Observable<BaseResult> userIconUpload(@Path("deviceId") int deviceId, @Path("userId") int userId, @Part MultipartBody.Part file);
}
