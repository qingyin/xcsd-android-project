package com.tuxing.sdk.manager;

import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.task.AsyncMethod;

/**
 * Created by Alan on 2015/7/5.
 */
public interface ContentManager extends BaseManager {

    /***
     * 获取最近的微学院文章，最多20条
     * 获取成功，触发事件ContentItemGroupEvent.EventType.FETCH_LATEST_ITEM_GROUP_SUCCESS
     * 其中包括了ContentItemGroup的List，ContentItemGroup中包括ContentItem的List
     * 获取失败，触发事件ContentItemGroupEvent.EventType.FETCH_LATEST_ITEM_GROUP_FAILED
     */
    void getLatestGroupItems();

    /***
     * 获取历史的微学院文章，最多20条
     * 获取成功，触发事件ContentItemGroupEvent.EventType.FETCH_HISTORY_ITEM_GROUP_SUCCESS
     * 其中包括了ContentItemGroup的List，ContentItemGroup中包括ContentItem的List
     * 获取失败，触发事件ContentItemGroupEvent.EventType.FETCH_HISTORY_ITEM_GROUP_FAILED
     */
    void getHistoryGroupItems(long maxItemId);

    /***
     * 从本地缓存中获取的微学院文章，最多20条
     * 返回ContentItemGroupEvent.EventType.FETCH_ITEM_GROUP_FROM_LOCAL
     * 其中包括了ContentItemGroup的List，ContentItemGroup中包括ContentItem的List
     */
    @AsyncMethod
    void getGroupItemsFromLocal();

    /***
     * 获取最近的公告，活动, 食谱等项目，最多20条
     * 获取成功，触发事件ContentItemEvent.EventType.FETCH_LATEST_ITEM_SUCCESS
     * 其中ContentItem的List
     * 获取失败，触发事件ContentItemEvent.EventType.FETCH_LATEST_ITEM_FAILED
     *
     * @param deptId        幼儿园ID
     * @param contentType   内容的类型，定义在Constants.CONTENT_TYPE中
     */
    void getLatestItems(Long deptId, int contentType);

    /***
     * 获取历史的公告，活动，食谱等项目，最多20条
     * 获取成功，触发事件ContentItemEvent.EventType.FETCH_HISTORY_ITEM_SUCCESS
     * 其中包括了ContentItem的List
     * 获取失败，触发事件ContentItemEvent.EventType.FETCH_HISTORY_ITEM_FAILED
     *
     * @param deptId        幼儿园ID
     * @param contentType   内容的类型，定义在Constants.CONTENT_TYPE中
     * @param maxItemId     上次返回的列表中最小的ID
     */
    void getHistoryItems(Long deptId, int contentType, long maxItemId);

    /***
     * 从本地缓存中获取公告，活动，食谱等项目，最多20条
     * 返回ContentItemEvent.EventType.FETCH_ITEM_FROM_LOCAL
     * 其中包括了ContentItemGroup的List，ContentItemGroup中包括ContentItem的List
     */
    @AsyncMethod
    void getItemsFromLocal(int contentType);

    /***
     * 获取ContentItem的Html内容
     * 获取成功，触发事件ContentItemEvent.EventType.FETCH_CONTENT_SUCCESS
     * 获取失败，触发事件ContentItemEvent.EventType.FETCH_CONTENT_FAILED
     *
     * @param itemId   ContentItem的ID
     * @param contentType   内容的类型，定义在Constants.CONTENT_TYPE中
     */
    void getContentHtml(long itemId, int contentType);

    /***
     * 获取幼儿园的简介
     * 获取成功，触发事件ContentItemEvent.EventType.FETCH_GARDEN_INTRO_SUCCESS
     * 获取失败，触发事件ContentItemEvent.EventType.FETCH_GARDEN_INTRO_FAILED
     */
    void FetchGardenIntroContent();

    /***
     * 获取用户协议
     * 获取成功，触发事件ContentItemEvent.EventType.FETCH_AGREEMENT_SUCCESS
     * 获取失败，触发事件ContentItemEvent.EventType.FETCH_AGREEMENT_FAILED
     */
    void FetchUserAgreement();

    /***
     * 获取最新的一条微学院信息
     *
     * @return 最新的一条微学院信息
     */
    ContentItem getLatestGroupItem();
}
