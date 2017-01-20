// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: src/main/resources/proto/LearningAbility.proto
package com.xcsd.rpc.proto;

import com.squareup.wire.ProtoEnum;

public enum Ability
    implements ProtoEnum {
  /**
   * 注意力
   */
  Attention(1),
  /**
   * 记忆力
   */
  Memory(2),
  /**
   * 反应力
   */
  Reaction(3),
  /**
   * 逻辑力
   */
  Reasoning(4),
  /**
   * 空间思维
   */
  SpatialThinking(5);

  private final int value;

  Ability(int value) {
    this.value = value;
  }

  @Override
  public int getValue() {
    return value;
  }
}
