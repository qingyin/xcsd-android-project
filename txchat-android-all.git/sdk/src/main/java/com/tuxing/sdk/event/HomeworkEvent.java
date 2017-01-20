package com.tuxing.sdk.event;

import java.util.List;

import com.tuxing.sdk.db.entity.GameLevel;
import com.tuxing.sdk.db.entity.HomeWorkClass;
import com.tuxing.sdk.db.entity.HomeWorkDetail;
import com.tuxing.sdk.db.entity.HomeWorkGenerate;
import com.tuxing.sdk.db.entity.HomeWorkMember;
import com.tuxing.sdk.db.entity.HomeWorkRecord;
import com.tuxing.sdk.db.entity.HomeWorkUserRank;

/**
 * Created by apple on 16/4/7.
 */
public class HomeworkEvent extends BaseEvent {
    public enum EventType{
        HOMEWORK_GAME_LIST_SUCCESS,
        HOMEWORK_GAME_LIST_FAILED,

        HOMEWORK_GAME_SUCCESS,
        HOMEWORK_GAME_FAILED,

        HOMEWORK_SEND_GAME_SUCCESS,
        HOMEWORK_SEND_GAME_FAILED,

        HOMEWORK_DATEILS_LIST_HISTORY_SUCCESS,
        HOMEWORK_DATEILS_LIST_HISTORY_FAILED,

        HOMEWORK_LIST_FROM_LOCAL,
        HOMEWORK_LIST_LATEST_SUCCESS,
        HOMEWORK_LIST_LATEST_FAILED,
        HOMEWORK_LIST_HISTORY_SUCCESS,
        HOMEWORK_LIST_HISTORY_FAILED,

        HOMEWORK_READ_NOTICE_SUCCESS,
        HOMEWORK_READ_NOTICE_FAILED,

        HOMEWORK_DELETE_NOTICE_SUCCESS,
        HOMEWORK_DELETE_NOTICE_FAILED,

        HOMEWORK_RANKING_LOCTION_SUCCESS,

        HOMEWORK_RANKING_SUCCESS,
        HOMEWORK_RANKING_FAILED,

        HOMEWORK_CALENDAR_SUCCESS,
        HOMEWORK_CALENDAR_FAILED,


        HOMEWORK_SENT_LIST_FROM_LOCAL,
        HOMEWORK_SENT_LIST_LATEST_SUCCESS,
        HOMEWORK_SENT_LIST_LATEST_FAILED,
        HOMEWORK_SENT_LIST_HISTORY_SUCCESS,
        HOMEWORK_SENT_LIST_HISTORY_FAILED,
        HOMEWORK_SENT_LIST_SUCCESS,


        HOMEWORK_MEMBERS_LIST_FROM_LOCAL,
        HOMEWORK_MEMBERS_LIST_LATEST_SUCCESS,
        HOMEWORK_MEMBERS_LIST_LATEST_FAILED,
        HOMEWORK_MEMBERS_LIST_HISTORY_SUCCESS,
        HOMEWORK_MEMBERS_LIST_HISTORY_FAILED,

        HOMEWORK_GENERATE_SUCCESS,
        HOMEWORK_GENERATE_FAILED,

        HOMEWORK_SEND_SUCCESS,
        HOMEWORK_SEND_FAILED,

        HOMEWORK_REMAINING_COUNT_SUCCESS,
        HOMEWORK_REMAINING_COUNT_FAILED,

        LEARNING_ABILITY_RANKING_SUCCESS,
        LEARNING_ABILITY_RANKING_FAILED,

    }

    private EventType event;
    private List<HomeWorkRecord> homeWorkRecords;
    private List<HomeWorkClass> homeWorkClassList;
    private List<HomeWorkUserRank> homeWorkUserRankList;
    private List<HomeWorkMember> homeWorkMemberList;
    private List<HomeWorkGenerate> homeWorkGenerateList;
    private boolean hasMore;

    private HomeWorkDetail hwDetails;


    private List<GameLevel> glList;

    List<Integer> unfinishedList;
    List<Integer> finishedList;

    private boolean customizedStatus;		//定制作业是否可以发送
    private int unifiedCount;		//统一作业剩余数量

    public HomeworkEvent(EventType event, String msg) {
        super(msg);
        this.event = event;
    }

    public HomeworkEvent(EventType event, String msg,boolean customizedStatus,int unifiedCount) {
        super(msg);
        this.event = event;
        this.customizedStatus = customizedStatus;
        this.unifiedCount = unifiedCount;
    }
    public HomeworkEvent(EventType event, String msg,List<Integer> unfinishedList,List<Integer> finishedList) {
        super(msg);
        this.event = event;
        this.unfinishedList = unfinishedList;
        this.finishedList = finishedList;
    }
    public HomeworkEvent(EventType event, String msg, List<HomeWorkRecord> homeWorkRecords,boolean hasMore) {
        super(msg);
        this.event = event;
        this.homeWorkRecords = homeWorkRecords;
        this.hasMore = hasMore;
    }
    public HomeworkEvent(EventType event, String msg, List<HomeWorkClass> homeWorkClassList) {
        super(msg);
        this.event = event;
        this.homeWorkClassList = homeWorkClassList;
    }

    public HomeworkEvent(EventType event, String msg, HomeWorkDetail hwDetails) {
        super(msg);
        this.event = event;
        this.hwDetails = hwDetails;
    }

    public HomeworkEvent(EventType event, String msg, List<GameLevel> glList,int more) {
        super(msg);
        this.event = event;
        this.glList = glList;
    }

    public HomeworkEvent(EventType event, String msg, List<GameLevel> glList,int more,int b) {
        super(msg);
        this.event = event;
        this.glList = glList;
    }

    public EventType getEvent() {
        return event;
    }
    public List<HomeWorkRecord> getHomeWorkRecords() {
        return homeWorkRecords;
    }

    public boolean getHasMore(){
        return  hasMore;
    }
    public void setHasMore(boolean hasMore){
        this.hasMore = hasMore;
    }

    public List<HomeWorkClass> getHomeWorkClassList() {
        return homeWorkClassList;
    }

    public List<HomeWorkUserRank> getHomeWorkUserRankList() {
        return homeWorkUserRankList;
    }

    public void setHomeWorkUserRankList(List<HomeWorkUserRank> homeWorkUserRankList) {
        this.homeWorkUserRankList = homeWorkUserRankList;
    }

    public List<Integer> getFinishedList() {
        return finishedList;
    }
    public List<Integer> getUnfinishedList() {
        return unfinishedList;
    }


    public List<HomeWorkMember> getHomeWorkMemberList() {
        return homeWorkMemberList;
    }

    public void setHomeWorkMemberList(List<HomeWorkMember> homeWorkMemberList) {
        this.homeWorkMemberList = homeWorkMemberList;
    }


    public List<HomeWorkGenerate> getHomeWorkGenerateList() {
        return homeWorkGenerateList;
    }

    public void setHomeWorkGenerateList(List<HomeWorkGenerate> homeWorkGenerateList) {
        this.homeWorkGenerateList = homeWorkGenerateList;
    }


    public boolean isCustomizedStatus() {
        return customizedStatus;
    }

    public int getUnifiedCount() {
        return unifiedCount;
    }


    public HomeWorkDetail getHwDetails() {
        return hwDetails;
    }

    public void setHwDetails(HomeWorkDetail hwDetails) {
        this.hwDetails = hwDetails;
    }

    public List<GameLevel> getGlList() {
        return glList;
    }

    public void setGlList(List<GameLevel> glList) {
        this.glList = glList;
    }

}
