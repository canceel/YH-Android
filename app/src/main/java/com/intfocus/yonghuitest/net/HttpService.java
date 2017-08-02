package com.intfocus.yonghuitest.net;

import com.intfocus.yonghuitest.data.response.BaseResult;
import com.intfocus.yonghuitest.data.response.article.ArticleResult;
import com.intfocus.yonghuitest.data.response.home.HomeMsgResult;
import com.intfocus.yonghuitest.data.response.home.KpiResult;
import com.intfocus.yonghuitest.data.response.notice.NoticesResult;
import com.intfocus.yonghuitest.util.K;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.POST;
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
     * @param userId
     * @param queryMap
     * @return
     */
    @GET(K.kNewNoticeListPath)
    Observable<NoticesResult> getNoticeList(@Path("userId") String userId, @QueryMap Map<String, String> queryMap);


}