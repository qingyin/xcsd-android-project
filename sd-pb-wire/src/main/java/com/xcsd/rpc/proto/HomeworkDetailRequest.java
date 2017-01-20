// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: src/main/resources/proto/Homework.proto
package com.xcsd.rpc.proto;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.INT64;
import static com.squareup.wire.Message.Label.REQUIRED;

/**
 * H5改源生新增接口
 * 作业详情接口
 * URL:/homework/detail
 */
public final class HomeworkDetailRequest extends Message {
  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_MEMBERID = 0L;

  @ProtoField(tag = 1, type = INT64, label = REQUIRED)
  public final Long memberId;

  public HomeworkDetailRequest(Long memberId) {
    this.memberId = memberId;
  }

  private HomeworkDetailRequest(Builder builder) {
    this(builder.memberId);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof HomeworkDetailRequest)) return false;
    return equals(memberId, ((HomeworkDetailRequest) other).memberId);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    return result != 0 ? result : (hashCode = memberId != null ? memberId.hashCode() : 0);
  }

  public static final class Builder extends Message.Builder<HomeworkDetailRequest> {

    public Long memberId;

    public Builder() {
    }

    public Builder(HomeworkDetailRequest message) {
      super(message);
      if (message == null) return;
      this.memberId = message.memberId;
    }

    public Builder memberId(Long memberId) {
      this.memberId = memberId;
      return this;
    }

    @Override
    public HomeworkDetailRequest build() {
      checkRequiredFields();
      return new HomeworkDetailRequest(this);
    }
  }
}
