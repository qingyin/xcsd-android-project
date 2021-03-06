// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: src/main/resources/proto/Homework.proto
package com.xcsd.rpc.proto;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.INT64;
import static com.squareup.wire.Message.Label.REQUIRED;

/**
 * 生成定制作业
 * URL:/homework/generate
 */
public final class GenerateHomeworkRequest extends Message {
  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_CLASSID = 0L;

  @ProtoField(tag = 1, type = INT64, label = REQUIRED)
  public final Long classId;

  public GenerateHomeworkRequest(Long classId) {
    this.classId = classId;
  }

  private GenerateHomeworkRequest(Builder builder) {
    this(builder.classId);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof GenerateHomeworkRequest)) return false;
    return equals(classId, ((GenerateHomeworkRequest) other).classId);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    return result != 0 ? result : (hashCode = classId != null ? classId.hashCode() : 0);
  }

  public static final class Builder extends Message.Builder<GenerateHomeworkRequest> {

    public Long classId;

    public Builder() {
    }

    public Builder(GenerateHomeworkRequest message) {
      super(message);
      if (message == null) return;
      this.classId = message.classId;
    }

    public Builder classId(Long classId) {
      this.classId = classId;
      return this;
    }

    @Override
    public GenerateHomeworkRequest build() {
      checkRequiredFields();
      return new GenerateHomeworkRequest(this);
    }
  }
}
