/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.commons.auth.lbac;

import org.apache.iotdb.commons.auth.entity.SecurityLabel;
import org.apache.iotdb.commons.exception.LBACException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 标签策略评估器，负责解析和评估标签策略表达式。 支持以下运算符： - 比较运算符：=、!=（适用于字符串标签和数字标签） - 比较运算符：>、<、>=、<=（仅适用于数字标签） -
 * 逻辑运算符：AND、OR - 优先级控制：()
 */
public class LabelPolicyEvaluator {
  /** 策略表达式最大长度 */
  private static final int MAX_POLICY_LENGTH = 1024;

  /** 逻辑操作符 */
  private static final String TOKEN_AND = "and";

  private static final String TOKEN_OR = "or";

  /** 比较操作符 */
  private static final String TOKEN_EQ = "=";

  private static final String TOKEN_NEQ = "!=";
  private static final String TOKEN_GT = ">";
  private static final String TOKEN_GTE = ">=";
  private static final String TOKEN_LT = "<";
  private static final String TOKEN_LTE = "<=";

  /** 标签表达式的正则表达式模式 - 字符串值（带双引号） */
  private static final Pattern STRING_LABEL_PATTERN =
      Pattern.compile("([a-zA-Z][a-zA-Z0-9_]*)\\s*(=|!=)\\s*\"([^\"]+)\"");

  /** 标签表达式的正则表达式模式 - 数字值（不带引号） */
  private static final Pattern NUMERIC_LABEL_PATTERN =
      Pattern.compile("([a-zA-Z][a-zA-Z0-9_]*)\\s*(=|!=|>|<|>=|<=)\\s*([0-9]+(?:\\.[0-9]+)?)");

  /** 策略表达式 */
  private final String policyExpression;

  /** 是否为空策略 */
  private final boolean isEmptyPolicy;

  /**
   * 创建标签策略评估器
   *
   * @param policyExpression 策略表达式
   * @throws LBACException 如果策略表达式无效
   */
  public LabelPolicyEvaluator(String policyExpression) throws LBACException {
    if (policyExpression == null || policyExpression.trim().isEmpty()) {
      throw new LBACException("策略表达式不能为空");
    }

    this.policyExpression = policyExpression.trim();
    if (this.policyExpression.length() > MAX_POLICY_LENGTH) {
      throw new LBACException("策略表达式长度超过最大限制" + MAX_POLICY_LENGTH + "字符");
    }

    this.isEmptyPolicy = false;

    // 在构造时验证表达式
    try {
      tokenize(this.policyExpression);
    } catch (LBACException e) {
      throw e;
    } catch (Exception e) {
      throw new LBACException("无效的策略表达式: " + e.getMessage());
    }
  }

  /**
   * 评估给定的安全标签是否匹配策略表达式 实现LBAC的访问控制规则： 1. 无策略且无标签：LBAC不介入（返回true） 2. 有策略但无标签：LBAC拒绝访问（返回false） 3.
   * 无策略但有标签：LBAC不介入（返回true） 4. 有策略且有标签：检查策略是否包含于标签
   *
   * @param securityLabel 要评估的安全标签
   * @return 如果访问允许则返回true，否则返回false
   * @throws LBACException 如果策略表达式无效
   */
  public boolean evaluate(SecurityLabel securityLabel) throws LBACException {
    // 处理无策略的情况
    if (isEmptyPolicy) {
      return true; // LBAC不介入
    }

    // 处理未设置标签的情况
    if (securityLabel == null) {
      return false; // 有策略但未设置标签，拒绝访问
    }

    // 处理空标签的情况
    if (securityLabel.getLabels() == null) {
      return false; // 有策略但未设置标签，拒绝访问
    }

    try {
      // 将表达式转换为后缀表达式并评估
      List<String> postfix = toPostfix(policyExpression);
      return evaluatePostfix(postfix, securityLabel);
    } catch (Exception e) {
      throw new LBACException("策略表达式评估失败: " + e.getMessage());
    }
  }

