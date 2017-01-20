package com.tuxing.sdk.manager.impl;

import android.content.Context;
import com.tuxing.rpc.proto.*;
import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.db.entity.ContentItemGroup;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.ContentItemEvent;
import com.tuxing.sdk.event.ContentItemGroupEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.ContentManager;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.PbMsgUtils;
import com.tuxing.sdk.utils.SerializeUtils;
import de.greenrobot.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alan on 2015/7/6.
 */
public class ContentManagerImpl implements ContentManager {
    private final static Logger logger = LoggerFactory.getLogger(ContentManager.class);
    private static ContentManager instance;

    HttpClient httpClient = HttpClient.getInstance();
    private Context context;

    private ContentItem latestGroupItem;

    private ContentManagerImpl(){

    }

    public synchronized static ContentManager getInstance(){
        if(instance == null){
            instance = new ContentManagerImpl();
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
    public void getLatestGroupItems() {
        logger.debug("ContentManager::getLatestGroupItems()");


        final FetchPostGroupRequest request = new FetchPostGroupRequest.Builder()
                .sinceId(0L)
                .maxId(Long.MAX_VALUE)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_POST_GROUP, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchPostGroupResponse response = SerializeUtils.parseFrom(data, FetchPostGroupResponse.class);

                List<ContentItemGroup> itemGroupList = new ArrayList<>();

                for(PostGroup txGroup : response.postGroup){
                    ContentItemGroup itemGroup = new ContentItemGroup();
                    List<ContentItem> itemList = new ArrayList<>();

                    itemGroup.setGroupId(txGroup.id);
                    for(Post txItem : txGroup.post){
                        ContentItem item = PbMsgUtils.transObj(txItem);
                        item.setGroupId(txGroup.id);
                        itemList.add(item);
                    }
                    itemGroup.setItems(itemList);
                    itemGroupList.add(itemGroup);
                }

                if(!CollectionUtils.isEmpty(itemGroupList)){
                    ContentItemGroup latestGroup =  itemGroupList.get(itemGroupList.size() - 1);

                    if(!CollectionUtils.isEmpty(latestGroup.getItems())){
                        latestGroupItem = latestGroup.getItems().get(0);
                    }
                }

                EventBus.getDefault().post(new ContentItemGroupEvent(
                        ContentItemGroupEvent.EventType.FETCH_LATEST_ITEM_GROUP_SUCCESS,
                        null,
                        itemGroupList,
                        response.hasMore
                ));

                UserDbHelper.getInstance().saveContentItemGroups(itemGroupList);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ContentItemGroupEvent(
                        ContentItemGroupEvent.EventType.FETCH_LATEST_ITEM_GROUP_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getHistoryGroupItems(long maxItemId) {
        logger.debug("ContentManager::getHistoryGroupItems(), maxItemId={}", maxItemId);


        FetchPostGroupRequest request = new FetchPostGroupRequest.Builder()
                .sinceId(0L)
                .maxId(maxItemId)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_POST_GROUP, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchPostGroupResponse response = SerializeUtils.parseFrom(data, FetchPostGroupResponse.class);

                List<ContentItemGroup> itemGroupList = new ArrayList<>();

                for(PostGroup txGroup : response.postGroup){
                    ContentItemGroup itemGroup = new ContentItemGroup();
                    List<ContentItem> itemList = new ArrayList<>();

                    itemGroup.setGroupId(txGroup.id);
                    for(Post txItem : txGroup.post){
                        ContentItem item = PbMsgUtils.transObj(txItem);
                        item.setGroupId(txGroup.id);
                        itemList.add(item);
                    }
                    itemGroup.setItems(itemList);
                    itemGroupList.add(itemGroup);
                }

                EventBus.getDefault().post(new ContentItemGroupEvent(
                        ContentItemGroupEvent.EventType.FETCH_HISTORY_ITEM_GROUP_SUCCESS,
                        null,
                        itemGroupList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ContentItemGroupEvent(
                        ContentItemGroupEvent.EventType.FETCH_HISTORY_ITEM_GROUP_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getGroupItemsFromLocal() {
        logger.debug("ContentManager::getGroupItemsFromLocal()");


        List<ContentItemGroup> itemGroupList = UserDbHelper.getInstance().getLatestContentItemGroups();

        EventBus.getDefault().post(new ContentItemGroupEvent(
                ContentItemGroupEvent.EventType.FETCH_ITEM_GROUP_FROM_LOCAL,
                null,
                itemGroupList,
                false
        ));
    }

    @Override
    public void getLatestItems(Long deptId, final int contentType) {
        logger.debug("ContentManager::getLatestItems(), deptId={}, contentType={}", deptId, contentType);


        FetchPostRequest request = new FetchPostRequest.Builder()
                .gardenId(deptId)
                .postType(PbMsgUtils.transPostType(contentType))
                .sinceId(0L)
                .maxId(Long.MAX_VALUE)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_POST, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchPostResponse response = SerializeUtils.parseFrom(data, FetchPostResponse.class);

                List<ContentItem> itemList = new ArrayList<>();

                for(Post txItem : response.post){
                    ContentItem item = PbMsgUtils.transObj(txItem);
                    itemList.add(item);
                }

                EventBus.getDefault().post(new ContentItemEvent(
                        ContentItemEvent.EventType.FETCH_LATEST_ITEM_SUCCESS,
                        null,
                        itemList,
                        response.hasMore
                ));

                UserDbHelper.getInstance().saveContentItems(itemList, contentType);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ContentItemEvent(
                        ContentItemEvent.EventType.FETCH_LATEST_ITEM_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getHistoryItems(Long deptId, int contentType, long maxItemId) {
        logger.debug("ContentManager::getHistoryItems(), deptId={}, contentType={}, maxItemId={}",
                deptId, contentType, maxItemId);


        FetchPostRequest request = new FetchPostRequest.Builder()
                .gardenId(deptId)
                .postType(PbMsgUtils.transPostType(contentType))
                .sinceId(0L)
                .maxId(maxItemId)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_POST, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchPostResponse response = SerializeUtils.parseFrom(data, FetchPostResponse.class);

                List<ContentItem> itemList = new ArrayList<>();

                for(Post txItem : response.post){
                    ContentItem item = PbMsgUtils.transObj(txItem);
                    itemList.add(item);
                }

                EventBus.getDefault().post(new ContentItemEvent(
                        ContentItemEvent.EventType.FETCH_HISTORY_ITEM_SUCCESS,
                        null,
                        itemList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ContentItemEvent(
                        ContentItemEvent.EventType.FETCH_HISTORY_ITEM_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getItemsFromLocal(int contentType) {
        logger.debug("ContentManager::getLatestItems(), contentType={}", contentType);


        List<ContentItem> itemList = UserDbHelper.getInstance().getLatestContentItems(contentType);

        EventBus.getDefault().post(new ContentItemEvent(
                ContentItemEvent.EventType.FETCH_ITEM_FROM_LOCAL,
                null,
                itemList,
                false
        ));

    }

    @Override
    public void getContentHtml(long itemId, int contentType) {
        logger.debug("ContentManager::getContentHtml(), itemId={}, contentType={}", itemId, contentType);


        FetchPostDetailRequest request = new FetchPostDetailRequest.Builder()
                .postId(itemId)
                .postType(PbMsgUtils.transPostType(contentType))
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_POST_DETAIL, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchPostDetailResponse response = SerializeUtils.parseFrom(data, FetchPostDetailResponse.class);

                ContentItem item = PbMsgUtils.transObj(response.post);
                item.setHtml(response.postHtml);

                EventBus.getDefault().post(new ContentItemEvent(
                        ContentItemEvent.EventType.FETCH_CONTENT_SUCCESS,
                        null,
                        Arrays.asList(item),
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ContentItemEvent(
                        ContentItemEvent.EventType.FETCH_CONTENT_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void FetchGardenIntroContent() {
        logger.debug("ContentManager::FetchGardenIntroContent()");


        httpClient.sendRequest(RequestUrl.FETCH_GARDEN_INTRO, new byte[]{}, new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchGardenIntroResponse response = SerializeUtils.parseFrom(data, FetchGardenIntroResponse.class);

                ContentItem item = PbMsgUtils.transObj(response.post);
                item.setHtml(response.postHtml);

                EventBus.getDefault().post(new ContentItemEvent(
                        ContentItemEvent.EventType.FETCH_GARDEN_INTRO_SUCCESS,
                        null,
                        Arrays.asList(item),
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ContentItemEvent(
                        ContentItemEvent.EventType.FETCH_GARDEN_INTRO_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void FetchUserAgreement() {
        logger.debug("ContentManager::FetchUserAgreement()");


        httpClient.sendRequest(RequestUrl.FETCH_AGREEMENT, new byte[]{}, new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchAgreementResponse response = SerializeUtils.parseFrom(data, FetchAgreementResponse.class);

                ContentItem item = PbMsgUtils.transObj(response.post);
                item.setHtml(response.postHtml);

                EventBus.getDefault().post(new ContentItemEvent(
                        ContentItemEvent.EventType.FETCH_AGREEMENT_SUCCESS,
                        null,
                        Arrays.asList(item),
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ContentItemEvent(
                        ContentItemEvent.EventType.FETCH_AGREEMENT_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public ContentItem getLatestGroupItem() {
        if(latestGroupItem == null) {
            latestGroupItem = UserDbHelper.getInstance().getLatestOneGroupItem();
        }

        return latestGroupItem;
    }
}
