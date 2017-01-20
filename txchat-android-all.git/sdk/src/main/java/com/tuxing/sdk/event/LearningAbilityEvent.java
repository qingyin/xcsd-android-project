package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.AbilityDetailList;
import com.tuxing.sdk.db.entity.AbilityPoint;
import com.tuxing.sdk.db.entity.GameLevel;
import com.tuxing.sdk.db.entity.Game_Score;
import com.tuxing.sdk.db.entity.HomeWorkUserRank;

import java.util.List;

/**
 * Created by apple on 16/4/9.
 */
public class LearningAbilityEvent extends BaseEvent{


    public enum EventType{
        GAME_SCORE_SUCCESS,
        GAME_SCORE_FAILED,

        GAME_TEST_SUCCESS,
        GAME_TEST_FAILED,

        ABILITY_SUCCESS_LOCATION,
        ABILITY_FAILED_LOCATION,

        ABILITY_SUCCESS,
        ABILITY_FAILED,

        LEARNING_ABILITY_RANKING,
        LEARNING_ABILITY_FAILED,
    }
    private EventType event;
    private List<HomeWorkUserRank> homeWorkUserRanks;
    private List<AbilityDetailList> abilityDetailslist;
    private List<AbilityPoint> abilityPoint;
    private int avgAbilityLevel;
    private Double avgAbilityPercentage;
    private int abilityQuotient;
    private int maxAbilityQuotient;

    private List<GameLevel> gameLevels;
    private boolean isfirst;
    private long testid;
    private long userId;


    private List<Game_Score> gameLevel;
    private int totalScore;



    public EventType getEvent() {

        return event;
    }

    public LearningAbilityEvent(EventType event, String msg) {
        super(msg);
        this.event = event;
    }

    public LearningAbilityEvent(EventType event, String msg,List<HomeWorkUserRank> homeWorkUserRanks) {
        super(msg);
        this.event = event;
        this.homeWorkUserRanks = homeWorkUserRanks;
    }

    public LearningAbilityEvent(EventType event, String msg,List<AbilityDetailList> abilityDetailslist,List<AbilityPoint> abilityPoint) {
        super(msg);
        this.event = event;
        this.abilityDetailslist = abilityDetailslist;
        this.abilityPoint = abilityPoint;
        this.avgAbilityLevel = avgAbilityLevel;
        this.avgAbilityPercentage = avgAbilityPercentage;
        this.abilityQuotient = abilityQuotient;
        this.maxAbilityQuotient = maxAbilityQuotient;
    }
    public LearningAbilityEvent(EventType event, String msg,List<AbilityDetailList> abilityDetailslist,List<AbilityPoint> abilityPoint,int avgAbilityLevel,Double avgAbilityPercentage,int abilityQuotient,int maxAbilityQuotient) {
        super(msg);
        this.event = event;
        this.abilityDetailslist = abilityDetailslist;
        this.abilityPoint = abilityPoint;
        this.avgAbilityLevel = avgAbilityLevel;
        this.avgAbilityPercentage = avgAbilityPercentage;
        this.abilityQuotient = abilityQuotient;
        this.maxAbilityQuotient = maxAbilityQuotient;
    }


    public LearningAbilityEvent(EventType event,String msg, List<Game_Score> gameLevels,int totalScore) {
        super(msg);
        this.event = event;
        this.gameLevel = gameLevels;
        this.totalScore = totalScore;
    }

    public LearningAbilityEvent(EventType event,String msg, List<GameLevel> gameLevels, boolean isfirst, long testid,long userId) {
        super(msg);
        this.event = event;
        this.gameLevels = gameLevels;
        this.isfirst = isfirst;
        this.testid = testid;
        this.userId = userId;
    }

    public List<HomeWorkUserRank> getHomeWorkUserRanks() {
        return homeWorkUserRanks;
    }

    public List<AbilityDetailList> getAbilityDetailslist() {
        return abilityDetailslist;
    }

    public List<AbilityPoint> getAbilityPoint() {
        return abilityPoint;
    }

    public int getAvgAbilityLevel() {
        return avgAbilityLevel;
    }

    public Double getAvgAbilityPercentage() {
        return avgAbilityPercentage;
    }

    public int getAbilityQuotient() {
        return abilityQuotient;
    }

    public int getMaxAbilityQuotient() {
        return maxAbilityQuotient;
    }

    public List<GameLevel> getGameLevels() {
        return gameLevels;
    }

    public void setGameLevels(List<GameLevel> gameLevels) {
        this.gameLevels = gameLevels;
    }

    public boolean getIsfirst() {
        return isfirst;
    }

    public void setIsfirst(boolean isfirst) {
        this.isfirst = isfirst;
    }

    public long getTestid() {
        return testid;
    }

    public void setTestid(long testid) {
        this.testid = testid;
    }

    public List<Game_Score> getGameLevel() {
        return gameLevel;
    }

    public void setGameLevel(List<Game_Score> gameLevel) {
        this.gameLevel = gameLevel;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
