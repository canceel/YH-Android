package com.intfocus.yonghuitest.net;

import com.intfocus.yonghuitest.data.request.RequestFavourite;
import com.intfocus.yonghuitest.data.response.BaseResult;
import com.intfocus.yonghuitest.data.response.article.ArticleResult;
import com.intfocus.yonghuitest.data.response.filter.MenuResult;
import com.intfocus.yonghuitest.data.response.home.HomeMsgResult;
import com.intfocus.yonghuitest.data.response.home.KpiResult;
import com.intfocus.yonghuitest.data.response.mine_page.UserIconResult;
import com.intfocus.yonghuitest.data.response.notice.NoticesResult;
import com.intfocus.yonghuitest.util.K;

import java.util.Map;

import retrofit2.http.Body;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
     * 我的收藏列表
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

    @Multipart
    @POST(K.kUserIconUploadPath)
    Observable<BaseResult> userIconUpload(@Path("deviceId") int deviceId, @Path("userId") int userId, @Part MultipartBody.Part file);
}
