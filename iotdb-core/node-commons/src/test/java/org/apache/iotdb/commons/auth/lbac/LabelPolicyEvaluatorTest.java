package org.apache.iotdb.commons.auth.lbac;

import org.apache.iotdb.commons.auth.entity.SecurityLabel;
import org.apache.iotdb.commons.exception.LBACException;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class LabelPolicyEvaluatorTest {

  @Test
  public void testBasicStringLabelMatching() throws LBACException {
    // 创建安全标签
    Map<String, String> labelMap = new HashMap<>();
    labelMap.put("region", "CN");
    labelMap.put("level", "secret");
    SecurityLabel securityLabel = new SecurityLabel(labelMap);

    // 测试基本的相等匹配
    LabelPolicyEvaluator evaluator = new LabelPolicyEvaluator("region = \"CN\"");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试不相等匹配
    evaluator = new LabelPolicyEvaluator("region != \"US\"");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试不存在的标签
    evaluator = new LabelPolicyEvaluator("department = \"RD\"");
    Assert.assertFalse(evaluator.evaluate(securityLabel));

    // 测试大小写敏感
    evaluator = new LabelPolicyEvaluator("region = \"cn\"");
    Assert.assertFalse(evaluator.evaluate(securityLabel));
  }

  @Test
  public void testNumericLabelMatching() throws LBACException {
    // 创建带数字值的安全标签
    Map<String, String> labelMap = new HashMap<>();
    labelMap.put("age", "25");
    labelMap.put("score", "85.5");
    SecurityLabel securityLabel = new SecurityLabel(labelMap);

    // 测试等于
    LabelPolicyEvaluator evaluator = new LabelPolicyEvaluator("age = 25");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试不等于
    evaluator = new LabelPolicyEvaluator("age != 30");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试大于
    evaluator = new LabelPolicyEvaluator("age > 20");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试小于
    evaluator = new LabelPolicyEvaluator("age < 30");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试大于等于
    evaluator = new LabelPolicyEvaluator("age >= 25");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试小于等于
    evaluator = new LabelPolicyEvaluator("age <= 25");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试浮点数比较
    evaluator = new LabelPolicyEvaluator("score > 85.0");
    Assert.assertTrue(evaluator.evaluate(securityLabel));
  }

  @Test
  public void testOperatorSpacing() throws LBACException {
    // 创建安全标签
    Map<String, String> labelMap = new HashMap<>();
    labelMap.put("age", "25");
    SecurityLabel securityLabel = new SecurityLabel(labelMap);

    // 测试运算符中间有空格的情况
    LabelPolicyEvaluator evaluator = new LabelPolicyEvaluator("age > = 20");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    evaluator = new LabelPolicyEvaluator("age < = 30");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    evaluator = new LabelPolicyEvaluator("age ! = 30");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试多个空格
    evaluator = new LabelPolicyEvaluator("age >  = 20");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    evaluator = new LabelPolicyEvaluator("age <   = 30");
    Assert.assertTrue(evaluator.evaluate(securityLabel));
  }

  @Test
  public void testLogicalOperators() throws LBACException {
    // 创建安全标签
    Map<String, String> labelMap = new HashMap<>();
    labelMap.put("region", "CN");
    labelMap.put("level", "secret");
    labelMap.put("department", "RD");
    SecurityLabel securityLabel = new SecurityLabel(labelMap);

    // 测试AND操作
    LabelPolicyEvaluator evaluator = new LabelPolicyEvaluator("region = \"CN\" and level = \"secret\"");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试OR操作
    evaluator = new LabelPolicyEvaluator("region = \"US\" or department = \"RD\"");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试复杂嵌套表达式
    evaluator = new LabelPolicyEvaluator("region = \"CN\" and (level = \"secret\" or department = \"HR\")");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试多个AND操作
    evaluator = new LabelPolicyEvaluator("region = \"CN\" and level = \"secret\" and department = \"RD\"");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试多个OR操作
    evaluator = new LabelPolicyEvaluator("region = \"US\" or level = \"secret\" or department = \"HR\"");
    Assert.assertTrue(evaluator.evaluate(securityLabel));
  }

  @Test
  public void testCaseInsensitiveKeywords() throws LBACException {
    // 创建安全标签
    Map<String, String> labelMap = new HashMap<>();
    labelMap.put("region", "CN");
    labelMap.put("level", "secret");
    SecurityLabel securityLabel = new SecurityLabel(labelMap);

    // 测试AND/OR关键字大小写不敏感
    LabelPolicyEvaluator evaluator = new LabelPolicyEvaluator("region = \"CN\" AND level = \"secret\"");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    evaluator = new LabelPolicyEvaluator("region = \"US\" OR level = \"secret\"");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    evaluator = new LabelPolicyEvaluator("region = \"CN\" And level = \"secret\"");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    evaluator = new LabelPolicyEvaluator("region = \"US\" Or level = \"secret\"");
    Assert.assertTrue(evaluator.evaluate(securityLabel));
  }

  @Test
  public void testWhitespaceHandling() throws LBACException {
    // 创建安全标签
    Map<String, String> labelMap = new HashMap<>();
    labelMap.put("region", "CN");
    labelMap.put("level", "secret");
    SecurityLabel securityLabel = new SecurityLabel(labelMap);

    // 测试表达式中的额外空格
    LabelPolicyEvaluator evaluator = new LabelPolicyEvaluator(
        "  region   =    \"CN\"    and    level   =   \"secret\"  ");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试括号周围的空格
    evaluator = new LabelPolicyEvaluator(
        "region = \"CN\" and   (   level = \"secret\"   or   department = \"HR\"   )");
    Assert.assertTrue(evaluator.evaluate(securityLabel));
  }

  @Test(expected = LBACException.class)
  public void testEmptyExpression() throws LBACException {
    new LabelPolicyEvaluator("");
  }

  @Test(expected = LBACException.class)
  public void testNullExpression() throws LBACException {
    new LabelPolicyEvaluator(null);
  }

  @Test(expected = LBACException.class)
  public void testInvalidLabelName() throws LBACException {
    new LabelPolicyEvaluator("123region = \"CN\"");
  }

  @Test(expected = LBACException.class)
  public void testMissingQuotes() throws LBACException {
    new LabelPolicyEvaluator("region = CN");
  }

  @Test(expected = LBACException.class)
  public void testInvalidNumericValue() throws LBACException {
    new LabelPolicyEvaluator("age > abc");
  }

  @Test
  public void testNullSecurityLabel() throws LBACException {
    // 测试空策略
    LabelPolicyEvaluator evaluator = new LabelPolicyEvaluator("region = \"CN\"");
    Assert.assertFalse(evaluator.evaluate(null));
  }

  @Test
  public void testUnsetLabels() throws LBACException {
    // 创建未设置标签的安全标签
    SecurityLabel securityLabel = new SecurityLabel();

    // 测试基本的相等匹配
    LabelPolicyEvaluator evaluator = new LabelPolicyEvaluator("region = \"CN\"");
    Assert.assertFalse(evaluator.evaluate(securityLabel));

    // 测试复杂表达式
    evaluator = new LabelPolicyEvaluator("region = \"CN\" and level = \"secret\"");
    Assert.assertFalse(evaluator.evaluate(securityLabel));
  }

  @Test
  public void testEmptySecurityLabel() throws LBACException {
    // 创建空标签映射的安全标签
    SecurityLabel securityLabel = new SecurityLabel(new HashMap<>());

    // 测试基本的相等匹配
    LabelPolicyEvaluator evaluator = new LabelPolicyEvaluator("region = \"CN\"");
    Assert.assertFalse(evaluator.evaluate(securityLabel));

    // 测试复杂表达式
    evaluator = new LabelPolicyEvaluator("region = \"CN\" and level = \"secret\"");
    Assert.assertFalse(evaluator.evaluate(securityLabel));
  }

  @Test
  public void testMaxPolicyLength() throws LBACException {
    // 创建一个超长的策略表达式
    StringBuilder longExpression = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      if (i > 0) {
        longExpression.append(" and ");
      }
      longExpression.append("label").append(i).append(" = \"value").append(i).append("\"");
    }

    try {
      new LabelPolicyEvaluator(longExpression.toString());
      Assert.fail("应该抛出异常：策略表达式过长");
    } catch (LBACException e) {
      // 预期会抛出异常
      Assert.assertTrue(e.getMessage().contains("策略表达式长度超过最大限制"));
    }
  }
}
