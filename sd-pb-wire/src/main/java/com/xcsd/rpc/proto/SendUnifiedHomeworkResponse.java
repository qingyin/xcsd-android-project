// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: src/main/resources/proto/Homework.proto
package com.xcsd.rpc.proto;

import com.squareup.wire.Message;

public final class SendUnifiedHomeworkResponse extends Message {
  private static final long serialVersionUID = 0L;

  public SendUnifiedHomeworkResponse() {
  }

  private SendUnifiedHomeworkResponse(Builder builder) {
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof SendUnifiedHomeworkResponse;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public static final class Builder extends Message.Builder<SendUnifiedHomeworkResponse> {

    public Builder() {
    }

    public Builder(SendUnifiedHomeworkResponse message) {
      super(message);
    }

    @Override
    public SendUnifiedHomeworkResponse build() {
      return new SendUnifiedHomeworkResponse(this);
    }
  }
}
