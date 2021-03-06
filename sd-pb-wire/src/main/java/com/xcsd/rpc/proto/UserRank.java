// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: src/main/resources/proto/Homework.proto
package com.xcsd.rpc.proto;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.INT32;
import static com.squareup.wire.Message.Datatype.INT64;
import static com.squareup.wire.Message.Datatype.STRING;
import static com.squareup.wire.Message.Label.REQUIRED;

/**
 * 用户通用排名
 */
public final class UserRank extends Message {
  private static final long serialVersionUID = 0L;

  public static final Integer DEFAULT_RANK = 0;
  public static final Long DEFAULT_USERID = 0L;
  public static final String DEFAULT_NAME = "";
  public static final String DEFAULT_AVATAR = "";
  public static final Integer DEFAULT_SCORE = 0;

  @ProtoField(tag = 1, type = INT32, label = REQUIRED)
  public final Integer rank;

  @ProtoField(tag = 2, type = INT64, label = REQUIRED)
  public final Long userId;

  @ProtoField(tag = 3, type = STRING)
  public final String name;

  @ProtoField(tag = 4, type = STRING)
  public final String avatar;

  @ProtoField(tag = 5, type = INT32, label = REQUIRED)
  public final Integer score;

  public UserRank(Integer rank, Long userId, String name, String avatar, Integer score) {
    this.rank = rank;
    this.userId = userId;
    this.name = name;
    this.avatar = avatar;
    this.score = score;
  }

  private UserRank(Builder builder) {
    this(builder.rank, builder.userId, builder.name, builder.avatar, builder.score);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof UserRank)) return false;
    UserRank o = (UserRank) other;
    return equals(rank, o.rank)
        && equals(userId, o.userId)
        && equals(name, o.name)
        && equals(avatar, o.avatar)
        && equals(score, o.score);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = rank != null ? rank.hashCode() : 0;
      result = result * 37 + (userId != null ? userId.hashCode() : 0);
      result = result * 37 + (name != null ? name.hashCode() : 0);
      result = result * 37 + (avatar != null ? avatar.hashCode() : 0);
      result = result * 37 + (score != null ? score.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<UserRank> {

    public Integer rank;
    public Long userId;
    public String name;
    public String avatar;
    public Integer score;

    public Builder() {
    }

    public Builder(UserRank message) {
      super(message);
      if (message == null) return;
      this.rank = message.rank;
      this.userId = message.userId;
      this.name = message.name;
      this.avatar = message.avatar;
      this.score = message.score;
    }

    public Builder rank(Integer rank) {
      this.rank = rank;
      return this;
    }

    public Builder userId(Long userId) {
      this.userId = userId;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder avatar(String avatar) {
      this.avatar = avatar;
      return this;
    }

    public Builder score(Integer score) {
      this.score = score;
      return this;
    }

    @Override
    public UserRank build() {
      checkRequiredFields();
      return new UserRank(this);
    }
  }
}
