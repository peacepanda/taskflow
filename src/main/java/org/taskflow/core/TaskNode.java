package org.taskflow.core;

import org.taskflow.exception.TaskFlowException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TaskNode<T> implements INode<T>{
    private final String name;
    private final ITask<T> task;
    private final List<TaskNode<T>> dependencies;
    private final List<TaskNode<T>> weakDependencies;

    private final TaskFlow<T> flow;
    private CompletableFuture<T> futureResult;

    protected TaskNode(String name, ITask<T> task, TaskFlow<T> flow) {
        this.name = name;
        this.task = task;
        this.dependencies = new ArrayList<>();
        this.weakDependencies = new ArrayList<>();
        this.flow = flow;
        this.futureResult = null;

        // 注入Flow
        this.task.setNode(this);
    }

    protected CompletableFuture<T> createFuture() {
        synchronized(this) {
            if (futureResult != null) {
                return futureResult;
            }

            futureResult = new CompletableFuture<>();

            if (this.dependencies.isEmpty()) {
                futureResult.completeAsync(() -> task.execute(this.flow.getInitResult()), this.flow.getExecutor());
            } else {
                List<CompletableFuture<T>> depFutures = dependencies.stream()
                        .map(TaskNode::createFuture)
                        .collect(Collectors.toList());

                CompletableFuture<Void> allDoneFuture =
                        CompletableFuture.allOf(depFutures.toArray(new CompletableFuture[0]));

                futureResult = allDoneFuture.thenComposeAsync(v ->
                        CompletableFuture.supplyAsync(() -> {
                            List<T> depResults = depFutures.stream()
                                    .map(CompletableFuture::join)
                                    .collect(Collectors.toList());
                            return task.execute(task.preprocess(depResults));
                        }, this.flow.getExecutor()), this.flow.getExecutor());
            }

            return futureResult;
        }
    }

    public T get() {
        if (futureResult == null) {
            futureResult = createFuture();
        }
        try {
            return futureResult.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            throw new TaskFlowException(e);
        }
        return null;
    }

    public T getDependencyResult(String nodeName) {
        TaskNode<T> node = flow.getNode(nodeName);
        if (node == null) {
            throw new TaskFlowException("node is not found");
        }
        if (!dependencies.contains(node) && !weakDependencies.contains(node)) {
            throw new TaskFlowException("node(" + nodeName + ") is not in dependencies");
        }
        return node.get();
    }

    protected void cancel() {
        if (futureResult != null) {
            futureResult.cancel(true);
        }
    }

    public void addDependency(TaskNode<T> dependency) {
        this.dependencies.add(dependency);
    }

    public void addWeakDependency(TaskNode<T> dependency) {
        this.weakDependencies.add(dependency);
    }

    public List<TaskNode<T>> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    public List<TaskNode<T>> getWeakDependencies() {
        return Collections.unmodifiableList(weakDependencies);
    }

    public String getDescription() {
        String additionalDescription = task.getDescription() == null ? "" : String.format("(%s)", task.getDescription());
        return name + additionalDescription;
    }

}
