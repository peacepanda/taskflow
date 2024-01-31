package org.example.simpleDemo;

import org.taskflow.core.TaskFlow;
import org.taskflow.core.TaskNode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        TaskFlow<Integer> flow = new TaskFlow<>(executorService);
        // 创建任务节点
        TaskNode<Integer> node1 = flow.createNode("n1", new AddOneTask());
        TaskNode<Integer> node2 = flow.createNode("n2", new AddOneTask());
        TaskNode<Integer> node3 = flow.createNode("n3", new AddOneTask());
        TaskNode<Integer> node4 = flow.createNode("n4", new AddOneTask());
        // 描述依赖关系
        node2.addDependency(node1);
        node4.addDependency(node2);
        node4.addDependency(node3);

        TaskNode<Integer> node5 = flow.createNode("n5", new AddOneTask());
        TaskNode<Integer> node6 = flow.createNode("n6", new CustomTask());

        node6.addWeakDependency(node4);
        node6.addDependency(node5);

        TaskNode<Integer> node7 = flow.createNode("n7", new AddOneTask());
        node5.addDependency(node7);

        flow.execute(0, ()->{
            System.out.println("answer:" + node6.get());
        });

        // 关闭线程池
        executorService.shutdown();

        // 打印DOT格式
        System.out.println(flow.dump());
    }
}