package org.taskflow.exception;

/**
 * 自定义运行时异常
 */
public class TaskFlowException extends RuntimeException {

    public TaskFlowException(String message) {
        super(message);
    }

    public TaskFlowException(Exception e) {
        super(e);
    }
}