package com.tuxing.sdk.manager.impl;

import android.content.Context;
import android.content.SharedPreferences;

import com.tuxing.sdk.db.entity.*;
import com.tuxing.sdk.db.entity.GameLevel;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.HomeworkEvent;
import com.tuxing.sdk.event.LearningAbilityEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.LearningAbilityManager;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.PbMsgUtils;
import com.xcsd.rpc.proto.AbilityDetail;
//import com.xcsd.rpc.proto.HomeworkProto;
import com.xcsd.rpc.proto.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import com.tuxing.sdk.utils.SerializeUtils;

/**
 * Created by apple on 16/4/6.
 */
public class LearningAbilityManagerImpl implements LearningAbilityManager{
    private final static Logger logger = LoggerFactory.getLogger(LearningAbilityManager.class);
    private static LearningAbilityManager instance;
    HttpClient httpClient = HttpClient.getInstance();
    private Context context;
    private LearningAbilityManagerImpl(){}

    public synchronized static LearningAbilityManager getInstance(){
        if(instance == null){
            instance = new LearningAbilityManagerImpl();
            instance = AsyncTaskProxyFactory.getProxy(instance);
        }
        return instance;
    }
    @Override
    public void init(Context context){
        this.context = context;
    }
    @Override
    public void destroy(){
    }

    @Override
    public void ClassAbilityRankingRequest(long classId){
        logger.debug("LearningAbilityManagerImpl::ClassAbilityRankingRequest()");
        ClassAbilityRankingRequest.Builder req = new ClassAbilityRankingRequest.Builder().classId(classId);

        httpClient.sendRequest(RequestUrl.LEARNING_ABILITY_RANKING, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                ClassAbilityRankingResponse response =  SerializeUtils.parseFrom(data, ClassAbilityRankingResponse.class);;

                List<UserRank> pbUserRankList = response.rankList;
                List<HomeWorkUserRank> userRankkList = new ArrayList<>();
                for (UserRank pbUserRank : pbUserRankList) {
                    HomeWorkUserRank ur = PbMsgUtils.transObj(pbUserRank);
                    userRankkList.add(ur);
                }
                EventBus.getDefault().post(new LearningAbilityEvent(
                        LearningAbilityEvent.EventType.LEARNING_ABILITY_RANKING,
                        null,
                        userRankkList
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new LearningAbilityEvent(
                        LearningAbilityEvent.EventType.LEARNING_ABILITY_FAILED,
                        cause.getMessage(),
                        null
                ));
            }
        });
    }

    @Override
    public void AbilityEvaluationRequestlocation() {
        List<AbilityDetailList> abilityDetailslist =UserDbHelper.getInstance().getLatestAbilityDetailList();
        List<AbilityPoint> abilityPoint = UserDbHelper.getInstance().getLatestAbilityPoint();

        EventBus.getDefault().post(new LearningAbilityEvent(
                LearningAbilityEvent.EventType.ABILITY_SUCCESS_LOCATION,
                "成功",
                abilityDetailslist, abilityPoint));

    }

    @Override
    public void AbilityEvaluationRequest(long childUserId) {
        logger.debug("LearningAbilityManagerImpl::AbilityEvaluationRequest()");
        final AbilityStatRequest.Builder req = new AbilityStatRequest.Builder().childUserId(childUserId);

        httpClient.sendRequest(RequestUrl.LEARNING_ABILITY, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                AbilityStatResponse response =  SerializeUtils.parseFrom(data, AbilityStatResponse.class);
//                List<com.xcsd.rpc.proto.AbilityDetail> abilityDetails = response.details;
                List<AbilityDetailList> abilityDetailslist = new ArrayList<>();
                for (com.xcsd.rpc.proto.AbilityDetail abilityDetail : response.details) {
                    AbilityDetailList ur = PbMsgUtils.transObj(abilityDetail);
                    abilityDetailslist.add(ur);
                }
//                List<AbilityEvaluationResponse.Point> abilityChart = response.abilityChart;
                List<AbilityPoint> abilityPoint = new ArrayList<>();
                for (AbilityStatResponse.Point abd : response.abilityChart) {
                    AbilityPoint ur = PbMsgUtils.transObj(abd);
                    abilityPoint.add(ur);
                }

                EventBus.getDefault().post(new LearningAbilityEvent(
                        LearningAbilityEvent.EventType.ABILITY_SUCCESS,
                        "成功",
                        abilityDetailslist, abilityPoint, response.totalAbilityLevel, response.totalAbilityPercentage, response.abilityQuotient, response.maxAbilityQuotient
                ));
//                ScoreShow scoreShow = new ScoreShow();
//                scoreShow.setTotalAbilityLevel(response.totalAbilityLevel);
//                scoreShow.setTotalAbilityPercentage(response.totalAbilityPercentage);
//                scoreShow.setAbilityQuotient(response.abilityQuotient);
//                scoreShow.setMaxAbilityQuotient(response.maxAbilityQuotient);
                UserDbHelper.getInstance().updateTestList(abilityDetailslist, abilityPoint);

            }


            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new LearningAbilityEvent(
                        LearningAbilityEvent.EventType.ABILITY_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }

    @Override
    public void GameTestRequest(long UserId) {
        logger.debug("LearningAbilityManagerImpl::GameTestRequest()");
        final GameTestRequest.Builder req = new GameTestRequest.Builder().userId(UserId);

        httpClient.sendRequest(RequestUrl.GAME_TEST, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                GameTestResponse response =  SerializeUtils.parseFrom(data, GameTestResponse.class);
//                List<com.xcsd.rpc.proto.AbilityDetail> abilityDetails = response.details;
                List<GameLevel> gameLevels = new ArrayList<>();
                for (com.xcsd.rpc.proto.GameLevel gl : response.gameLevel) {
                    GameLevel temp = new GameLevel(null, gl.gameId, gl.level, gl.gameName, gl.abilityName, gl.picUrl, gl.stars,gl.color,gl.abilityId+"",gl.hasGuide);
                    gameLevels.add(temp);
                }

                EventBus.getDefault().post(new LearningAbilityEvent(
                        LearningAbilityEvent.EventType.GAME_TEST_SUCCESS,
                        "成功",
                        gameLevels,response.isFirstTest,response.testId,response.userId
                ));
            }


            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new LearningAbilityEvent(
                        LearningAbilityEvent.EventType.GAME_TEST_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }

    @Override
    public void GameScoreRequest(long userId, Ability ability) {
        logger.debug("LearningAbilityManagerImpl::GameTestRequest()");
        final AbilityScoreRequest.Builder req = new AbilityScoreRequest.Builder().userId(userId).ability(ability);

        httpClient.sendRequest(RequestUrl.GAME_SCORE, req.build().toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                AbilityScoreResponse response =  SerializeUtils.parseFrom(data, AbilityScoreResponse.class);
                List<Game_Score> gameLevels = new ArrayList<>();
                for (AbilityScoreResponse.GameScore gl : response.gameList) {
                    Game_Score temp = new Game_Score(null, gl.gameName, gl.score, gl.bestLevel, gl.percentage, gl.color);
                    gameLevels.add(temp);
                }

                EventBus.getDefault().post(new LearningAbilityEvent(
                        LearningAbilityEvent.EventType.GAME_SCORE_SUCCESS,
                        "成功",
                        gameLevels,response.totalScore
                ));
            }


            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new LearningAbilityEvent(
                        LearningAbilityEvent.EventType.GAME_SCORE_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }
}
