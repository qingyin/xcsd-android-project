// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: src/main/resources/proto/Homework.proto
package com.xcsd.rpc.proto;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Datatype.ENUM;
import static com.squareup.wire.Message.Datatype.INT32;
import static com.squareup.wire.Message.Datatype.INT64;
import static com.squareup.wire.Message.Datatype.STRING;
import static com.squareup.wire.Message.Label.REPEATED;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class HomeworkDetailResponse extends Message {
  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_MEMBERID = 0L;
  public static final HomeworkStatus DEFAULT_STATUS = HomeworkStatus.UNFINISHED;
  public static final String DEFAULT_TITLE = "";
  public static final String DEFAULT_DESCRIPTION = "";
  public static final String DEFAULT_SENDERNAME = "";
  public static final Integer DEFAULT_TOTALSCORE = 0;
  public static final Integer DEFAULT_MAXSCORE = 0;
  public static final List<GameLevel> DEFAULT_GAMELEVELS = Collections.emptyList();
  public static final Long DEFAULT_SENDTIME = 0L;
  public static final Long DEFAULT_CHILDUSERID = 0L;

  /**
   * 传给H5
   */
  @ProtoField(tag = 1, type = INT64, label = REQUIRED)
  public final Long memberId;

  /**
   * 作业状态
   */
  @ProtoField(tag = 2, type = ENUM, label = REQUIRED)
  public final HomeworkStatus status;

  /**
   * 标题 预留 现在用不上
   */
  @ProtoField(tag = 3, type = STRING)
  public final String title;

  /**
   * 描述 预留
   */
  @ProtoField(tag = 4, type = STRING)
  public final String description;

  /**
   * 发送者名称
   */
  @ProtoField(tag = 5, type = STRING)
  public final String senderName;

  /**
   * 作业总得分
   */
  @ProtoField(tag = 6, type = INT32)
  public final Integer totalScore;

  /**
   * 作业满分
   */
  @ProtoField(tag = 7, type = INT32)
  public final Integer maxScore;

  /**
   * 作业布置的游戏关卡
   */
  @ProtoField(tag = 8, label = REPEATED, messageType = GameLevel.class)
  public final List<GameLevel> gameLevels;

  /**
   * 作业发送时间
   */
  @ProtoField(tag = 9, type = INT64)
  public final Long sendTime;

  /**
   * 孩子ID
   */
  @ProtoField(tag = 10, type = INT64)
  public final Long childUserId;

  public HomeworkDetailResponse(Long memberId, HomeworkStatus status, String title, String description, String senderName, Integer totalScore, Integer maxScore, List<GameLevel> gameLevels, Long sendTime, Long childUserId) {
    this.memberId = memberId;
    this.status = status;
    this.title = title;
    this.description = description;
    this.senderName = senderName;
    this.totalScore = totalScore;
    this.maxScore = maxScore;
    this.gameLevels = immutableCopyOf(gameLevels);
    this.sendTime = sendTime;
    this.childUserId = childUserId;
  }

  private HomeworkDetailResponse(Builder builder) {
    this(builder.memberId, builder.status, builder.title, builder.description, builder.senderName, builder.totalScore, builder.maxScore, builder.gameLevels, builder.sendTime, builder.childUserId);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof HomeworkDetailResponse)) return false;
    HomeworkDetailResponse o = (HomeworkDetailResponse) other;
    return equals(memberId, o.memberId)
        && equals(status, o.status)
        && equals(title, o.title)
        && equals(description, o.description)
        && equals(senderName, o.senderName)
        && equals(totalScore, o.totalScore)
        && equals(maxScore, o.maxScore)
        && equals(gameLevels, o.gameLevels)
        && equals(sendTime, o.sendTime)
        && equals(childUserId, o.childUserId);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = memberId != null ? memberId.hashCode() : 0;
      result = result * 37 + (status != null ? status.hashCode() : 0);
      result = result * 37 + (title != null ? title.hashCode() : 0);
      result = result * 37 + (description != null ? description.hashCode() : 0);
      result = result * 37 + (senderName != null ? senderName.hashCode() : 0);
      result = result * 37 + (totalScore != null ? totalScore.hashCode() : 0);
      result = result * 37 + (maxScore != null ? maxScore.hashCode() : 0);
      result = result * 37 + (gameLevels != null ? gameLevels.hashCode() : 1);
      result = result * 37 + (sendTime != null ? sendTime.hashCode() : 0);
      result = result * 37 + (childUserId != null ? childUserId.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<HomeworkDetailResponse> {

    public Long memberId;
    public HomeworkStatus status;
    public String title;
    public String description;
    public String senderName;
    public Integer totalScore;
    public Integer maxScore;
    public List<GameLevel> gameLevels;
    public Long sendTime;
    public Long childUserId;

    public Builder() {
    }

    public Builder(HomeworkDetailResponse message) {
      super(message);
      if (message == null) return;
      this.memberId = message.memberId;
      this.status = message.status;
      this.title = message.title;
      this.description = message.description;
      this.senderName = message.senderName;
      this.totalScore = message.totalScore;
      this.maxScore = message.maxScore;
      this.gameLevels = copyOf(message.gameLevels);
      this.sendTime = message.sendTime;
      this.childUserId = message.childUserId;
    }

    /**
     * 传给H5
     */
    public Builder memberId(Long memberId) {
      this.memberId = memberId;
      return this;
    }

    /**
     * 作业状态
     */
    public Builder status(HomeworkStatus status) {
      this.status = status;
      return this;
    }

    /**
     * 标题 预留 现在用不上
     */
    public Builder title(String title) {
      this.title = title;
      return this;
    }

    /**
     * 描述 预留
     */
    public Builder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * 发送者名称
     */
    public Builder senderName(String senderName) {
      this.senderName = senderName;
      return this;
    }

    /**
     * 作业总得分
     */
    public Builder totalScore(Integer totalScore) {
      this.totalScore = totalScore;
      return this;
    }

    /**
     * 作业满分
     */
    public Builder maxScore(Integer maxScore) {
      this.maxScore = maxScore;
      return this;
    }

    /**
     * 作业布置的游戏关卡
     */
    public Builder gameLevels(List<GameLevel> gameLevels) {
      this.gameLevels = checkForNulls(gameLevels);
      return this;
    }

    /**
     * 作业发送时间
     */
    public Builder sendTime(Long sendTime) {
      this.sendTime = sendTime;
      return this;
    }

    /**
     * 孩子ID
     */
    public Builder childUserId(Long childUserId) {
      this.childUserId = childUserId;
      return this;
    }

    @Override
    public HomeworkDetailResponse build() {
      checkRequiredFields();
      return new HomeworkDetailResponse(this);
    }
  }
}
