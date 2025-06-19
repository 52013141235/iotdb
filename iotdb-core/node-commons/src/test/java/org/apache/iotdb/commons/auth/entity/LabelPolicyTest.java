package org.apache.iotdb.commons.auth.entity;

import org.junit.Assert;
import org.junit.Test;

/** 标签策略（LabelPolicy）的单元测试类 主要测试策略的创建、修改、权限设置和比较等基本操作 */
public class LabelPolicyTest {

  /** 测试默认构造函数 验证新创建的策略的读写权限默认值是否为false */
  @Test
  public void testDefaultConstructor() {
    LabelPolicy policy = new LabelPolicy();
    Assert.assertFalse(policy.isForRead());
    Assert.assertFalse(policy.isForWrite());
    Assert.assertNull(policy.getPolicyExpression());
    Assert.assertTrue(policy.isEmpty());
  }

  /** 测试带参数的构造函数 验证通过表达式和权限标志创建的策略是否正确设置 */
  @Test
  public void testConstructorWithParameters() {
    LabelPolicy policy = new LabelPolicy("level = \"secret\"", true, false);
    Assert.assertEquals("level = \"secret\"", policy.getPolicyExpression());
    Assert.assertTrue(policy.isForRead());
    Assert.assertFalse(policy.isForWrite());
    Assert.assertFalse(policy.isEmpty());
  }

  /** 测试使用null表达式构造策略 */
  @Test
  public void testConstructorWithNullExpression() {
    LabelPolicy policy = new LabelPolicy(null, true, true);
    Assert.assertNull(policy.getPolicyExpression());
    Assert.assertTrue(policy.isEmpty());
    Assert.assertTrue(policy.isForRead());
    Assert.assertTrue(policy.isForWrite());
  }

  /** 测试使用空字符串表达式构造策略时的异常处理 */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithEmptyExpression() {
    new LabelPolicy("", true, true);
  }

  /** 测试使用空白字符串表达式构造策略时的异常处理 */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithBlankExpression() {
    new LabelPolicy("   ", true, true);
  }

  /** 测试设置策略表达式 验证是否能正确设置策略表达式 */
  @Test
  public void testSetPolicyExpression() {
    LabelPolicy policy = new LabelPolicy();
    policy.setPolicyExpression("department = \"IT\"");
    Assert.assertEquals("department = \"IT\"", policy.getPolicyExpression());
    Assert.assertFalse(policy.isEmpty());
  }

  /** 测试设置null策略表达式 */
  @Test
  public void testSetNullPolicyExpression() {
    LabelPolicy policy = new LabelPolicy("level = \"secret\"", true, true);
    policy.setPolicyExpression(null);
    Assert.assertNull(policy.getPolicyExpression());
    Assert.assertTrue(policy.isEmpty());
  }

  /** 测试设置空字符串策略表达式时的异常处理 */
  @Test(expected = IllegalArgumentException.class)
  public void testSetEmptyPolicyExpression() {
    LabelPolicy policy = new LabelPolicy();
    policy.setPolicyExpression("");
  }

  /** 测试设置空白字符串策略表达式时的异常处理 */
  @Test(expected = IllegalArgumentException.class)
  public void testSetBlankPolicyExpression() {
    LabelPolicy policy = new LabelPolicy();
    policy.setPolicyExpression("  ");
  }

  /** 测试设置读权限 验证是否能正确设置和修改读权限标志 */
  @Test
  public void testSetForRead() {
    LabelPolicy policy = new LabelPolicy();
    policy.setPolicyExpression("level = \"secret\"");
    Assert.assertFalse(policy.isForRead());

    policy.setForRead(true);
    Assert.assertTrue(policy.isForRead());

    policy.setForRead(false);
    Assert.assertFalse(policy.isForRead());
  }

  /** 测试设置写权限 验证是否能正确设置和修改写权限标志 */
  @Test
  public void testSetForWrite() {
    LabelPolicy policy = new LabelPolicy();
    policy.setPolicyExpression("level = \"secret\"");
    Assert.assertFalse(policy.isForWrite());

    policy.setForWrite(true);
    Assert.assertTrue(policy.isForWrite());

    policy.setForWrite(false);
    Assert.assertFalse(policy.isForWrite());
  }

  /** 测试策略对象的相等性比较 验证具有相同和不同内容的策略对象的比较结果 */
  @Test
  public void testEquals() {
    LabelPolicy policy1 = new LabelPolicy("level = \"secret\"", true, false);
    LabelPolicy policy2 = new LabelPolicy("level = \"secret\"", true, false);
    LabelPolicy policy3 = new LabelPolicy("level = \"confidential\"", true, false);
    LabelPolicy policy4 = new LabelPolicy("level = \"secret\"", false, true);
    LabelPolicy policy5 = new LabelPolicy(null, true, false);
    LabelPolicy policy6 = new LabelPolicy(null, true, false);

    Assert.assertEquals(policy1, policy2);
    Assert.assertNotEquals(policy1, policy3);
    Assert.assertNotEquals(policy1, policy4);
    Assert.assertNotEquals(policy1, policy5);
    Assert.assertEquals(policy5, policy6);
    Assert.assertNotEquals(policy1, null);
    Assert.assertNotEquals(policy1, new Object());
  }

  /** 测试策略对象的哈希码 验证相同内容的策略对象是否具有相同的哈希码 */
  @Test
  public void testHashCode() {
    LabelPolicy policy1 = new LabelPolicy("level = \"secret\"", true, false);
    LabelPolicy policy2 = new LabelPolicy("level = \"secret\"", true, false);
    LabelPolicy policy3 = new LabelPolicy(null, true, false);
    LabelPolicy policy4 = new LabelPolicy(null, true, false);

    Assert.assertEquals(policy1.hashCode(), policy2.hashCode());
    Assert.assertEquals(policy3.hashCode(), policy4.hashCode());
  }

  /** 测试策略对象的字符串表示 验证toString方法是否包含所有必要的策略信息 */
  @Test
  public void testToString() {
    LabelPolicy policy1 = new LabelPolicy("level = \"secret\"", true, false);
    String str1 = policy1.toString();
    Assert.assertTrue(str1.contains("policyExpression"));
    Assert.assertTrue(str1.contains("level = \"secret\""));
    Assert.assertTrue(str1.contains("forRead=true"));
    Assert.assertTrue(str1.contains("forWrite=false"));

    LabelPolicy policy2 = new LabelPolicy(null, true, false);
    String str2 = policy2.toString();
    Assert.assertTrue(str2.contains("policyExpression = null"));
    Assert.assertTrue(str2.contains("forRead=true"));
    Assert.assertTrue(str2.contains("forWrite=false"));
  }

  /** 测试策略表达式的空格处理 验证是否能正确处理表达式中的前后空格 */
  @Test
  public void testTrimPolicyExpression() {
    LabelPolicy policy = new LabelPolicy("  level = \"secret\"  ", true, false);
    Assert.assertEquals("level = \"secret\"", policy.getPolicyExpression());
  }
}
