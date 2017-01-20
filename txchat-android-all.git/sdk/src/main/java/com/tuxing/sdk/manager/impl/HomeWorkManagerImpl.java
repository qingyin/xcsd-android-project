package com.tuxing.sdk.manager.impl;

import android.content.Context;

import com.tuxing.sdk.db.entity.GameLevel;
import com.tuxing.sdk.db.entity.HomeWorkClass;
import com.tuxing.sdk.db.entity.HomeWorkDetail;
import com.tuxing.sdk.db.entity.HomeWorkGenerate;
import com.tuxing.sdk.db.entity.HomeWorkMember;
import com.tuxing.sdk.db.entity.HomeWorkRecord;
import com.tuxing.sdk.db.entity.HomeWorkUserRank;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.HomeworkEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.CounterManager;
import com.tuxing.sdk.manager.HomeWorkManager;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.PbMsgUtils;
import com.tuxing.sdk.utils.SerializeUtils;

import com.xcsd.rpc.proto.ClassAbilityRankingRequest;
import com.xcsd.rpc.proto.ClassAbilityRankingResponse;
import com.xcsd.rpc.proto.ClassHomework;
import com.xcsd.rpc.proto.DeleteHomeworkNoticeRequest;
import com.xcsd.rpc.proto.DeleteHomeworkNoticeResponse;
import com.xcsd.rpc.proto.GameListRequest;
import com.xcsd.rpc.proto.GameListResponse;
import com.xcsd.rpc.proto.GenerateHomeworkRequest;
import com.xcsd.rpc.proto.GenerateHomeworkResponse;
import com.xcsd.rpc.proto.Homework;
import com.xcsd.rpc.proto.HomeworkCalendarRequest;
import com.xcsd.rpc.proto.HomeworkCalendarResponse;
import com.xcsd.rpc.proto.HomeworkDetailRequest;
import com.xcsd.rpc.proto.HomeworkDetailResponse;
import com.xcsd.rpc.proto.HomeworkGenerateDetailRequest;
import com.xcsd.rpc.proto.HomeworkGenerateDetailResponse;
import com.xcsd.rpc.proto.HomeworkListRequest;
import com.xcsd.rpc.proto.HomeworkListResponse;
import com.xcsd.rpc.proto.HomeworkMember;
import com.xcsd.rpc.proto.HomeworkMemberListRequest;
import com.xcsd.rpc.proto.HomeworkMemberListResponse;
import com.xcsd.rpc.proto.HomeworkRankingRequest;
import com.xcsd.rpc.proto.HomeworkRankingResponse;
import com.xcsd.rpc.proto.HomeworkRemainingCountRequest;
import com.xcsd.rpc.proto.HomeworkRemainingCountResponse;
import com.xcsd.rpc.proto.HomeworkSentListRequest;
import com.xcsd.rpc.proto.HomeworkSentListResponse;
import com.xcsd.rpc.proto.ReadHomeworkNoticeRequest;
import com.xcsd.rpc.proto.ReadHomeworkNoticeResponse;
import com.xcsd.rpc.proto.SendHomeworkRequest;
import com.xcsd.rpc.proto.SendHomeworkResponse;
import com.xcsd.rpc.proto.SendUnifiedHomeworkRequest;
import com.xcsd.rpc.proto.SendUnifiedHomeworkResponse;
import com.xcsd.rpc.proto.StudentScope;
import com.xcsd.rpc.proto.UserRank;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by apple on 16/4/6.
 */
public class HomeWorkManagerImpl implements HomeWorkManager {
    private final static Logger logger = LoggerFactory.getLogger(HomeWorkManager.class);
    private static HomeWorkManager instance;
    HttpClient httpClient = HttpClient.getInstance();
    private Context context;


    CounterManager counterManager = CounterManagerImpl.getInstance();

    private HomeWorkManagerImpl() {
    }

