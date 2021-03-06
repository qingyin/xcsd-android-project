// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: src/main/resources/proto/LearningAbility.proto
package com.xcsd.rpc.proto;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.ENUM;
import static com.squareup.wire.Message.Datatype.INT32;
import static com.squareup.wire.Message.Label.REQUIRED;

/**
 * 能力值
 */
public final class AbilityValue extends Message {
  private static final long serialVersionUID = 0L;

  public static final Ability DEFAULT_ABILITY = Ability.Attention;
  public static final Integer DEFAULT_VALUE = 0;

  @ProtoField(tag = 1, type = ENUM, label = REQUIRED)
  public final Ability ability;

  @ProtoField(tag = 2, type = INT32, label = REQUIRED)
  public final Integer value;

  public AbilityValue(Ability ability, Integer value) {
    this.ability = ability;
    this.value = value;
  }

  private AbilityValue(Builder builder) {
    this(builder.ability, builder.value);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof AbilityValue)) return false;
    AbilityValue o = (AbilityValue) other;
    return equals(ability, o.ability)
        && equals(value, o.value);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = ability != null ? ability.hashCode() : 0;
      result = result * 37 + (value != null ? value.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<AbilityValue> {

    public Ability ability;
    public Integer value;

    public Builder() {
    }

    public Builder(AbilityValue message) {
      super(message);
      if (message == null) return;
      this.ability = message.ability;
      this.value = message.value;
    }

    public Builder ability(Ability ability) {
      this.ability = ability;
      return this;
    }

    public Builder value(Integer value) {
      this.value = value;
      return this;
    }

    @Override
    public AbilityValue build() {
      checkRequiredFields();
      return new AbilityValue(this);
    }
  }
}
