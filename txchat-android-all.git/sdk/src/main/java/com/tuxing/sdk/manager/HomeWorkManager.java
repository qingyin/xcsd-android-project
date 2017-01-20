package com.tuxing.sdk.manager;

import com.tuxing.sdk.db.entity.GameLevel;
import com.tuxing.sdk.db.entity.HomeWorkRecord;

import java.util.List;

/**
 * Created by apple on 16/4/6.
 */
public interface HomeWorkManager extends BaseManager{
    //家长端接口***************************************begin

    /***
     * 从本地缓存中获取的作业列表，最多20条
     * 返回HomeworkEvent.EventType.HOMEWORK_LIST_FROM_LOCAL
     * 其中获取数据接口List<HomeWorkRecord> getHomeWorkRecords();和boolean getHasMore();
     */
    void getHomeWorkListFromLocal();

    void getHomeWorkDateils(long memberId);

    void generate_detail(long memberId,long classid);

    void get_game_list(long memberId,long classid);

    void send_unified(long memberId,long classid ,List<com.xcsd.rpc.proto.GameLevel> gameLevels);

    HomeWorkRecord getHomeWorkRecordLastOneFromLocal();

    /***
     * 获取最近的作业列表，最多20条
     * 获取成功，触发事件HomeworkEvent.EventType.HOMEWORK_LIST_LATEST_SUCCESS
     * 其中获取数据接口List<HomeWorkRecord> getHomeWorkRecords();和boolean getHasMore();
     * 获取失败，触发事件HomeworkEvent.EventType.HOMEWORK_LIST_LATEST_FAILED
     */
    void getLatestHomeWorkList();

    /***
     * 获取历史的作业列表，最多20条
     * 获取成功，触发事件HomeworkEvent.EventType.HOMEWORK_LIST_HISTORY_SUCCESS
     * 其中获取数据接口List<HomeWorkRecord> getHomeWorkRecords();和boolean getHasMore();
     * 获取失败，触发事件HomeworkEvent.EventType.HOMEWORK_LIST_HISTORY_FAILED
     */
    void getHistoryHomeWorkList(long maxId);

    /***
     * 读作业通知
     * 获取成功，触发事件HomeworkEvent.EventType.HOMEWORK_READ_NOTICE_SUCCESS
     * 其中获取数据接口 无
     * 获取失败，触发事件HomeworkEvent.EventType.HOMEWORK_READ_NOTICE_FAILED
     */
    void ReadHomeworkNoticeRequest(long homeworkNoticeId);

    /***
     * 删除作业通知
     * 获取成功，触发事件HomeworkEvent.EventType.HOMEWORK_DELETE_NOTICE_SUCCESS
     * 其中获取数据接口 无
     * 获取失败，触发事件HomeworkEvent.EventType.HOMEWORK_DELETE_NOTICE_FAILED
     */
    void DeleteHomeworkNoticeRequest(long homeworkNoticeId);

    /***
     * 获取作业排名（家长端，教师端）
     * 获取成功，触发事件HomeworkEvent.EventType.HOMEWORK_RANKING_SUCCESS
     * 其中获取数据接口List<HomeWorkUserRank> getHomeWorkUserRankList()
     * 获取失败，触发事件HomeworkEvent.EventType.HOMEWORK_RANKING_FAILED
     */
    void getHomeworkRankingFromParent(long childUserId);//家长端用
    void getHomeworkRankingFromTeacher(long classId);//教师端用
    void getHomeworkRankingFromTeacherLocation();//教师端用

    /***
     *获取本月作业情况统计月历
     * 获取成功，触发事件HomeworkEvent.EventType.HOMEWORK_CALENDAR_SUCCESS
     * 其中获取数据接口List<Integer> getFinishedList();和List<Integer> getUnfinishedList()
     * 获取失败，触发事件HomeworkEvent.EventType.HOMEWORK_CALENDAR_FAILED
     */
    void HomeworkCalendarRequest(long childUserId);

    //家长端接口***************************************end



    //教师端接口***************************************begin

    /***
     * 从本地缓存中获取的发送作业列表，最多20条
     * 返回HomeworkEvent.EventType.HOMEWORK_SENT_LIST_FROM_LOCAL
     * 其中获取数据接口List<HomeWorkClass> getHomeWorkClassList();和boolean getHasMore();
     */
    void getHomeWorkSendListFromLocal();

