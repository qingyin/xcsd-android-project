// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: src/main/resources/proto/Test.proto
package com.xcsd.rpc.proto;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Datatype.BOOL;
import static com.squareup.wire.Message.Datatype.INT64;
import static com.squareup.wire.Message.Label.REPEATED;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class GameTestResponse extends Message {
  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_TESTID = 0L;
  public static final Boolean DEFAULT_ISFIRSTTEST = false;
  public static final List<GameLevel> DEFAULT_GAMELEVEL = Collections.emptyList();
  public static final Long DEFAULT_USERID = 0L;

  @ProtoField(tag = 1, type = INT64, label = REQUIRED)
  public final Long testId;

  @ProtoField(tag = 2, type = BOOL, label = REQUIRED)
  public final Boolean isFirstTest;

  @ProtoField(tag = 3, label = REPEATED, messageType = GameLevel.class)
  public final List<GameLevel> gameLevel;

  /**
   * 测试人的用户ID
   */
  @ProtoField(tag = 4, type = INT64)
  public final Long userId;

  public GameTestResponse(Long testId, Boolean isFirstTest, List<GameLevel> gameLevel, Long userId) {
    this.testId = testId;
    this.isFirstTest = isFirstTest;
    this.gameLevel = immutableCopyOf(gameLevel);
    this.userId = userId;
  }

  private GameTestResponse(Builder builder) {
    this(builder.testId, builder.isFirstTest, builder.gameLevel, builder.userId);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof GameTestResponse)) return false;
    GameTestResponse o = (GameTestResponse) other;
    return equals(testId, o.testId)
        && equals(isFirstTest, o.isFirstTest)
        && equals(gameLevel, o.gameLevel)
        && equals(userId, o.userId);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = testId != null ? testId.hashCode() : 0;
      result = result * 37 + (isFirstTest != null ? isFirstTest.hashCode() : 0);
      result = result * 37 + (gameLevel != null ? gameLevel.hashCode() : 1);
      result = result * 37 + (userId != null ? userId.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<GameTestResponse> {

    public Long testId;
    public Boolean isFirstTest;
    public List<GameLevel> gameLevel;
    public Long userId;

    public Builder() {
    }

    public Builder(GameTestResponse message) {
      super(message);
      if (message == null) return;
      this.testId = message.testId;
      this.isFirstTest = message.isFirstTest;
      this.gameLevel = copyOf(message.gameLevel);
      this.userId = message.userId;
    }

    public Builder testId(Long testId) {
      this.testId = testId;
      return this;
    }

    public Builder isFirstTest(Boolean isFirstTest) {
      this.isFirstTest = isFirstTest;
      return this;
    }

    public Builder gameLevel(List<GameLevel> gameLevel) {
      this.gameLevel = checkForNulls(gameLevel);
      return this;
    }

    /**
     * 测试人的用户ID
     */
    public Builder userId(Long userId) {
      this.userId = userId;
      return this;
    }

    @Override
    public GameTestResponse build() {
      checkRequiredFields();
      return new GameTestResponse(this);
    }
  }
}
