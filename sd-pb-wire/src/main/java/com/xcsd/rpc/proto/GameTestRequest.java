// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: src/main/resources/proto/Test.proto
package com.xcsd.rpc.proto;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.INT64;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class GameTestRequest extends Message {
  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_USERID = 0L;

  @ProtoField(tag = 1, type = INT64, label = REQUIRED)
  public final Long userId;

  public GameTestRequest(Long userId) {
    this.userId = userId;
  }

  private GameTestRequest(Builder builder) {
    this(builder.userId);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof GameTestRequest)) return false;
    return equals(userId, ((GameTestRequest) other).userId);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    return result != 0 ? result : (hashCode = userId != null ? userId.hashCode() : 0);
  }

  public static final class Builder extends Message.Builder<GameTestRequest> {

    public Long userId;

    public Builder() {
    }

    public Builder(GameTestRequest message) {
      super(message);
      if (message == null) return;
      this.userId = message.userId;
    }

    public Builder userId(Long userId) {
      this.userId = userId;
      return this;
    }

    @Override
    public GameTestRequest build() {
      checkRequiredFields();
      return new GameTestRequest(this);
    }
  }
}