    /***
     * 获取最近的发送作业列表，最多20条
     * 获取成功，触发事件HomeworkEvent.EventType.HOMEWORK_SENT_LIST_LATEST_SUCCESS
     * 其中获取数据接口List<HomeWorkClass> getHomeWorkClassList();和boolean getHasMore();
     * 获取失败，触发事件HomeworkEvent.EventType.HOMEWORK_SENT_LIST_LATEST_FAILED
     */
    void getHomeWorkSendListLatest();

    /***
     * 获取历史的发送作业列表，最多20条
     * 获取成功，触发事件HomeworkEvent.EventType.HOMEWORK_SENT_LIST_HISTORY_SUCCESS
     * 其中获取数据接口List<HomeWorkClass> getHomeWorkClassList();和boolean getHasMore();
     * 获取失败，触发事件HomeworkEvent.EventType.HOMEWORK_SENT_LIST_HISTORY_FAILED
     */
    void getHomeWorkSendListHistory(long maxId);

    /***
     * 获取本地的作业成员列表，最多20条
     * 触发事件HomeworkEvent.EventType.HOMEWORK_MEMBERS_LIST_FROM_LOCAL
     * 其中获取数据接口List<HomeWorkMember> getHomeWorkMemberList();和boolean getHasMore();
     */
    void getHomeworkMemberListFromLacal(final long homeworkId);
    /***
     * 获取最新的作业成员列表，最多20条
     * 获取成功，触发事件HomeworkEvent.EventType.HOMEWORK_MEMBERS_LIST_LATEST_SUCCESS
     * 其中获取数据接口List<HomeWorkMember> getHomeWorkMemberList();和boolean getHasMore();
     * 获取失败，触发事件HomeworkEvent.EventType.HOMEWORK_MEMBERS_LIST_LATEST_FAILED
     */
    void getLatestHomeworkMemberList(final long homeworkId);
    /***
     * 获取历史的作业成员列表，最多20条
     * 获取成功，触发事件HomeworkEvent.EventType.HOMEWORK_MEMBERS_LIST_HISTORY_SUCCESS
     * 其中获取数据接口List<HomeWorkMember> getHomeWorkMemberList();和boolean getHasMore();
     * 获取失败，触发事件HomeworkEvent.EventType.HOMEWORK_MEMBERS_LIST_HISTORY_FAILED
     */
    void getHistoryHomeworkMemberList(final long homeworkId);

    /***
     * 生成定制作业
     * 获取成功，触发事件HomeworkEvent.EventType.HOMEWORK_GENERATE_SUCCESS
     * 其中获取数据接口List<HomeWorkGenerate> getHomeWorkGenerateList()
     * 获取失败，触发事件HomeworkEvent.EventType.HOMEWORK_GENERATE_FAILED
     */
    void GenerateHomeworkRequest(long classId);

    /***
     * 发送定制作业
     * 获取成功，触发事件HomeworkEvent.EventType.HOMEWORK_SEND_SUCCESS
     * 其中获取数据接口 无
     * 获取失败，触发事件HomeworkEvent.EventType.HOMEWORK_SEND_FAILED
     */
    void SendHomeworkRequest(long classId,int scope);

    /***
     * 获取可发送的作业数量
     * 获取成功，触发事件HomeworkEvent.EventType.HOMEWORK_REMAINING_COUNT_SUCCESS
     * 其中获取数据接口boolean isCustomizedStatus();和int getUnifiedCount()
     * 获取失败，触发事件HomeworkEvent.EventType.HOMEWORK_REMAINING_COUNT_FAILED
     */
    void HomeworkRemainingCountRequest(long classId);

    /***
     * 班级学习能力排行榜
     * 获取成功，触发事件HomeworkEvent.EventType.LEARNING_ABILITY_RANKING_SUCCESS
     * 其中获取数据接口List<HomeWorkUserRank> getHomeWorkUserRankList()
     * 获取失败，触发事件HomeworkEvent.EventType.LEARNING_ABILITY_RANKING_FAILED
     */
    void getClassAbilityRankingList(long classId);//教师端用

    //教师端接口***************************************end
}
