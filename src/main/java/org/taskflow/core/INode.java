package org.taskflow.core;

import java.util.concurrent.ExecutionException;

public interface INode<T> {
    /**
     * 获取依赖节点的结果，必须是声明过强依赖或者弱依赖的节点才能获取。
     * @param nodeName 节点注册在Flow中的名字
     * @return 节点执行后的结果
     */
    T getDependencyResult(String nodeName);
}
