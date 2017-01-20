package com.tuxing.sdk.manager.impl;

import android.content.Context;
import com.squareup.wire.Wire;
import com.tuxing.rpc.proto.*;
import com.tuxing.rpc.proto.Comment;
import com.tuxing.sdk.db.entity.*;
import com.tuxing.sdk.event.CommentEvent;
import com.tuxing.sdk.event.resource.*;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.ResourceManager;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.SerializeUtils;
import de.greenrobot.dao.internal.LongHashMap;
import de.greenrobot.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alan on 16/1/6.
 */
public class ResourceManagerImpl implements ResourceManager {
    private final static Logger logger = LoggerFactory.getLogger(ResourceManager.class);
    private static ResourceManager instance = null;

    HttpClient httpClient = HttpClient.getInstance();

    private Context context;

    private ResourceManagerImpl(){

    }

    public synchronized static ResourceManager getInstance(){
        if(instance == null){
            instance = new ResourceManagerImpl();
            instance = AsyncTaskProxyFactory.getProxy(instance);
        }

        return instance;
    }

    @Override
    public void init(Context context) {
        this.context = context;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void getResourceBanners() {
        GetResourceBannersRequest request = new GetResourceBannersRequest();

        httpClient.sendRequest(RequestUrl.GET_RESOURCE_BANNER, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                GetResourceBannersResponse response = SerializeUtils.parseFrom(data, GetResourceBannersResponse.class);

                EventBus.getDefault().post(new ResourceBannerEvent(
                        ResourceBannerEvent.EventType.GET_RESOURCE_BANNER_SUCCESS,
                        null,
                        Wire.get(response.banners, new ArrayList<ResourceBanner>())));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ResourceBannerEvent(
                        ResourceBannerEvent.EventType.GET_RESOURCE_BANNER_FAILED,
                        cause.getMessage(),
                        null));
            }
        });
    }

    @Override
    public void getResourceCategories() {
        GetResourceCategoryRequest request = new GetResourceCategoryRequest();

        httpClient.sendRequest(RequestUrl.GET_RESOURCE_CATEGORY, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                GetResourceCategoryResponse response = SerializeUtils.parseFrom(data, GetResourceCategoryResponse.class);

                EventBus.getDefault().post(new ResourceCategoryEvent(
                        ResourceCategoryEvent.EventType.GET_RESOURCE_CATEGORY_SUCCESS,
                        null,
                        Wire.get(response.catogories, new ArrayList<ResourceCategory>())
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ResourceCategoryEvent(
                        ResourceCategoryEvent.EventType.GET_RESOURCE_CATEGORY_FAILED,
                        cause.getMessage(),
                        null
                ));
            }
        });
    }

    @Override
    public void getHomePageResources() {
        FetchHomePageResourcesRequest request = new FetchHomePageResourcesRequest();

        httpClient.sendRequest(RequestUrl.GET_HOME_PAGE_RESOURCE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchHomePageResourcesResponse response = SerializeUtils.parseFrom(data, FetchHomePageResourcesResponse.class);

                EventBus.getDefault().post(new HomePageResourceEvent(
                        HomePageResourceEvent.EventType.GET_HOME_PAGE_RESOURCE_SUCCESS,
                        null,
                        Wire.get(response.hot, new ArrayList<Resource>()),
                        Wire.get(response.latest, new ArrayList<Resource>()),
                        Wire.get(response.recommended, new ArrayList<Resource>()),
                        Wire.get(response.providers, new ArrayList<Provider>())));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomePageResourceEvent(
                        HomePageResourceEvent.EventType.GET_HOME_PAGE_RESOURCE_SUCCESS,
                        cause.getMessage(),
                        null,
                        null,
                        null,
                        null));
            }
        });
    }

    @Override
    public void getHotResources(int page) {
        final FetchHotResourceRequest request = new FetchHotResourceRequest(page);

        httpClient.sendRequest(RequestUrl.GET_HOT_RESOURCE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchHotResourceResponse response = SerializeUtils.parseFrom(data, FetchHotResourceResponse.class);

                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_HOT_RESOURCE_SUCCESS,
                        null,
                        Wire.get(response.resources, new ArrayList<Resource>()),
                        Wire.get(response.hasMore, false)));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_HOT_RESOURCE_FAILED,
                        cause.getMessage(),
                        null,
                        false));
            }
        });
    }

    @Override
    public void getLatestResources() {
        final FetchResourceRequest request = new FetchResourceRequest(0L, Long.MAX_VALUE);

        httpClient.sendRequest(RequestUrl.GET_RESOURCE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchResourceResponse response = SerializeUtils.parseFrom(data, FetchResourceResponse.class);

                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_LATEST_RESOURCE_SUCCESS,
                        null,
                        Wire.get(response.resources, new ArrayList<Resource>()),
                        Wire.get(response.hasMore, false)));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_LATEST_RESOURCE_FAILED,
                        cause.getMessage(),
                        null,
                        false));
            }
        });
    }

    @Override
    public void getHistoryResources(long maxResId) {
        final FetchResourceRequest request = new FetchResourceRequest(0L, maxResId);

        httpClient.sendRequest(RequestUrl.GET_RESOURCE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchResourceResponse response = SerializeUtils.parseFrom(data, FetchResourceResponse.class);

                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_HISTORY_RESOURCE_SUCCESS,
                        null,
                        Wire.get(response.resources, new ArrayList<Resource>()),
                        Wire.get(response.hasMore, false)));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_HISTORY_RESOURCE_FAILED,
                        cause.getMessage(),
                        null,
                        false));
            }
        });
    }

    @Override
    public void getRecommendedResources(int page) {
        final FetchRecommendedResourceRequest request = new FetchRecommendedResourceRequest(page);

        httpClient.sendRequest(RequestUrl.GET_RECOMMENDED_RESOURCE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchRecommendedResourceResponse response = SerializeUtils.parseFrom(data, FetchRecommendedResourceResponse.class);

                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_RECOMMEND_RESOURCE_SUCCESS,
                        null,
                        Wire.get(response.resources, new ArrayList<Resource>()),
                        Wire.get(response.hasMore, false)));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_RECOMMEND_RESOURCE_FAILED,
                        cause.getMessage(),
                        null,
                        false));
            }
        });
    }

    @Override
    public void getLatestUpdateProvider(int page) {
        FetchProviderByUpdateRequest request = new FetchProviderByUpdateRequest(page);

        httpClient.sendRequest(RequestUrl.GET_PROVIDER_BY_UPDATE_TIME, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchProviderByUpdateResponse response = SerializeUtils.parseFrom(data, FetchProviderByUpdateResponse.class);

                EventBus.getDefault().post(new ProviderEvent(
                        ProviderEvent.EventType.GET_LATEST_UPDATE_PROVIDER_SUCCESS,
                        null,
                        Wire.get(response.provides, new ArrayList<Provider>()),
                        Wire.get(response.hasMore, false)
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ProviderEvent(
                        ProviderEvent.EventType.GET_LATEST_UPDATE_PROVIDER_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getSearchKeywords() {
        FetchSearchKeywordsRequest request = new FetchSearchKeywordsRequest();

        httpClient.sendRequest(RequestUrl.GET_SEARCH_KEYWORD, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchSearchKeywordsResponse response = SerializeUtils.parseFrom(data, FetchSearchKeywordsResponse.class);

                EventBus.getDefault().post(new SearchKeywordEvent(
                        SearchKeywordEvent.EventType.GET_SEARCH_KEYWORD_SUCCESS,
                        null,
                        response.hot,
                        response.recommended
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new SearchKeywordEvent(
                        SearchKeywordEvent.EventType.GET_SEARCH_KEYWORD_FAILED,
                        cause.getMessage(),
                        null,
                        null
                ));
            }
        });
    }

    @Override
    public void getSearchResults(final SearchResultCategory category, String keyword, int page) {
        SearchResourceRequest request = new SearchResourceRequest(keyword, page, category);

        httpClient.sendRequest(RequestUrl.GET_SEARCH_RESULT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                SearchResourceResponse response = SerializeUtils.parseFrom(data, SearchResourceResponse.class);

                EventBus.getDefault().post(new SearchResultEvent(
                        SearchResultEvent.EventType.GET_SEARCH_RESULT_SUCCESS,
                        null,
                        response.resources,
                        response.albums,
                        response.providers,
                        category
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new SearchResultEvent(
                        SearchResultEvent.EventType.GET_SEARCH_RESULT_FAILED,
                        cause.getMessage(),
                        null,
                        null,
                        null,
                        category
                ));
            }
        });
    }

    @Override
    public void getPlayHistoryList(int page) {
        FetchPlayHistoryRequest request = new FetchPlayHistoryRequest(page);

        httpClient.sendRequest(RequestUrl.GET_PLAY_HISTORY, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchPlayHistoryResponse response = SerializeUtils.parseFrom(data, FetchPlayHistoryResponse.class);

                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_PLAY_HISTORY_SUCCESS,
                        null,
                        Wire.get(response.resources, new ArrayList<Resource>()),
                        Wire.get(response.hasMore, false)));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_PLAY_HISTORY_FAILED,
                        cause.getMessage(),
                        null,
                        false));
            }
        });
    }

    @Override
    public void getLatestAlbumByCategory(final long categoryId) {
        FetchAlbumByCategoryRequest request = new FetchAlbumByCategoryRequest(categoryId, 0L, Long.MAX_VALUE);

        httpClient.sendRequest(RequestUrl.GET_ALBUM_BY_CATEGORY, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchAlbumByCategoryResponse response = SerializeUtils.parseFrom(data, FetchAlbumByCategoryResponse.class);

                EventBus.getDefault().post(new AlbumEvent(
                        AlbumEvent.EventType.GET_LATEST_ALBUM_BY_CATEGORY_SUCCESS,
                        null,
                        Wire.get(response.albums, new ArrayList<Album>()),
                        Wire.get(response.hasMore, false)
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new AlbumEvent(
                        AlbumEvent.EventType.GET_LATEST_ALBUM_BY_CATEGORY_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getHistoryAlbumByCategory(long categoryId, long maxAlbumId) {
        FetchAlbumByCategoryRequest request = new FetchAlbumByCategoryRequest(categoryId, 0L, maxAlbumId);

        httpClient.sendRequest(RequestUrl.GET_ALBUM_BY_CATEGORY, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchAlbumByCategoryResponse response = SerializeUtils.parseFrom(data, FetchAlbumByCategoryResponse.class);

                EventBus.getDefault().post(new AlbumEvent(
                        AlbumEvent.EventType.GET_HISTORY_ALBUM_BY_CATEGORY_SUCCESS,
                        null,
                        Wire.get(response.albums, new ArrayList<Album>()),
                        Wire.get(response.hasMore, false)
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new AlbumEvent(
                        AlbumEvent.EventType.GET_HISTORY_ALBUM_BY_CATEGORY_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getLatestAlbumByProvider(long providerId) {
        FetchAlbumByProviderRequest request = new FetchAlbumByProviderRequest(providerId, 0L, Long.MAX_VALUE);

        httpClient.sendRequest(RequestUrl.GET_ALBUM_BY_PROVIDER, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchAlbumByProviderResponse response = SerializeUtils.parseFrom(data, FetchAlbumByProviderResponse.class);

                EventBus.getDefault().post(new AlbumEvent(
                        AlbumEvent.EventType.GET_LATEST_ALBUM_BY_PROVIDER_SUCCESS,
                        null,
                        Wire.get(response.albums, new ArrayList<Album>()),
                        Wire.get(response.hasMore, false)
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new AlbumEvent(
                        AlbumEvent.EventType.GET_LATEST_ALBUM_BY_PROVIDER_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getHistoryAlbumByProvider(long providerId, long maxAlbumId) {
        FetchAlbumByProviderRequest request = new FetchAlbumByProviderRequest(providerId, 0L, maxAlbumId);

        httpClient.sendRequest(RequestUrl.GET_ALBUM_BY_PROVIDER, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchAlbumByProviderResponse response = SerializeUtils.parseFrom(data, FetchAlbumByProviderResponse.class);

                EventBus.getDefault().post(new AlbumEvent(
                        AlbumEvent.EventType.GET_HISTORY_ALBUM_BY_PROVIDER_SUCCESS,
                        null,
                        Wire.get(response.albums, new ArrayList<Album>()),
                        Wire.get(response.hasMore, false)
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new AlbumEvent(
                        AlbumEvent.EventType.GET_HISTORY_ALBUM_BY_PROVIDER_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getResourcesByAlbum(long albumId, int page) {
        FetchResourceByAlbumRequest request = new FetchResourceByAlbumRequest(albumId, page);

        httpClient.sendRequest(RequestUrl.GET_RESOURCE_BY_ALBUM, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchResourceByAlbumResponse response = SerializeUtils.parseFrom(data, FetchResourceByAlbumResponse.class);

                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_RESOURCE_BY_ALBUM_SUCCESS,
                        null,
                        Wire.get(response.resources, new ArrayList<Resource>()),
                        Wire.get(response.hasMore, false)
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_RESOURCE_BY_ALBUM_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void playNext(long resId, boolean isPlayHistory) {
        PlayNextRequest request = new PlayNextRequest(resId, isPlayHistory);

        httpClient.sendRequest(RequestUrl.PLAY_NEXT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                PlayNextResponse response = SerializeUtils.parseFrom(data, PlayNextResponse.class);

                List<Resource> resources = new ArrayList<>();

                if(response.resource != null){
                    resources.add(response.resource);
                }

                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_NEXT_RESOURCE_SUCCESS,
                        null,
                        resources,
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_NEXT_RESOURCE_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getResourcePictures(long picResId) {
        final FetchResourcePicturesRequest request = new FetchResourcePicturesRequest(picResId);

        httpClient.sendRequest(RequestUrl.GET_RESOURCE_PICTURE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchResourcePicturesResponse response = SerializeUtils.parseFrom(data, FetchResourcePicturesResponse.class);

                EventBus.getDefault().post(new PictureEvent(
                        PictureEvent.EventType.GET_RESOURCE_PICTURE_SUCCESS,
                        null,
                        Wire.get(response.pictures, new ArrayList<String>())
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new PictureEvent(
                        PictureEvent.EventType.GET_RESOURCE_PICTURE_FAILED,
                        cause.getMessage(),
                        null
                ));
            }
        });
    }

    @Override
    public void play(long resId) {
        PlayResourceRequest request = new PlayResourceRequest(resId);

        httpClient.sendRequest(RequestUrl.PLAY_RESOURCE, request.toByteArray(), null);
    }

    @Override
    public void getResourceById(long resId) {
        FetchResourceByIdRequest request = new FetchResourceByIdRequest(resId);

        httpClient.sendRequest(RequestUrl.GET_RESOURCE_BY_ID, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchResourceByIdResponse response = SerializeUtils.parseFrom(data, FetchResourceByIdResponse.class);

                List<Resource> resources = response.resource == null ? new ArrayList<Resource>() :
                        CollectionUtils.asList(response.resource);

                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_RESOURCE_BY_ID_SUCCESS,
                        null,
                        resources,
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_RESOURCE_BY_ID_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getProviderById(long providerId) {
        FetchProviderByIdRequest request = new FetchProviderByIdRequest(providerId);

        httpClient.sendRequest(RequestUrl.GET_PROVIDER_BY_ID, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchProviderByIdResponse response = SerializeUtils.parseFrom(data, FetchProviderByIdResponse.class);

                List<Provider> providers = response.provider == null ? new ArrayList<Provider>() :
                        CollectionUtils.asList(response.provider);

                EventBus.getDefault().post(new ProviderEvent(
                        ProviderEvent.EventType.GET_PROVIDER_BY_ID_SUCCESS,
                        null,
                        providers,
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ProviderEvent(
                        ProviderEvent.EventType.GET_PROVIDER_BY_ID_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getNearResource(long resId, int count, boolean forward) {
        FetchNearResourcesRequest request = new FetchNearResourcesRequest(resId, count, forward);

        httpClient.sendRequest(RequestUrl.GET_NEAR_RESOURCE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchNearResourcesResponse response =  SerializeUtils.parseFrom(data, FetchNearResourcesResponse.class);

                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_NEAR_RESOURCE_SUCCESS,
                        null,
                        Wire.get(response.resource, new ArrayList<Resource>()),
                        Wire.get(response.hasMore, false)
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ResourceEvent(
                        ResourceEvent.EventType.GET_NEAR_RESOURCE_SUCCESS,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getAlbumById(long albumId) {
        FetchAlbumByIdRequest request = new FetchAlbumByIdRequest(albumId);

        httpClient.sendRequest(RequestUrl.GET_ALBUM_BY_ID, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchAlbumByIdResponse response = SerializeUtils.parseFrom(data, FetchAlbumByIdResponse.class);

                List<Album> albums = response.album == null ? new ArrayList<Album>() :
                        CollectionUtils.asList(response.album);

                EventBus.getDefault().post(new AlbumEvent(
                        AlbumEvent.EventType.GET_ALBUM_BY_ID_SUCCESS,
                        null,
                        albums,
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new AlbumEvent(
                        AlbumEvent.EventType.GET_ALBUM_BY_ID_SUCCESS,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }
}
