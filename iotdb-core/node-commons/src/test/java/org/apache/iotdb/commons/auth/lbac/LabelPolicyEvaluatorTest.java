package org.apache.iotdb.commons.auth.lbac;

import org.apache.iotdb.commons.auth.entity.SecurityLabel;
import org.apache.iotdb.commons.exception.LBACException;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class LabelPolicyEvaluatorTest {

  @Test
  public void testBasicLabelMatching() throws LBACException {
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
  }

  @Test
  public void testComplexExpression() throws LBACException {
    // 创建安全标签
    Map<String, String> labelMap = new HashMap<>();
    labelMap.put("region", "CN");
    labelMap.put("level", "secret");
    labelMap.put("department", "RD");
    SecurityLabel securityLabel = new SecurityLabel(labelMap);

    // 测试AND操作
    LabelPolicyEvaluator evaluator =
        new LabelPolicyEvaluator("region = \"CN\" and level = \"secret\"");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试OR操作
    evaluator = new LabelPolicyEvaluator("region = \"US\" or department = \"RD\"");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试复杂嵌套表达式
    evaluator =
        new LabelPolicyEvaluator("region = \"CN\" and (level = \"secret\" or department = \"HR\")");
    Assert.assertTrue(evaluator.evaluate(securityLabel));
  }

  @Test
  public void testCaseSensitivity() throws LBACException {
    // 创建安全标签
    Map<String, String> labelMap = new HashMap<>();
    labelMap.put("Region", "CN");
    labelMap.put("level", "Secret");
    SecurityLabel securityLabel = new SecurityLabel(labelMap);

    // 测试语法关键字大小写不敏感
    LabelPolicyEvaluator evaluator =
        new LabelPolicyEvaluator("Region = \"CN\" AND level = \"Secret\"");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试标签值大小写敏感
    evaluator = new LabelPolicyEvaluator("Region = \"cn\"");
    Assert.assertFalse(evaluator.evaluate(securityLabel));

    evaluator = new LabelPolicyEvaluator("level = \"secret\"");
    Assert.assertFalse(evaluator.evaluate(securityLabel));
  }

  @Test
  public void testEmptyPolicyAndLabel() throws LBACException {
    // 创建空标签
    SecurityLabel emptyLabel = new SecurityLabel(new HashMap<>());

    // 创建空策略
    LabelPolicyEvaluator emptyEvaluator = new LabelPolicyEvaluator("");

    // 测试无策略且无标签
    Assert.assertTrue(emptyEvaluator.evaluate(emptyLabel));

    // 测试有策略但无标签
    LabelPolicyEvaluator evaluator = new LabelPolicyEvaluator("region = \"CN\"");
    Assert.assertFalse(evaluator.evaluate(emptyLabel));

    // 测试无策略但有标签
    Map<String, String> labelMap = new HashMap<>();
    labelMap.put("region", "CN");
    SecurityLabel securityLabel = new SecurityLabel(labelMap);
    Assert.assertTrue(emptyEvaluator.evaluate(securityLabel));
  }

  @Test(expected = LBACException.class)
  public void testInvalidExpression() throws LBACException {
    // 测试无效的表达式
    LabelPolicyEvaluator evaluator = new LabelPolicyEvaluator("region = CN"); // 缺少引号
    evaluator.evaluate(new SecurityLabel(new HashMap<>()));
  }

  @Test
  public void testComplexLogicalOperations() throws LBACException {
    // 创建安全标签
    Map<String, String> labelMap = new HashMap<>();
    labelMap.put("region", "CN");
    labelMap.put("level", "secret");
    labelMap.put("department", "RD");
    SecurityLabel securityLabel = new SecurityLabel(labelMap);

    // 测试多个AND操作
    LabelPolicyEvaluator evaluator =
        new LabelPolicyEvaluator("region = \"CN\" and level = \"secret\" and department = \"RD\"");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试多个OR操作
    evaluator =
        new LabelPolicyEvaluator("region = \"US\" or level = \"secret\" or department = \"HR\"");
    Assert.assertTrue(evaluator.evaluate(securityLabel));

    // 测试混合AND和OR操作
    evaluator =
        new LabelPolicyEvaluator(
            "(region = \"CN\" or region = \"US\") and (level = \"secret\" or level = \"top-secret\")");
    Assert.assertTrue(evaluator.evaluate(securityLabel));
  }
}