  /**
   * 将中缀表达式转换为后缀表达式（Shunting Yard算法）
   *
   * @param expression 中缀表达式
   * @return 后缀表达式的标记列表
   * @throws LBACException 如果表达式无效
   */
  private List<String> toPostfix(String expression) throws LBACException {
    List<String> output = new ArrayList<>();
    Deque<String> operators = new ArrayDeque<>();

    // 标记化表达式
    List<String> tokens = tokenize(expression);

    for (String token : tokens) {
      // 对操作符进行大小写不敏感的比较
      String lowerToken = token.toLowerCase();
      switch (lowerToken) {
        case "(":
          operators.push(token);
          break;
        case ")":
          while (!operators.isEmpty() && !operators.peek().equals("(")) {
            output.add(operators.pop());
          }
          if (!operators.isEmpty()) {
            operators.pop(); // 弹出 "("
          }
          break;
        case TOKEN_AND:
        case TOKEN_OR:
          while (!operators.isEmpty()
              && !operators.peek().equals("(")
              && getOperatorPrecedence(operators.peek().toLowerCase())
                  >= getOperatorPrecedence(lowerToken)) {
            output.add(operators.pop());
          }
          operators.push(lowerToken); // 统一使用小写的操作符
          break;
        default:
          output.add(token); // 保持标签表达式的原始大小写
          break;
      }
    }

    while (!operators.isEmpty()) {
      output.add(operators.pop());
    }

    return output;
  }

  /**
   * 获取操作符优先级
   *
   * @param operator 操作符（已转换为小写）
   * @return 优先级（数字越大优先级越高）
   */
  private int getOperatorPrecedence(String operator) {
    switch (operator) {
      case TOKEN_AND:
        return 2;
      case TOKEN_OR:
        return 1;
      default:
        return 0;
    }
  }

  /**
   * 将表达式分解为标记
   *
   * @param expression 表达式字符串
   * @return 标记列表
   * @throws LBACException 如果表达式无效
   */
  private List<String> tokenize(String expression) throws LBACException {
    List<String> tokens = new ArrayList<>();
    int lastEnd = 0;
    boolean hasLabelExpression = false;

    // 尝试匹配所有标签表达式（字符串和数字）
    while (true) {
      // 查找下一个字符串标签或数字标签的位置
      Matcher stringMatcher = STRING_LABEL_PATTERN.matcher(expression.substring(lastEnd));
      Matcher numericMatcher = NUMERIC_LABEL_PATTERN.matcher(expression.substring(lastEnd));

      int stringStart = stringMatcher.find() ? stringMatcher.start() : -1;
      int numericStart = numericMatcher.find() ? numericMatcher.start() : -1;

      // 如果两种模式都没有匹配到，退出循环
      if (stringStart == -1 && numericStart == -1) {
        break;
      }

      // 选择最先出现的匹配
      Matcher matcher;
      if (stringStart == -1 || (numericStart != -1 && numericStart < stringStart)) {
        matcher = numericMatcher;
      } else {
        matcher = stringMatcher;
      }

      hasLabelExpression = true;

      // 处理匹配前的操作符和括号
      String prefix = expression.substring(lastEnd, lastEnd + matcher.start()).trim();
      if (!prefix.isEmpty()) {
        for (String token : prefix.split("\\s+")) {
          if (!token.isEmpty()) {
            validateOperatorOrParenthesis(token);
            tokens.add(token);
          }
        }
      }

      // 添加标签表达式
      tokens.add(matcher.group(0));
      lastEnd += matcher.end();
    }

    // 处理剩余的操作符和括号
    String remaining = expression.substring(lastEnd).trim();
    if (!remaining.isEmpty()) {
      for (String token : remaining.split("\\s+")) {
        if (!token.isEmpty()) {
          validateOperatorOrParenthesis(token);
          tokens.add(token);
        }
      }
    }

    // 如果表达式不为空但没有找到任何标签表达式，则表达式无效
    if (!expression.isEmpty() && !hasLabelExpression) {
      throw new LBACException("无效的策略表达式：未找到有效的标签表达式");
    }

    return tokens;
  }

  /**
   * 验证操作符或括号的有效性
   *
   * @param token 要验证的标记
   * @throws LBACException 如果标记无效
   */
  private void validateOperatorOrParenthesis(String token) throws LBACException {
    String lowerToken = token.toLowerCase();
    if (!lowerToken.equals(TOKEN_AND)
        && !lowerToken.equals(TOKEN_OR)
        && !token.equals("(")
        && !token.equals(")")) {
      throw new LBACException("无效的操作符或括号：" + token);
    }
  }

