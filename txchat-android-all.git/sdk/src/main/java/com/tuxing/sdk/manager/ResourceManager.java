package com.tuxing.sdk.manager;

import com.tuxing.rpc.proto.SearchResultCategory;

/**
 * Created by alan on 16/1/6.
 */
public interface ResourceManager extends BaseManager{
    /***
     * 获得教育资源首页上的banner
     *
     * 请求成功, 返回ResourceBannerEvent.GET_RESOURCE_BANNER_SUCCESS, 包括有ResourceBanner的列表
     * 请求失败, 返回ResourceBannerEvent.GET_RESOURCE_BANNER_FAILED, 包括有错误信息
     */
    void getResourceBanners();

    /***
     * 获得教育资源分类
     *
     * 请求成功, 返回ResourceCategoryEvent.GET_RESOURCE_CATEGORY_SUCCESS, 包括有ResourceCategory的列表
     * 请求失败, 返回ResourceCategoryEvent.GET_RESOURCE_CATEGORY_FAILED, 包括有错误信息
     */
    void getResourceCategories();

    /***
     * 获取教育资源首页上显示的资源
     *
     * 请求成功, 返回HomePageResourceEvent.GET_HOME_PAGE_RESOURCE_SUCCESS, 包括有最热,最新,推荐的资源和内容提供商的列表
     * 请求成功, 返回HomePageResourceEvent.GET_HOME_PAGE_RESOURCE_FAILED, 包括有错误信息
     */
    void getHomePageResources();

    /***
     * 获取最热的资源
     * @param page 页码,从1开始
     *
     * 请求成功, 返回ResourceEvent.GET_HOT_RESOURCE_SUCCESS, 包括有资源列表
     * 请求成功, 返回ResourceEvent.GET_HOT_RESOURCE_FAILED, 包括有错误信息
     */
    void getHotResources(int page);

    /***
     * 获取资源列表,按时间排序
     *
     * 请求成功, 返回ResourceEvent.GET_LATEST_RESOURCE_SUCCESS, 包括有资源列表
     * 请求成功, 返回ResourceEvent.GET_LATEST_RESOURCE_FAILED, 包括有错误信息
     */
    void getLatestResources();

    /***
     * 获取资源列表,按时间排序
     * @param maxResId 上次返回的最大的资源ID
     *
     * 请求成功, 返回ResourceEvent.GET_HISTORY_RESOURCE_SUCCESS, 包括有资源列表
     * 请求成功, 返回ResourceEvent.GET_HISTORY_RESOURCE_FAILED, 包括有错误信息
     */
    void getHistoryResources(long maxResId);

    /***
     * 获取推荐的资源列表
     * @param page 页码,从1开始
     *
     * 请求成功, 返回ResourceEvent.GET_RECOMMENDED_RESOURCE_SUCCESS, 包括有资源列表
     * 请求成功, 返回ResourceEvent.GET_RECOMMENDED_RESOURCE_FAILED, 包括有错误信息
     */
    void getRecommendedResources(int page);

    /***
     * 根据资源更新的时间获取资源提供商列表
     * @param page 页码 从1开始
     *
     * 请求成功, 返回ProviderEvent.GET_LATEST_PROVIDER_SUCCESS, 包括有资源源提供商列表
     * 请求成功, 返回ProviderEvent.GET_LATEST_PROVIDER_FAILED, 包括有错误信息
     */
    void getLatestUpdateProvider(int page);

    /***
     * 获取最热和推荐的搜索关键字
     *
     * 请求成功, 返回SearchKeywordEvent.GET_SEARCH_KEYWORD_SUCCESS, 包括有最热和推荐的搜索关键字
     * 请求成功, 返回SearchKeywordEvent.GET_SEARCH_KEYWORD_FAILED, 包括有错误信息
     */
    void getSearchKeywords();

    /***
     * 获取搜索结果
     * @param category 结果的分类,是专辑,资源还是提供商
     * @param keyword 关键字
     * @param page 页码,从1开始
     *
     * 请求成功, 返回SearchResultEvent.GET_SEARCH_RESULT_SUCCESS, 包括有搜索结果
     * 请求成功, 返回SearchResultEvent.GET_SEARCH_RESULT_FAILED, 包括有错误信息
     */
    void getSearchResults(SearchResultCategory category, String keyword, int page);

    /***
     * 获取播放历史列表
     * @param page 页码,从1开始
     *
     * 请求成功, 返回ResourceEvent.GET_PLAY_HISTORY_SUCCESS, 包括有资源列表
     * 请求成功, 返回ResourceEvent.GET_PLAY_HISTORY_FAILED, 包括有错误信息
     */
    void getPlayHistoryList(int page);

