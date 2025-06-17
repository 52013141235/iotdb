package org.apache.iotdb.commons.auth.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** 安全标签类，用于存储数据库对象的安全标签。 每个标签都是一个键值对，其中键是标签名称，值是标签值。 */
public class SecurityLabel implements Serializable {
  private static final long serialVersionUID = 1L;

  /** 存储标签的键值对 */
  private Map<String, String> labels;

  /** 默认构造函数 */
  public SecurityLabel() {
    this.labels = new HashMap<>();
  }

  /**
   * 使用给定的标签映射创建安全标签
   *
   * @param labels 标签映射
   * @throws IllegalArgumentException 如果标签映射为空
   */
  public SecurityLabel(Map<String, String> labels) {
    if (labels == null) {
      throw new IllegalArgumentException("标签映射不能为空");
    }
    this.labels = new HashMap<>(labels);
  }

  /**
   * 获取所有标签的副本
   *
   * @return 标签映射的副本
   */
  public Map<String, String> getLabels() {
    return new HashMap<>(labels);
  }

  /**
   * 设置标签映射
   *
   * @param labels 新的标签映射
   * @throws IllegalArgumentException 如果标签映射为空
   */
  public void setLabels(Map<String, String> labels) {
    if (labels == null) {
      throw new IllegalArgumentException("标签映射不能为空");
    }
    this.labels = new HashMap<>(labels);
  }

  /**
   * 添加一个新的标签
   *
   * @param key   标签名
   * @param value 标签值
   * @throws IllegalArgumentException 如果标签名或标签值为空
   */
  public void addLabel(String key, String value) {
    if (key == null || key.trim().isEmpty()) {
      throw new IllegalArgumentException("标签名不能为空");
    }
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("标签值不能为空");
    }
    labels.put(key, value);
  }

  /**
   * 获取指定标签的值
   *
   * @param key 标签名
   * @return 标签值，如果标签不存在则返回null
   * @throws IllegalArgumentException 如果标签名为空
   */
  public String getLabel(String key) {
    if (key == null || key.trim().isEmpty()) {
      throw new IllegalArgumentException("标签名不能为空");
    }
    return labels.get(key);
  }

  /**
   * 移除指定的标签
   *
   * @param key 要移除的标签名
   * @throws IllegalArgumentException 如果标签名为空
  */
  /* 
  public void removeLabel(String key) {
    if (key == null || key.trim().isEmpty()) {
      throw new IllegalArgumentException("标签名不能为空");
    }
    labels.remove(key);
  } 
  */

  /**
   * 检查是否存在指定的标签
   *
   * @param key 标签名
   * @return 如果标签存在则返回true，否则返回false
   * @throws IllegalArgumentException 如果标签名为空
   */
  /* 
  public boolean hasLabel(String key) {
    if (key == null || key.trim().isEmpty()) {
      throw new IllegalArgumentException("标签名不能为空");
    }
    return labels.containsKey(key);
  }
   */

  /** 清除所有标签 */
  public void clear() {
    labels.clear();
  }

  /**
   * 检查是否没有标签
   *
   * @return 如果没有标签则返回true，否则返回false
   */
  public boolean isEmpty() {
    return labels.isEmpty();
  }
      

      
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SecurityLabel that = (SecurityLabel) o;
    return Objects.equals(labels, that.labels);
  }

  @Override
  public int hashCode() {
    return Objects.hash(labels);
  }

  @Override
  public String toString() {
    return "Security_Label (" + labels + ')';
  }
}
