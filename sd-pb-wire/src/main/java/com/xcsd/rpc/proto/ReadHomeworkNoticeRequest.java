// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: src/main/resources/proto/Homework.proto
package com.xcsd.rpc.proto;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.INT64;
import static com.squareup.wire.Message.Label.REQUIRED;

/**
 * 读作业通知
 * URL:/homework/read_notice
 */
public final class ReadHomeworkNoticeRequest extends Message {
  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_HOMEWORKNOTICEID = 0L;

  @ProtoField(tag = 1, type = INT64, label = REQUIRED)
  public final Long homeworkNoticeId;

  public ReadHomeworkNoticeRequest(Long homeworkNoticeId) {
    this.homeworkNoticeId = homeworkNoticeId;
  }

  private ReadHomeworkNoticeRequest(Builder builder) {
    this(builder.homeworkNoticeId);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof ReadHomeworkNoticeRequest)) return false;
    return equals(homeworkNoticeId, ((ReadHomeworkNoticeRequest) other).homeworkNoticeId);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    return result != 0 ? result : (hashCode = homeworkNoticeId != null ? homeworkNoticeId.hashCode() : 0);
  }

  public static final class Builder extends Message.Builder<ReadHomeworkNoticeRequest> {

    public Long homeworkNoticeId;

    public Builder() {
    }

    public Builder(ReadHomeworkNoticeRequest message) {
      super(message);
      if (message == null) return;
      this.homeworkNoticeId = message.homeworkNoticeId;
    }

    public Builder homeworkNoticeId(Long homeworkNoticeId) {
      this.homeworkNoticeId = homeworkNoticeId;
      return this;
    }

    @Override
    public ReadHomeworkNoticeRequest build() {
      checkRequiredFields();
      return new ReadHomeworkNoticeRequest(this);
    }
  }
}
