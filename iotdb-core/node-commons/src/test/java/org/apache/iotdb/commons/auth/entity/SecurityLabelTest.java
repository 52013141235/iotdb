package org.apache.iotdb.commons.auth.entity;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/** 安全标签（SecurityLabel）的单元测试类 主要测试标签的创建、修改、查询和比较等基本操作 */
public class SecurityLabelTest {

  /** 测试空构造函数 验证新创建的标签是否为空 */
  @Test
  public void testEmptyConstructor() {
    SecurityLabel label = new SecurityLabel();
    Assert.assertTrue(label.isEmpty());
    Assert.assertTrue(label.getLabels().isEmpty());
  }

  /** 测试使用Map构造标签 验证通过Map创建的标签是否包含正确的键值对 */
  @Test
  public void testMapConstructor() {
    Map<String, String> labels = new HashMap<>();
    labels.put("region", "CN");
    labels.put("level", "secret");

    SecurityLabel label = new SecurityLabel(labels);
    Assert.assertFalse(label.isEmpty());
    Assert.assertEquals(2, label.getLabels().size());
    Assert.assertEquals("CN", label.getLabel("region"));
    Assert.assertEquals("secret", label.getLabel("level"));
  }

  /** 测试使用null Map构造标签时的异常处理 */
  @Test(expected = IllegalArgumentException.class)
  public void testNullMapConstructor() {
    new SecurityLabel(null);
  }

  /** 测试设置标签集合 验证是否能正确设置多个标签 */
  @Test
  public void testSetLabels() {
    SecurityLabel label = new SecurityLabel();

    Map<String, String> labels = new HashMap<>();
    labels.put("env", "prod");
    labels.put("department", "IT");

    label.setLabels(labels);
    Assert.assertFalse(label.isEmpty());
    Assert.assertEquals(2, label.getLabels().size());
    Assert.assertEquals("prod", label.getLabel("env"));
    Assert.assertEquals("IT", label.getLabel("department"));
  }

  /** 测试设置null标签集合时的异常处理 */
  @Test(expected = IllegalArgumentException.class)
  public void testSetNullLabels() {
    SecurityLabel label = new SecurityLabel();
    label.setLabels(null);
  }

  /** 测试添加单个标签 验证是否能正确添加一个标签 */
  @Test
  public void testAddLabel() {
    SecurityLabel label = new SecurityLabel();
    label.addLabel("region", "EU");

    Assert.assertFalse(label.isEmpty());
    Assert.assertEquals(1, label.getLabels().size());
    Assert.assertEquals("EU", label.getLabel("region"));
  }

  /** 测试添加标签时使用null键的异常处理 */
  @Test(expected = IllegalArgumentException.class)
  public void testAddLabelWithNullKey() {
    SecurityLabel label = new SecurityLabel();
    label.addLabel(null, "value");
  }

  /** 测试添加标签时使用空键的异常处理 */
  @Test(expected = IllegalArgumentException.class)
  public void testAddLabelWithEmptyKey() {
    SecurityLabel label = new SecurityLabel();
    label.addLabel("", "value");
  }

  /** 测试添加标签时使用null值的异常处理 */
  @Test(expected = IllegalArgumentException.class)
  public void testAddLabelWithNullValue() {
    SecurityLabel label = new SecurityLabel();
    label.addLabel("key", null);
  }

  /** 测试添加标签时使用空值的异常处理 */
  @Test(expected = IllegalArgumentException.class)
  public void testAddLabelWithEmptyValue() {
    SecurityLabel label = new SecurityLabel();
    label.addLabel("key", "");
  }

  /** 测试获取标签值 验证是否能正确获取已存在和不存在的标签值 */
  @Test
  public void testGetLabel() {
    SecurityLabel label = new SecurityLabel();
    label.addLabel("region", "CN");

    Assert.assertEquals("CN", label.getLabel("region"));
    Assert.assertNull(label.getLabel("nonexistent"));
  }

  /** 测试获取标签值时使用null键的异常处理 */
  @Test(expected = IllegalArgumentException.class)
  public void testGetLabelWithNullKey() {
    SecurityLabel label = new SecurityLabel();
    label.getLabel(null);
  }

  /** 测试获取标签值时使用空键的异常处理 */
  @Test(expected = IllegalArgumentException.class)
  public void testGetLabelWithEmptyKey() {
    SecurityLabel label = new SecurityLabel();
    label.getLabel("");
  }

  /** 测试清空标签 验证清空操作后标签集合是否为空 */
  @Test
  public void testClear() {
    SecurityLabel label = new SecurityLabel();
    label.addLabel("region", "CN");
    label.addLabel("level", "secret");

    Assert.assertFalse(label.isEmpty());
    label.clear();
    Assert.assertTrue(label.isEmpty());
  }

  /** 测试标签对象的相等性比较 验证具有相同和不同内容的标签对象的比较结果 */
  @Test
  public void testEquals() {
    Map<String, String> labels1 = new HashMap<>();
    labels1.put("region", "CN");
    labels1.put("level", "secret");
    SecurityLabel label1 = new SecurityLabel(labels1);

    Map<String, String> labels2 = new HashMap<>();
    labels2.put("region", "CN");
    labels2.put("level", "secret");
    SecurityLabel label2 = new SecurityLabel(labels2);

    Map<String, String> labels3 = new HashMap<>();
    labels3.put("region", "US");
    labels3.put("level", "secret");
    SecurityLabel label3 = new SecurityLabel(labels3);

    Assert.assertEquals(label1, label2);
    Assert.assertNotEquals(label1, label3);
    Assert.assertNotEquals(label1, null);
    Assert.assertNotEquals(label1, new Object());
  }

  /** 测试标签对象的哈希码 验证相同内容的标签对象是否具有相同的哈希码 */
  @Test
  public void testHashCode() {
    Map<String, String> labels1 = new HashMap<>();
    labels1.put("region", "CN");
    SecurityLabel label1 = new SecurityLabel(labels1);

    Map<String, String> labels2 = new HashMap<>();
    labels2.put("region", "CN");
    SecurityLabel label2 = new SecurityLabel(labels2);

    Assert.assertEquals(label1.hashCode(), label2.hashCode());
  }

  /** 测试标签对象的字符串表示 验证toString方法是否包含所有必要的标签信息 */
  @Test
  public void testToString() {
    Map<String, String> labels = new HashMap<>();
    labels.put("region", "CN");
    labels.put("level", "secret");
    SecurityLabel label = new SecurityLabel(labels);

    String str = label.toString();
    Assert.assertTrue(str.contains("Security_Label"));
    Assert.assertTrue(str.contains("region"));
    Assert.assertTrue(str.contains("CN"));
    Assert.assertTrue(str.contains("level"));
    Assert.assertTrue(str.contains("secret"));
  }
}