  /**
   * 评估后缀表达式
   *
   * @param postfix 后缀表达式的标记列表
   * @param securityLabel 安全标签
   * @return 评估结果
   * @throws LBACException 如果表达式无效
   */
  private boolean evaluatePostfix(List<String> postfix, SecurityLabel securityLabel)
      throws LBACException {
    Deque<Boolean> stack = new ArrayDeque<>();

    for (String token : postfix) {
      // 对操作符进行大小写不敏感的比较
      String lowerToken = token.toLowerCase();
      switch (lowerToken) {
        case TOKEN_AND:
          if (stack.size() < 2) {
            throw new LBACException("无效的策略表达式：AND操作符缺少操作数");
          }
          boolean right = stack.pop();
          boolean left = stack.pop();
          stack.push(left && right);
          break;
        case TOKEN_OR:
          if (stack.size() < 2) {
            throw new LBACException("无效的策略表达式：OR操作符缺少操作数");
          }
          right = stack.pop();
          left = stack.pop();
          stack.push(left || right);
          break;
        default:
          stack.push(evaluateLabel(token, securityLabel));
          break;
      }
    }

    if (stack.size() != 1) {
      throw new LBACException("无效的策略表达式：操作符和操作数数量不匹配");
    }

    return stack.pop();
  }

  /**
   * 评估单个标签表达式
   *
   * @param expression 标签表达式
   * @param securityLabel 安全标签
   * @return 评估结果
   * @throws LBACException 如果表达式无效
   */
  private boolean evaluateLabel(String expression, SecurityLabel securityLabel)
      throws LBACException {
    // 尝试匹配字符串标签模式
    Matcher stringMatcher = STRING_LABEL_PATTERN.matcher(expression);
    if (stringMatcher.matches()) {
      return evaluateStringLabel(stringMatcher, securityLabel);
    }

    // 尝试匹配数字标签模式
    Matcher numericMatcher = NUMERIC_LABEL_PATTERN.matcher(expression);
    if (numericMatcher.matches()) {
      return evaluateNumericLabel(numericMatcher, securityLabel);
    }

    throw new LBACException("无效的标签表达式: " + expression);
  }

  /**
   * 评估字符串标签表达式
   *
   * @param matcher 正则表达式匹配器
   * @param securityLabel 安全标签
   * @return 评估结果
   * @throws LBACException 如果表达式无效
   */
  private boolean evaluateStringLabel(Matcher matcher, SecurityLabel securityLabel)
      throws LBACException {
    String labelKey = matcher.group(1);
    String operator = matcher.group(2);
    String expectedValue = matcher.group(3);

    String actualValue = securityLabel.getLabel(labelKey);
    if (actualValue == null) {
      return false;
    }

    switch (operator) {
      case TOKEN_EQ:
        return expectedValue.equals(actualValue);
      case TOKEN_NEQ:
        return !expectedValue.equals(actualValue);
      default:
        throw new LBACException("字符串标签不支持运算符: " + operator);
    }
  }

  /**
   * 评估数字标签表达式
   *
   * @param matcher 正则表达式匹配器
   * @param securityLabel 安全标签
   * @return 评估结果
   * @throws LBACException 如果表达式无效
   */
  private boolean evaluateNumericLabel(Matcher matcher, SecurityLabel securityLabel)
      throws LBACException {
    String labelKey = matcher.group(1);
    String operator = matcher.group(2);
    String expectedValueStr = matcher.group(3);

    String actualValueStr = securityLabel.getLabel(labelKey);
    if (actualValueStr == null) {
      return false;
    }

    // 尝试将值转换为数字
    double expectedValue, actualValue;
    try {
      expectedValue = Double.parseDouble(expectedValueStr);
      actualValue = Double.parseDouble(actualValueStr);
    } catch (NumberFormatException e) {
      throw new LBACException("标签 '" + labelKey + "' 的值不是有效的数字");
    }

    switch (operator) {
      case TOKEN_EQ:
        return actualValue == expectedValue;
      case TOKEN_NEQ:
        return actualValue != expectedValue;
      case TOKEN_GT:
        return actualValue > expectedValue;
      case TOKEN_GTE:
        return actualValue >= expectedValue;
      case TOKEN_LT:
        return actualValue < expectedValue;
      case TOKEN_LTE:
        return actualValue <= expectedValue;
      default:
        throw new LBACException("不支持的运算符: " + operator);
    }
  }
}
