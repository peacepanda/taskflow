package org.taskflow.core;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ITask<T> {

    /**
     * 用于打印DAG可视化时描述节点
     * @return 任务描述信息
     */
    String getDescription();

    /**
     * 强依赖节点都完成时，用此方法预处理上游节点的结果，其处理后的输出会作为execute函数的输入
     * @param results 上游强依赖节点的执行结果
     * @return 预处理后结果
     */
    T preprocess(List<T> results);

    /**
     * 任务主逻辑
     * @param input 任务输入
     * @return 任务输出
     */
    T execute(T input);

    /**
     * 注入绑定的node
     * @param node 执行单元节点
     */
    void setNode(INode<T> node);

}