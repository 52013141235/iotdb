package org.apache.iotdb.commons.exception;

/** 标签访问控制（LBAC）相关的异常类。 当标签访问控制过程中出现错误时抛出此异常。 */
public class LBACException extends Exception {
  private static final long serialVersionUID = 1L;

  /**
   * 使用指定的错误消息创建异常
   *
   * @param message 错误消息
   */
  public LBACException(String message) {
    super(message);
  }

  /**
   * 使用指定的错误消息和原因创建异常
   *
   * @param message 错误消息
   * @param cause 原因
   */
  public LBACException(String message, Throwable cause) {
    super(message, cause);
  }
}
