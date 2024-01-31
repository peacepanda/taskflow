package org.taskflow.pool;

import java.util.concurrent.*;

/**
 * 自定义线程池
 */
public class CustomThreadPool {

    /**
     * 可缓存线程池
     */
    public static ExecutorService newCachedThreadPool() {
        return Executors.newCachedThreadPool();
    }

}