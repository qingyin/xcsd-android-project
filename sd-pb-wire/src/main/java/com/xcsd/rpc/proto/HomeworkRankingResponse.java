// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: src/main/resources/proto/Homework.proto
package com.xcsd.rpc.proto;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Label.REPEATED;

public final class HomeworkRankingResponse extends Message {
  private static final long serialVersionUID = 0L;

  public static final List<UserRank> DEFAULT_RANKLIST = Collections.emptyList();

  @ProtoField(tag = 1, label = REPEATED, messageType = UserRank.class)
  public final List<UserRank> rankList;

  public HomeworkRankingResponse(List<UserRank> rankList) {
    this.rankList = immutableCopyOf(rankList);
  }

  private HomeworkRankingResponse(Builder builder) {
    this(builder.rankList);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof HomeworkRankingResponse)) return false;
    return equals(rankList, ((HomeworkRankingResponse) other).rankList);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    return result != 0 ? result : (hashCode = rankList != null ? rankList.hashCode() : 1);
  }

  public static final class Builder extends Message.Builder<HomeworkRankingResponse> {

    public List<UserRank> rankList;

    public Builder() {
    }

    public Builder(HomeworkRankingResponse message) {
      super(message);
      if (message == null) return;
      this.rankList = copyOf(message.rankList);
    }

    public Builder rankList(List<UserRank> rankList) {
      this.rankList = checkForNulls(rankList);
      return this;
    }

    @Override
    public HomeworkRankingResponse build() {
      return new HomeworkRankingResponse(this);
    }
  }
}