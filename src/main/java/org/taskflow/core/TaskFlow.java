package org.taskflow.core;


import org.taskflow.exception.TaskFlowException;
import org.taskflow.exception.ThrowableRunnable;
import org.taskflow.pool.CustomThreadPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class TaskFlow<T> {

    private final Executor executor;
    private final Map<String, TaskNode<T>> allNodes;
    private T initResult;

    public TaskFlow(Executor executor) {
        this.allNodes = new HashMap<>();
        this.executor = executor;
    }

    public TaskNode<T> createNode(String nodeName, ITask<T> task) {
        if (allNodes.containsKey(nodeName)) {
            throw new TaskFlowException("node name is repeated");
        }
        TaskNode<T> node = new TaskNode<>(nodeName, task, this);
        allNodes.put(nodeName, node);
        return node;
    }

    protected T getInitResult() {
        return initResult;
    }

    protected TaskNode<T> getNode(String nodeName) {
        return allNodes.get(nodeName);
    }

    protected Executor getExecutor() {
        return executor;
    }

    public void execute(T initResult, ThrowableRunnable runnable) {
        this.initResult = initResult;

        // 收集所有节点的future
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (TaskNode<T> node : allNodes.values()) {
            CompletableFuture<T> taskFuture = node.createFuture();
            futures.add(taskFuture);
        }

        CompletableFuture<String> mainLogicFutrue = CompletableFuture.supplyAsync(()->{
            try {
                runnable.run();

                // 中断其他未完成节点
                for (TaskNode<T> node : allNodes.values()) {
                    node.cancel();
                }
            } catch (Exception e) {
                throw new TaskFlowException(e);
            }
            return null;
        }, this.executor);
        futures.add(mainLogicFutrue);

        try {
            CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allDoneFuture.join();
        } catch (Exception e) {
            if (e.getCause().getClass() != CancellationException.class) {
                throw e;
            }
        }
    }

    public String dump() {
        StringBuilder dotGraph = new StringBuilder("digraph taskflow {\n");
        for (TaskNode<T> node : allNodes.values()) {
            String nodeId = Integer.toHexString(System.identityHashCode(node));
            dotGraph.append("    \"").append(nodeId).append("\" [label=\"").append(node.getDescription()).append("\"];\n");
            for (TaskNode<T> dep : node.getDependencies()) {
                String depId = Integer.toHexString(System.identityHashCode(dep));
                dotGraph.append("    \"").append(depId).append("\" -> \"").append(nodeId).append("\";\n");
            }
            for (TaskNode<T> dep : node.getWeakDependencies()) {
                String depId = Integer.toHexString(System.identityHashCode(dep));
                dotGraph.append("    \"").append(depId).append("\" -> \"").append(nodeId).append("\"[style=\"dashed\"];\n");
            }
        }
        dotGraph.append("}\n");
        return dotGraph.toString();
    }

}
