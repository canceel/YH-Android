package com.intfocus.yonghuitest.net;

import com.intfocus.yonghuitest.data.response.BaseResult;
import com.intfocus.yonghuitest.data.response.article.ArticleResult;
import com.intfocus.yonghuitest.util.K;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
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


}
