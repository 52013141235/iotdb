package org.apache.iotdb.commons.auth.entity;

import java.io.Serializable;
import java.util.Objects;

/** 标签策略类，用于定义用户的标签访问控制策略。 可以用于读访问、写访问或两者都有。 */
public class LabelPolicy implements Serializable {
  private static final long serialVersionUID = 1L;

  /** 策略表达式，例如：level = "secret" and department = "hr"。可以为null表示空策略。 */
  private String policyExpression;

  /** 是否用于读访问 */
  private boolean forRead;

  /** 是否用于写访问 */
  private boolean forWrite;

  /** 默认构造函数 */
  public LabelPolicy() {}

  /**
   * 使用给定的参数创建标签策略
   *
   * @param policyExpression 策略表达式，可以为null表示空策略，但不能为空字符串
   * @param forRead 是否用于读访问
   * @param forWrite 是否用于写访问
   * @throws IllegalArgumentException 如果策略表达式为空字符串
   */
  public LabelPolicy(String policyExpression, boolean forRead, boolean forWrite) {
    setPolicyExpression(policyExpression);
    this.forRead = forRead;
    this.forWrite = forWrite;
  }

  /**
   * 获取策略表达式
   *
   * @return 策略表达式，可能为null表示空策略
   */
  public String getPolicyExpression() {
    return policyExpression;
  }

  /**
   * 设置策略表达式
   *
   * @param policyExpression 新的策略表达式，可以为null表示空策略，但不能为空字符串
   * @throws IllegalArgumentException 如果策略表达式为空字符串
   */
  public void setPolicyExpression(String policyExpression) {
    if (policyExpression != null) {
      String trimmed = policyExpression.trim();
      if (trimmed.isEmpty()) {
        throw new IllegalArgumentException("策略表达式不能为空字符串");
      }
      this.policyExpression = trimmed;
    } else {
      this.policyExpression = null;
    }
  }

  /**
   * 检查是否为空策略
   *
   * @return 如果策略表达式为null则返回true，否则返回false
   */
  public boolean isEmpty() {
    return policyExpression == null;
  }

  /**
   * 检查是否用于读访问
   *
   * @return 如果用于读访问则返回true，否则返回false
   */
  public boolean isForRead() {
    return forRead;
  }

  /**
   * 设置是否用于读访问
   *
   * @param forRead 是否用于读访问
   */
  public void setForRead(boolean forRead) {
    this.forRead = forRead;
  }

  /**
   * 检查是否用于写访问
   *
   * @return 如果用于写访问则返回true，否则返回false
   */
  public boolean isForWrite() {
    return forWrite;
  }

  /**
   * 设置是否用于写访问
   *
   * @param forWrite 是否用于写访问
   */
  public void setForWrite(boolean forWrite) {
    this.forWrite = forWrite;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LabelPolicy that = (LabelPolicy) o;
    return forRead == that.forRead
        && forWrite == that.forWrite
        && Objects.equals(policyExpression, that.policyExpression);
  }

  @Override
  public int hashCode() {
    return Objects.hash(policyExpression, forRead, forWrite);
  }

  @Override
  public String toString() {
    return "policyExpression = "
        + (policyExpression == null ? "null" : policyExpression)
        + ", forRead="
        + forRead
        + ", forWrite="
        + forWrite;
  }
}
