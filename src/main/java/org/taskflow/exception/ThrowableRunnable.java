package org.taskflow.exception;

@FunctionalInterface
public interface ThrowableRunnable {
    void run() throws Exception;
}
