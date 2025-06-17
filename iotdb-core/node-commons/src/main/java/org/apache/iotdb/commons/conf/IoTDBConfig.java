package org.apache.iotdb.commons.conf;

/** IoTDB配置类 */
public class IoTDBConfig {
  // LBAC related configurations
  /** LBAC相关配置 */

  /** 是否启用标签访问控制 */
  private boolean enableLBAC = false;

  /** 标签键的最大长度 */
  private int maxLabelKeyLength = 64;

  /** 标签值的最大长度 */
  private int maxLabelValueLength = 256;

  /** 标签策略表达式的最大长度 */
  private int maxLabelPolicyLength = 1024;

  /** 每个数据库允许的最大标签数量 */
  private int maxLabelsPerDatabase = 20;

  /**
   * 获取是否启用标签访问控制
   *
   * @return 如果启用则返回true，否则返回false
   */
  public boolean isEnableLBAC() {
    return enableLBAC;
  }

  /**
   * 设置是否启用标签访问控制
   *
   * @param enableLBAC 是否启用标签访问控制
   */
  public void setEnableLBAC(boolean enableLBAC) {
    this.enableLBAC = enableLBAC;
  }

  /**
   * 获取标签键的最大长度
   *
   * @return 标签键的最大长度
   */
  public int getMaxLabelKeyLength() {
    return maxLabelKeyLength;
  }

  /**
   * 设置标签键的最大长度
   *
   * @param maxLabelKeyLength 标签键的最大长度
   */
  public void setMaxLabelKeyLength(int maxLabelKeyLength) {
    this.maxLabelKeyLength = maxLabelKeyLength;
  }

  /**
   * 获取标签值的最大长度
   *
   * @return 标签值的最大长度
   */
  public int getMaxLabelValueLength() {
    return maxLabelValueLength;
  }

  /**
   * 设置标签值的最大长度
   *
   * @param maxLabelValueLength 标签值的最大长度
   */
  public void setMaxLabelValueLength(int maxLabelValueLength) {
    this.maxLabelValueLength = maxLabelValueLength;
  }

  /**
   * 获取标签策略表达式的最大长度
   *
   * @return 标签策略表达式的最大长度
   */
  public int getMaxLabelPolicyLength() {
    return maxLabelPolicyLength;
  }

  /**
   * 设置标签策略表达式的最大长度
   *
   * @param maxLabelPolicyLength 标签策略表达式的最大长度
   */
  public void setMaxLabelPolicyLength(int maxLabelPolicyLength) {
    this.maxLabelPolicyLength = maxLabelPolicyLength;
  }

  /**
   * 获取每个数据库允许的最大标签数量
   *
   * @return 每个数据库允许的最大标签数量
   */
  public int getMaxLabelsPerDatabase() {
    return maxLabelsPerDatabase;
  }

  /**
   * 设置每个数据库允许的最大标签数量
   *
   * @param maxLabelsPerDatabase 每个数据库允许的最大标签数量
   */
  public void setMaxLabelsPerDatabase(int maxLabelsPerDatabase) {
    this.maxLabelsPerDatabase = maxLabelsPerDatabase;
  }
}
