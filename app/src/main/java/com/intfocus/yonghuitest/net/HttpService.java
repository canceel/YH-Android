package com.intfocus.yonghuitest.net;

import com.intfocus.yonghuitest.data.response.BaseResult;
import com.intfocus.yonghuitest.data.response.article.ArticleResult;
import com.intfocus.yonghuitest.data.response.filter.MenuResult;
import com.intfocus.yonghuitest.data.response.home.HomeMsgResult;
import com.intfocus.yonghuitest.data.response.home.KpiResult;
import com.intfocus.yonghuitest.data.response.notice.NoticesResult;
import com.intfocus.yonghuitest.login.bean.DeviceRequest;
import com.intfocus.yonghuitest.login.bean.Device;
import com.intfocus.yonghuitest.login.bean.NewUser;
import com.intfocus.yonghuitest.util.K;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
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

    /**
     * 获取文章收藏列表
     *
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @GET(K.KFavouriteArticlesPath)
    Observable<ArticleResult> getArticleList(@Path("userId") String userId, @Path("page") String page, @Path("pageSize") String pageSize);

    /**
     * 操作文章收藏
     *
     * @param userId
     * @param articleId
     * @param status
     * @return
     */
    @POST(K.KArticleCollectionPath)
    Observable<BaseResult> articleOperating(@Path("userId") String userId, @Path("articleId") String articleId, @Path("status") String status);

    /**
     * 获取数据学院文章列表
     *
     * @param userId
     * @param page
     * @param pageSize
     * @param keyWorld
     * @return
     */
    @GET(K.KInstituteListPath)
    Observable<ArticleResult> getArticleList(@Path("userId") String userId, @Path("page") String page, @Path("pageSize") String pageSize, @Query("keyword") String keyWorld);


    /**
     * 获取首页概况数据
     *
     * @param groupId
     * @param roleId
     * @return
     */
    @GET(K.kNewKPIApiDataPath)
    Observable<KpiResult> getHomeIndex(@Path("groupId") String groupId, @Path("roleId") String roleId);

    /**
     * 获取首页消息数据
     *
     * @param groupId
     * @param roleId
     * @return
     */
    @GET(K.kNewMsgDataMobilePath)
    Observable<HomeMsgResult> getHomeMsg(@Path("groupId") String groupId, @Path("roleId") String roleId, @Path("userId") String userId);

    /**
     * 公告预警
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

    @Multipart
    @POST(K.kUserIconUploadPath)
    Observable<BaseResult> userIconUpload(@Path("deviceId") int deviceId, @Path("userId") int userId, @Part MultipartBody.Part file);

    /**
     * 登录post请求
     * @param queryMap
     * @return
     */
    @POST(K.KNewLogin)
    Observable<NewUser> userLogin(@QueryMap Map<String, String> queryMap);

    /**
     * 上传设备信息
     * @param deviceRequest
     * @return
     */
    @POST(K.KNewDevice)
    Observable<Device> deviceUpLoad(@Body DeviceRequest deviceRequest);

    /**
     * 退出登录
     * @param user_device_id
     * @return
     */
    @POST(K.KNewLogout)
    Observable<BaseResult> userLogout(@Query("user_device_id") String user_device_id);
}
