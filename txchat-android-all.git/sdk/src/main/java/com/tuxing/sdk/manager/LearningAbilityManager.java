package com.tuxing.sdk.manager;

import com.xcsd.rpc.proto.Ability;

/**
 * Created by apple on 16/4/6.
 */
public interface LearningAbilityManager extends BaseManager{

    //班级学习能力排行榜
    void ClassAbilityRankingRequest(long classId);
    //用户学习能力排行榜
    void AbilityEvaluationRequestlocation();
    //用户学习能力排行榜
    void AbilityEvaluationRequest(long childUserId);
    //开始测试游戏
    void GameTestRequest(long UserId);

    void GameScoreRequest(long userId,Ability ability);
}