    /***
     * 根据分类获取专辑列表
     * @param categoryId 分类ID
     *
     * 请求成功, 返回AlbumEvent.GET_LATEST_ALBUM_BY_CATEGORY_SUCCESS, 包括有专辑列表
     * 请求成功, 返回AlbumEvent.GET_LATEST_ALBUM_BY_CATEGORY_FAILED, 包括有错误信息
     */
    void getLatestAlbumByCategory(long categoryId);

    /***
     * 根据分类获取专辑列表
     * @param categoryId 分类ID
     * @param maxAlbumId 上次返回的最大的专辑ID
     *
     * 请求成功, 返回AlbumEvent.GET_HISTORY_ALBUM_BY_CATEGORY_SUCCESS, 包括有专辑列表
     * 请求成功, 返回AlbumEvent.GET_HISTORY_ALBUM_BY_CATEGORY_FAILED, 包括有错误信息
     */
    void getHistoryAlbumByCategory(long categoryId, long maxAlbumId);

    /***
     * 根据提供商获取专辑列表
     * @param providerId 提供商ID
     *
     * 请求成功, 返回AlbumEvent.GET_LATEST_ALBUM_BY_PROVIDER_SUCCESS, 包括有专辑列表
     * 请求成功, 返回AlbumEvent.GET_LATEST_ALBUM_BY_PROVIDER_FAILED, 包括有错误信息
     */
    void getLatestAlbumByProvider(long providerId);

    /***
     * 根据提供商获取专辑列表
     * @param providerId 提供商ID
     * @param maxAlbumId 上次返回的最大的专辑ID
     *
     * 请求成功, 返回AlbumEvent.GET_HISTORY_ALBUM_BY_PROVIDER_SUCCESS, 包括有专辑列表
     * 请求成功, 返回AlbumEvent.GET_HISTORY_ALBUM_BY_PROVIDER_FAILED, 包括有错误信息
     */
    void getHistoryAlbumByProvider(long providerId, long maxAlbumId);

    /***
     * 获取专辑中的资源列表
     * @param albumId 专辑ID
     * @param page 页码, 从1开始
     */
    void getResourcesByAlbum(long albumId, int page);

    /***
     * 播放下一首
     * @param resId 当前正在播放的资源ID
     * @param isPlayHistory 是否在播放播放历史中的资源
     *
     * 请求成功, 返回ResourceEvent.GET_NEXT_RESOURCE_SUCCESS
     * 请求成功, 返回ResourceEvent.GET_NEXT_RESOURCE_FAILED, 包括有错误信息
     */
    void playNext(long resId, boolean isPlayHistory);

    /***
     * 获取图片资源的开始几页
     * @param picResId 图片资源ID
     *
     * 请求成功, 返回PictureEvent.GET_HEAD_RESOURCE_PICTURE_SUCCESS, 包括有图片列表
     * 请求成功, 返回PictureEvent.GET_HEAD_RESOURCE_PICTURE_FAILED, 包括有错误信息
     */
    void getResourcePictures(long picResId);

    /***
     * 通知服务器客户端播放了某个资源
     * @param resId 播放的资源ID
     */
    void play(long resId);

    /***
     * 根据ID获取资源
     * @param resId 资源ID
     *
     * 请求成功, 返回ResourceEvent.GET_RESOURCE_BY_ID_SUCCESS, 包括有资源列表
     * 请求成功, 返回ResourceEvent.GET_RESOURCE_BY_ID_FAILED, 包括有错误信息
     */
    void getResourceById(long resId);

    /***
     * 根据ID获取服务商
     * @param providerId 服务商ID
     *
     * 请求成功, 返回ProviderEvent.GET_PROVIDER_BY_ID_SUCCESS, 包括有服务商列表
     * 请求成功, 返回ProviderEvent.GET_PROVIDER_BY_ID_FAILED, 包括有错误信息
     */
    void getProviderById(long providerId);

    /***
     * 获取资源前后几集的内容
     * @param resId 当前资源ID
     * @param count 返回的资源数量
     * @param forward true是向前取, false是向后取
     *
     * 请求成功, 返回ResourceEvent.GET_NEAR_RESOURCE_SUCCESS, 包括有资源列表
     * 请求成功, 返回ResourceEvent.GET_NEAR_RESOURCE_FAILED, 包括有错误信息
     */
    void getNearResource(long resId, int count, boolean forward);

    /***
     * 根据ID获取专辑
     * @param albumId 专辑ID
     *
     * 请求成功, 返回AlbumEvent.GET_ALBUM_BY_ID_SUCCESS, 包括有专辑列表
     * 请求成功, 返回AlbumEvent.GET_ALBUM_BY_ID_FAILED, 包括有错误信息
     */
    void getAlbumById(long albumId);
}
