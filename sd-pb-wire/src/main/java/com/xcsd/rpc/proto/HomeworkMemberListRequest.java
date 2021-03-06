// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: src/main/resources/proto/Homework.proto
package com.xcsd.rpc.proto;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.INT32;
import static com.squareup.wire.Message.Datatype.INT64;
import static com.squareup.wire.Message.Label.REQUIRED;

/**
 * 获取作业成员列表
 * URL:/homework/members
 */
public final class HomeworkMemberListRequest extends Message {
  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_HOMEWORKID = 0L;
  public static final Integer DEFAULT_PAGENUM = 0;

  @ProtoField(tag = 1, type = INT64, label = REQUIRED)
  public final Long homeworkId;

  /**
   * 翻页的页数 首页可不传
   */
  @ProtoField(tag = 2, type = INT32)
  public final Integer pageNum;

  public HomeworkMemberListRequest(Long homeworkId, Integer pageNum) {
    this.homeworkId = homeworkId;
    this.pageNum = pageNum;
  }

  private HomeworkMemberListRequest(Builder builder) {
    this(builder.homeworkId, builder.pageNum);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof HomeworkMemberListRequest)) return false;
    HomeworkMemberListRequest o = (HomeworkMemberListRequest) other;
    return equals(homeworkId, o.homeworkId)
        && equals(pageNum, o.pageNum);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = homeworkId != null ? homeworkId.hashCode() : 0;
      result = result * 37 + (pageNum != null ? pageNum.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<HomeworkMemberListRequest> {

    public Long homeworkId;
    public Integer pageNum;

    public Builder() {
    }

    public Builder(HomeworkMemberListRequest message) {
      super(message);
      if (message == null) return;
      this.homeworkId = message.homeworkId;
      this.pageNum = message.pageNum;
    }

    public Builder homeworkId(Long homeworkId) {
      this.homeworkId = homeworkId;
      return this;
    }

    /**
     * 翻页的页数 首页可不传
     */
    public Builder pageNum(Integer pageNum) {
      this.pageNum = pageNum;
      return this;
    }

    @Override
    public HomeworkMemberListRequest build() {
      checkRequiredFields();
      return new HomeworkMemberListRequest(this);
    }
  }
}