    public synchronized static HomeWorkManager getInstance() {
        if (instance == null) {
            instance = new HomeWorkManagerImpl();
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
    public void getHomeWorkListFromLocal() {
        List<HomeWorkRecord> hwRecord = UserDbHelper.getInstance().getLatestHomeWorkRecordList();
        EventBus.getDefault().post(new HomeworkEvent(
                HomeworkEvent.EventType.HOMEWORK_LIST_FROM_LOCAL,
                null,
                hwRecord,
                false
        ));
    }

    @Override
    public void getHomeWorkDateils(long memberId) {
        logger.debug("HomeWorkManagerImpl::getHomeWorkDateils()");

        HomeworkDetailRequest.Builder req = new HomeworkDetailRequest.Builder();
        req.memberId(memberId);

        httpClient.sendRequest(RequestUrl.HOMEWORK_DETAILS, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {

                HomeworkDetailResponse res = SerializeUtils.parseFrom(data, HomeworkDetailResponse.class);

                HomeWorkDetail hwDetail = new HomeWorkDetail(null);
                hwDetail.setMemberId(res.memberId);
                hwDetail.setStatus(res.status.getValue());
                hwDetail.setTitle(res.title);
                hwDetail.setDescription(res.description);
                hwDetail.setSenderName(res.senderName);
                hwDetail.setTotalScore(res.totalScore);
                hwDetail.setMaxScore(res.maxScore);
                hwDetail.setSendTime(res.sendTime);
                hwDetail.setChildUserId(res.childUserId);
                List<GameLevel> glList = new ArrayList<>();
                for (com.xcsd.rpc.proto.GameLevel gl : res.gameLevels) {
                    GameLevel temp = new GameLevel(null, gl.gameId, gl.level, gl.gameName, gl.abilityName, gl.picUrl, gl.stars,gl.color,gl.abilityId+"",gl.hasGuide);
                    glList.add(temp);
                }
                //hwDetail.setGameLevelList(glList);
                HomeworkEvent he = new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_DATEILS_LIST_HISTORY_SUCCESS,
                        null,
                        hwDetail
                );
                he.setGlList(glList);
                EventBus.getDefault().post(he);
            }


            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_DATEILS_LIST_HISTORY_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }

    @Override
    public void generate_detail(long memberId, long classid) {
        logger.debug("HomeWorkManagerImpl::generate_detail()");

        HomeworkGenerateDetailRequest.Builder req = new HomeworkGenerateDetailRequest.Builder();
        req.childUserId(memberId);
        req.classId(classid);

        httpClient.sendRequest(RequestUrl.HOMEWORK_GENERATE_DETAILS, req.build().toByteArray(), new RequestCallback() {


            @Override
            public void onResponse(byte[] data) throws IOException {
                HomeworkGenerateDetailResponse response = SerializeUtils.parseFrom(data, HomeworkGenerateDetailResponse.class);
                List<GameLevel> glList = new ArrayList<>();
                for (com.xcsd.rpc.proto.GameLevel gl : response.gameLevels) {
                    GameLevel temp = new GameLevel(null, gl.gameId, gl.level, gl.gameName, gl.abilityName, gl.picUrl, gl.stars,gl.color,gl.abilityId+"",gl.hasGuide);
                    glList.add(temp);
                }

                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_GAME_LIST_SUCCESS,
                        null,
                        glList,
                        0));

            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_GAME_LIST_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }

    @Override
    public void get_game_list(long memberId, long classid) {
        logger.debug("HomeWorkManagerImpl::GameListRequest()");

        GameListRequest.Builder req = new GameListRequest.Builder();

        httpClient.sendRequest(RequestUrl.HOMEWORK_GAME_LIST, req.build().toByteArray(), new RequestCallback() {


            @Override
            public void onResponse(byte[] data) throws IOException {
                GameListResponse response = SerializeUtils.parseFrom(data, GameListResponse.class);
                List<GameLevel> glList = new ArrayList<>();
                for (GameListResponse.Game gl : response.gameList) {
                    GameLevel temp = new GameLevel(null, gl.gameId, gl.levelCount, gl.gameName, gl.abilityName, gl.picUrl, 0,gl.color,"",false);
                    glList.add(temp);
                }
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_GAME_SUCCESS,
                        null,
                        glList,
                        0,0));

            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_GAME_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }


    @Override
    public void send_unified(long memberId, long classid, List<com.xcsd.rpc.proto.GameLevel> gameLevels) {
        logger.debug("HomeWorkManagerImpl::send_unified()");

        SendUnifiedHomeworkRequest.Builder req = new SendUnifiedHomeworkRequest.Builder();
        req.classId(classid);
        req.gameLevels(gameLevels);

        httpClient.sendRequest(RequestUrl.HOMEWORK_GAME_LIST_SEND, req.build().toByteArray(), new RequestCallback() {


            @Override
            public void onResponse(byte[] data) throws IOException {
                SendUnifiedHomeworkResponse response = SerializeUtils.parseFrom(data, SendUnifiedHomeworkResponse.class);

                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_SEND_GAME_SUCCESS,
                        ""));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_SEND_GAME_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }


    @Override
    public HomeWorkRecord getHomeWorkRecordLastOneFromLocal() {
        HomeWorkRecord record = UserDbHelper.getInstance().getHomeWorkRecordLastOneFromLocal();
        return record;
    }


    @Override
    public void getLatestHomeWorkList() {
        logger.debug("HomeWorkManagerImpl::getLatestHomeWorkList()");
        HomeworkListRequest.Builder req = new HomeworkListRequest.Builder();
        req.maxId(Long.MAX_VALUE);
        req.sinceId(0L);

        httpClient.sendRequest(RequestUrl.HOMEWORK_LIST, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {

                HomeworkListResponse response = SerializeUtils.parseFrom(data, HomeworkListResponse.class);

                List<Homework> pbHomeworkList = response.homeworks;
                boolean hasMore = response.hasMore;
                List<HomeWorkRecord> hwRecordList = new ArrayList<>();
                for (Homework pbHomework : pbHomeworkList) {
                    HomeWorkRecord hwRecord = PbMsgUtils.transObj(pbHomework);
                    hwRecordList.add(hwRecord);
                }
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_LIST_LATEST_SUCCESS,
                        null,
                        hwRecordList,
                        hasMore
                ));
                UserDbHelper.getInstance().updateHomeWorkList(hwRecordList);
            }


            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_LIST_LATEST_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }

    @Override
    public void getHistoryHomeWorkList(long maxId) {
        logger.debug("HomeWorkManagerImpl::getHistoryHomeWorkList()");
        HomeworkListRequest.Builder req = new HomeworkListRequest.Builder().maxId(maxId).sinceId(0L);

        httpClient.sendRequest(RequestUrl.HOMEWORK_LIST, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                HomeworkListResponse response = SerializeUtils.parseFrom(data, HomeworkListResponse.class);
                List<Homework> pbHomeworkList = response.homeworks;
                boolean hasMore = response.hasMore;
                List<HomeWorkRecord> hwRecordList = new ArrayList<>();
                for (Homework pbHomework : pbHomeworkList) {
                    HomeWorkRecord hwRecord = PbMsgUtils.transObj(pbHomework);
                    hwRecordList.add(hwRecord);
                }
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_LIST_HISTORY_SUCCESS,
                        null,
                        hwRecordList,
                        hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_LIST_LATEST_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }

    //读作业通知
    @Override
    public void ReadHomeworkNoticeRequest(final long homeworkNoticeId) {
        logger.debug("HomeWorkManagerImpl::ReadHomeworkNoticeRequest()");
        ReadHomeworkNoticeRequest.Builder req = new ReadHomeworkNoticeRequest.Builder().homeworkNoticeId(homeworkNoticeId);

        httpClient.sendRequest(RequestUrl.HOMEWORK_READ_NOTICE, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {

                ReadHomeworkNoticeResponse response = SerializeUtils.parseFrom(data, ReadHomeworkNoticeResponse.class);

                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_READ_NOTICE_SUCCESS,
                        null));
                UserDbHelper.getInstance().setHomeWorkNoticeToReaded(homeworkNoticeId);

                counterManager.decCounter(Constants.COUNTER.HOMEWORK);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_READ_NOTICE_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }

    //删除作业通知
    @Override
    public void DeleteHomeworkNoticeRequest(final long homeworkNoticeId) {
        logger.debug("HomeWorkManagerImpl::DeleteHomeworkNoticeRequest()");
        DeleteHomeworkNoticeRequest.Builder req = new DeleteHomeworkNoticeRequest.Builder().homeworkNoticeId(homeworkNoticeId);

        httpClient.sendRequest(RequestUrl.HOMEWORK_DELETE_NOTICE, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                DeleteHomeworkNoticeResponse response = SerializeUtils.parseFrom(data, DeleteHomeworkNoticeResponse.class);

                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_DELETE_NOTICE_SUCCESS,
                        null
                ));

                UserDbHelper.getInstance().deleteHomeWorkRecordById(homeworkNoticeId);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_DELETE_NOTICE_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }


    @Override
    public void getHomeworkRankingFromParent(long childUserId) {
        HomeworkRankingRequest(childUserId, null);
    }

    @Override
    public void getHomeworkRankingFromTeacher(long classId) {
        HomeworkRankingRequest(null, classId);
    }

    @Override
    public void getHomeworkRankingFromTeacherLocation() {
        List<HomeWorkUserRank> homeWorkUserRanks= UserDbHelper.getInstance().getLatestHomeWorkUserRank();
        HomeworkEvent event = new HomeworkEvent(
                HomeworkEvent.EventType.HOMEWORK_RANKING_LOCTION_SUCCESS,
                null);
        event.setHomeWorkUserRankList(homeWorkUserRanks);
        EventBus.getDefault().post(event);
    }

    //获取作业排名
    private void HomeworkRankingRequest(Long childUserId, final Long classId) {
        logger.debug("HomeWorkManagerImpl::HomeworkRankingRequest()");
        final HomeworkRankingRequest.Builder req = new HomeworkRankingRequest.Builder();
        if (childUserId != null) {
            req.childUserId(childUserId.longValue());
        }
        if (classId != null) {
            req.classId(classId.longValue());
        }

        httpClient.sendRequest(RequestUrl.HOMEWORK_RANKING, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                HomeworkRankingResponse response = SerializeUtils.parseFrom(data, HomeworkRankingResponse.class);
                List<HomeWorkUserRank> userRankList = new ArrayList<>();
                for (UserRank pbUserRank : response.rankList) {
                    HomeWorkUserRank userRank = PbMsgUtils.transObj(pbUserRank);
                    userRankList.add(userRank);
                }
                HomeworkEvent event = new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_RANKING_SUCCESS,
                        null);
                event.setHomeWorkUserRankList(userRankList);
                EventBus.getDefault().post(event);
//                if (classId != null) {
//                    UserDbHelper.getInstance().updateHomeWorkRank(userRankList);
//                }
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_RANKING_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }

    //获取本月作业情况统计月历
    @Override
    public void HomeworkCalendarRequest(long childUserId) {
        logger.debug("HomeWorkManagerImpl::HomeworkCalendarRequest()");
        HomeworkCalendarRequest.Builder req = new HomeworkCalendarRequest.Builder();
        req.childUserId(childUserId);

        httpClient.sendRequest(RequestUrl.HOMEWORK_CALENDAR, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                HomeworkCalendarResponse response = SerializeUtils.parseFrom(data, HomeworkCalendarResponse.class);
                HomeworkEvent event = new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_CALENDAR_SUCCESS,
                        null,
                        response.unfinished,
                        response.finished
                );
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_CALENDAR_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }

    @Override
    public void getHomeWorkSendListFromLocal() {
        List<HomeWorkClass> hwClassList = UserDbHelper.getInstance().getLatestHomeWorkSendList();
        HomeworkEvent event = new HomeworkEvent(
                HomeworkEvent.EventType.HOMEWORK_SENT_LIST_FROM_LOCAL,
                null,
                hwClassList);
        event.setHasMore(false);
        EventBus.getDefault().post(event);
    }

    @Override
    public void getHomeWorkSendListLatest() {
        logger.debug("HomeWorkManagerImpl::getHomeWorkSendListLatest()");
        HomeworkSentListRequest.Builder req = new HomeworkSentListRequest.Builder();
        req.maxId(Long.MAX_VALUE);
        req.sinceId(0L);

        httpClient.sendRequest(RequestUrl.HOMEWORK_SENT_LIST, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                HomeworkSentListResponse response = SerializeUtils.parseFrom(data, HomeworkSentListResponse.class);

                List<ClassHomework> pbHomeworkList = response.homeworks;
                boolean hasMore = response.hasMore;
                List<HomeWorkClass> hwClassList = new ArrayList<>();
                for (ClassHomework pbHomework : pbHomeworkList) {
                    HomeWorkClass hwClass = PbMsgUtils.transObj(pbHomework);
                    hwClassList.add(hwClass);
                }
                HomeworkEvent event = new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_SENT_LIST_LATEST_SUCCESS,
                        null,
                        hwClassList);
                event.setHasMore(hasMore);
                EventBus.getDefault().post(event);
                UserDbHelper.getInstance().updateHomeWorkSendList(hwClassList);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_SENT_LIST_LATEST_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }

    @Override
    public void getHomeWorkSendListHistory(long maxId) {
        logger.debug("HomeWorkManagerImpl::getHomeWorkSendListHistory()");
        HomeworkSentListRequest.Builder req = new HomeworkSentListRequest.Builder();
        req.maxId(maxId);
        req.sinceId(0L);

        httpClient.sendRequest(RequestUrl.HOMEWORK_SENT_LIST, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                HomeworkSentListResponse response = SerializeUtils.parseFrom(data, HomeworkSentListResponse.class);
                List<ClassHomework> pbHomeworkList = response.homeworks;
                boolean hasMore = response.hasMore;
                List<HomeWorkClass> hwClassList = new ArrayList<>();
                for (ClassHomework pbHomework : pbHomeworkList) {
                    HomeWorkClass hwClass = PbMsgUtils.transObj(pbHomework);
                    hwClassList.add(hwClass);
                }
                HomeworkEvent event = new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_SENT_LIST_HISTORY_SUCCESS,
                        null,
                        hwClassList);
                event.setHasMore(hasMore);
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_SENT_LIST_HISTORY_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }

    //获取作业成员列表
    @Override
    public void getHomeworkMemberListFromLacal(long homeworkId) {
        List<HomeWorkMember> memberList = UserDbHelper.getInstance().getLatestHomeWorkMemberList(homeworkId);
        HomeworkEvent event = new HomeworkEvent(
                HomeworkEvent.EventType.HOMEWORK_MEMBERS_LIST_FROM_LOCAL,
                null);
        event.setHasMore(false);
        event.setHomeWorkMemberList(memberList);
        EventBus.getDefault().post(event);
    }

    @Override
    public void getLatestHomeworkMemberList(final long homeworkId) {
        logger.debug("HomeWorkManagerImpl::getLatestHomeworkMemberList()");
        HomeworkMemberListRequest.Builder req = new HomeworkMemberListRequest.Builder();
        req.homeworkId(homeworkId);

        httpClient.sendRequest(RequestUrl.HOMEWORK_MEMBERS, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                HomeworkMemberListResponse response = SerializeUtils.parseFrom(data, HomeworkMemberListResponse.class);
                List<HomeworkMember> pbHomeworkList = response.members;
                boolean hasMore = response.hasMore;
                List<HomeWorkMember> memberList = new ArrayList<>();
                for (HomeworkMember pbHomeworkMember : pbHomeworkList) {
                    HomeWorkMember info = PbMsgUtils.transObj(pbHomeworkMember, homeworkId);
                    memberList.add(info);
                }

                HomeworkEvent event = new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_MEMBERS_LIST_LATEST_SUCCESS,
                        null);
                event.setHomeWorkMemberList(memberList);
                event.setHasMore(hasMore);
                EventBus.getDefault().post(event);

                UserDbHelper.getInstance().updateHomeWorkMemberList(memberList);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_MEMBERS_LIST_HISTORY_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }

    @Override
    public void getHistoryHomeworkMemberList(final long homeworkId) {
        logger.debug("HomeWorkManagerImpl::getHistoryHomeworkMemberList()");
        HomeworkMemberListRequest.Builder req = new HomeworkMemberListRequest.Builder();
        req.homeworkId(homeworkId);

        httpClient.sendRequest(RequestUrl.HOMEWORK_MEMBERS, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                HomeworkMemberListResponse response = SerializeUtils.parseFrom(data, HomeworkMemberListResponse.class);
                List<HomeworkMember> pbHomeworkList = response.members;
                boolean hasMore = response.hasMore;
                List<HomeWorkMember> infoList = new ArrayList<>();
                for (HomeworkMember pbHomeworkMember : pbHomeworkList) {
                    HomeWorkMember info = PbMsgUtils.transObj(pbHomeworkMember, homeworkId);
                    infoList.add(info);
                }

                HomeworkEvent event = new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_MEMBERS_LIST_HISTORY_SUCCESS,
                        null);
                event.setHomeWorkMemberList(infoList);
                event.setHasMore(hasMore);
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_MEMBERS_LIST_HISTORY_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }

    //生成定制作业
    @Override
    public void GenerateHomeworkRequest(long classId) {
        logger.debug("HomeWorkManagerImpl::GenerateHomeworkRequest()");
        GenerateHomeworkRequest.Builder req = new GenerateHomeworkRequest.Builder();
        req.classId(classId);

        httpClient.sendRequest(RequestUrl.HOMEWORK_GENERATE, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                GenerateHomeworkResponse response = SerializeUtils.parseFrom(data, GenerateHomeworkResponse.class);
                List<GenerateHomeworkResponse.UserHomework> pbHomeworkList = response.userHomeworks;
                List<HomeWorkGenerate> infoList = new ArrayList<>();
                for (GenerateHomeworkResponse.UserHomework pbHomeWork : pbHomeworkList) {
                    HomeWorkGenerate info = PbMsgUtils.transObj(pbHomeWork);
                    infoList.add(info);
                }

                HomeworkEvent event = new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_GENERATE_SUCCESS,
                        null);
                event.setHomeWorkGenerateList(infoList);
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_GENERATE_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }

    // 发送定制作业
    @Override
    public void SendHomeworkRequest(long classId, int scope) {
        logger.debug("HomeWorkManagerImpl::SendHomeworkRequest()");
        SendHomeworkRequest.Builder req = new SendHomeworkRequest.Builder();
        req.classId(classId);
        req.scope(StudentScope.values()[scope - 1]);

        httpClient.sendRequest(RequestUrl.HOMEWORK_SEND, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                SendHomeworkResponse response = SerializeUtils.parseFrom(data, SendHomeworkResponse.class);

                HomeworkEvent event = new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_SEND_SUCCESS,
                        null);
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_SEND_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }


    //获取可发送的作业数量
    @Override
    public void HomeworkRemainingCountRequest(long classId) {
        logger.debug("HomeWorkManagerImpl::HomeworkRemainingCountRequest()");
        HomeworkRemainingCountRequest.Builder req = new HomeworkRemainingCountRequest.Builder();
        req.classId(classId);

        httpClient.sendRequest(RequestUrl.HOMEWORK_REMAINING_COUNT, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                HomeworkRemainingCountResponse response = SerializeUtils.parseFrom(data, HomeworkRemainingCountResponse.class);

                HomeworkEvent event = new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_REMAINING_COUNT_SUCCESS,
                        null,
                        response.customizedStatus,
                        response.unifiedCount
                );
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.HOMEWORK_REMAINING_COUNT_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }

    @Override
    public void getClassAbilityRankingList(long classId) {
        logger.debug("HomeWorkManagerImpl::getClassAbilityRankingList()");
        ClassAbilityRankingRequest.Builder req = new ClassAbilityRankingRequest.Builder();
        req.classId(classId);

        httpClient.sendRequest(RequestUrl.LEARNING_ABILITY_RANKING, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                ClassAbilityRankingResponse response = SerializeUtils.parseFrom(data, ClassAbilityRankingResponse.class);
                List<HomeWorkUserRank> userRankList = new ArrayList<>();
                for (UserRank pbUserRank : response.rankList) {
                    HomeWorkUserRank userRank = PbMsgUtils.transObj(pbUserRank);
                    userRankList.add(userRank);
                }
                HomeworkEvent event = new HomeworkEvent(
                        HomeworkEvent.EventType.LEARNING_ABILITY_RANKING_SUCCESS,
                        null);
                event.setHomeWorkUserRankList(userRankList);
                EventBus.getDefault().post(event);
                UserDbHelper.getInstance().updateHomeWorkRank(userRankList);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new HomeworkEvent(
                        HomeworkEvent.EventType.LEARNING_ABILITY_RANKING_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }
}
