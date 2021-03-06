// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: src/main/resources/proto/LearningAbility.proto
package com.xcsd.rpc.proto;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Datatype.DOUBLE;
import static com.squareup.wire.Message.Datatype.INT32;
import static com.squareup.wire.Message.Label.REPEATED;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class AbilityStatResponse extends Message {
  private static final long serialVersionUID = 0L;

  public static final List<AbilityDetail> DEFAULT_DETAILS = Collections.emptyList();
  public static final Integer DEFAULT_TOTALABILITYLEVEL = 0;
  public static final Double DEFAULT_TOTALABILITYPERCENTAGE = 0D;
  public static final Integer DEFAULT_ABILITYQUOTIENT = 0;
  public static final Integer DEFAULT_MAXABILITYQUOTIENT = 0;
  public static final List<Point> DEFAULT_ABILITYCHART = Collections.emptyList();

  /**
   * 学习能力详情
   */
  @ProtoField(tag = 1, label = REPEATED, messageType = AbilityDetail.class)
  public final List<AbilityDetail> details;

  /**
   * 学能等级总和
   */
  @ProtoField(tag = 2, type = INT32, label = REQUIRED)
  public final Integer totalAbilityLevel;

  /**
   * 学能等级总和超过百分比
   */
  @ProtoField(tag = 3, type = DOUBLE, label = REQUIRED)
  public final Double totalAbilityPercentage;

  /**
   * 学能商数
   */
  @ProtoField(tag = 4, type = INT32, label = REQUIRED)
  public final Integer abilityQuotient;

  /**
   * 学能商数满分
   */
  @ProtoField(tag = 5, type = INT32, label = REQUIRED)
  public final Integer maxAbilityQuotient;

  /**
   * 学能商数图表
   */
  @ProtoField(tag = 6, label = REPEATED, messageType = Point.class)
  public final List<Point> abilityChart;

  public AbilityStatResponse(List<AbilityDetail> details, Integer totalAbilityLevel, Double totalAbilityPercentage, Integer abilityQuotient, Integer maxAbilityQuotient, List<Point> abilityChart) {
    this.details = immutableCopyOf(details);
    this.totalAbilityLevel = totalAbilityLevel;
    this.totalAbilityPercentage = totalAbilityPercentage;
    this.abilityQuotient = abilityQuotient;
    this.maxAbilityQuotient = maxAbilityQuotient;
    this.abilityChart = immutableCopyOf(abilityChart);
  }

  private AbilityStatResponse(Builder builder) {
    this(builder.details, builder.totalAbilityLevel, builder.totalAbilityPercentage, builder.abilityQuotient, builder.maxAbilityQuotient, builder.abilityChart);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof AbilityStatResponse)) return false;
    AbilityStatResponse o = (AbilityStatResponse) other;
    return equals(details, o.details)
        && equals(totalAbilityLevel, o.totalAbilityLevel)
        && equals(totalAbilityPercentage, o.totalAbilityPercentage)
        && equals(abilityQuotient, o.abilityQuotient)
        && equals(maxAbilityQuotient, o.maxAbilityQuotient)
        && equals(abilityChart, o.abilityChart);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = details != null ? details.hashCode() : 1;
      result = result * 37 + (totalAbilityLevel != null ? totalAbilityLevel.hashCode() : 0);
      result = result * 37 + (totalAbilityPercentage != null ? totalAbilityPercentage.hashCode() : 0);
      result = result * 37 + (abilityQuotient != null ? abilityQuotient.hashCode() : 0);
      result = result * 37 + (maxAbilityQuotient != null ? maxAbilityQuotient.hashCode() : 0);
      result = result * 37 + (abilityChart != null ? abilityChart.hashCode() : 1);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<AbilityStatResponse> {

    public List<AbilityDetail> details;
    public Integer totalAbilityLevel;
    public Double totalAbilityPercentage;
    public Integer abilityQuotient;
    public Integer maxAbilityQuotient;
    public List<Point> abilityChart;

    public Builder() {
    }

    public Builder(AbilityStatResponse message) {
      super(message);
      if (message == null) return;
      this.details = copyOf(message.details);
      this.totalAbilityLevel = message.totalAbilityLevel;
      this.totalAbilityPercentage = message.totalAbilityPercentage;
      this.abilityQuotient = message.abilityQuotient;
      this.maxAbilityQuotient = message.maxAbilityQuotient;
      this.abilityChart = copyOf(message.abilityChart);
    }

    /**
     * 学习能力详情
     */
    public Builder details(List<AbilityDetail> details) {
      this.details = checkForNulls(details);
      return this;
    }

    /**
     * 学能等级总和
     */
    public Builder totalAbilityLevel(Integer totalAbilityLevel) {
      this.totalAbilityLevel = totalAbilityLevel;
      return this;
    }

    /**
     * 学能等级总和超过百分比
     */
    public Builder totalAbilityPercentage(Double totalAbilityPercentage) {
      this.totalAbilityPercentage = totalAbilityPercentage;
      return this;
    }

    /**
     * 学能商数
     */
    public Builder abilityQuotient(Integer abilityQuotient) {
      this.abilityQuotient = abilityQuotient;
      return this;
    }

    /**
     * 学能商数满分
     */
    public Builder maxAbilityQuotient(Integer maxAbilityQuotient) {
      this.maxAbilityQuotient = maxAbilityQuotient;
      return this;
    }

    /**
     * 学能商数图表
     */
    public Builder abilityChart(List<Point> abilityChart) {
      this.abilityChart = checkForNulls(abilityChart);
      return this;
    }

    @Override
    public AbilityStatResponse build() {
      checkRequiredFields();
      return new AbilityStatResponse(this);
    }
  }

  public static final class Point extends Message {
    private static final long serialVersionUID = 0L;

    public static final Integer DEFAULT_NUMBER = 0;
    public static final Integer DEFAULT_SCORE = 0;

    @ProtoField(tag = 1, type = INT32, label = REQUIRED)
    public final Integer number;

    @ProtoField(tag = 2, type = INT32, label = REQUIRED)
    public final Integer score;

    public Point(Integer number, Integer score) {
      this.number = number;
      this.score = score;
    }

    private Point(Builder builder) {
      this(builder.number, builder.score);
      setBuilder(builder);
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (!(other instanceof Point)) return false;
      Point o = (Point) other;
      return equals(number, o.number)
          && equals(score, o.score);
    }

    @Override
    public int hashCode() {
      int result = hashCode;
      if (result == 0) {
        result = number != null ? number.hashCode() : 0;
        result = result * 37 + (score != null ? score.hashCode() : 0);
        hashCode = result;
      }
      return result;
    }

    public static final class Builder extends Message.Builder<Point> {

      public Integer number;
      public Integer score;

      public Builder() {
      }

      public Builder(Point message) {
        super(message);
        if (message == null) return;
        this.number = message.number;
        this.score = message.score;
      }

      public Builder number(Integer number) {
        this.number = number;
        return this;
      }

      public Builder score(Integer score) {
        this.score = score;
        return this;
      }

      @Override
      public Point build() {
        checkRequiredFields();
        return new Point(this);
      }
    }
  }
}
