package org.example.protoDemo;

import com.example.myapp.PersonOuterClass.Person;
import org.taskflow.core.TaskFlow;
import org.taskflow.core.TaskNode;

import java.util.concurrent.*;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        // 创建线程池
        ExecutorService executorService = Executors.newCachedThreadPool();

        int n = 1;

        // 模拟并发处理多个请求
        CompletableFuture<Void> requestFutures = CompletableFuture.allOf(
                IntStream.range(0, n)
                .mapToObj(i -> CompletableFuture.supplyAsync(()-> handelRequest(executorService, ""), executorService))
                .toArray(CompletableFuture[]::new)
        );

        requestFutures.join();

        // 关闭线程池
        executorService.shutdown();
    }

    public static String handelRequest(Executor executor, String request) {

        TaskFlow<Person> flow = new TaskFlow<>(executor);

        // 创建任务节点
        TaskNode<Person> node1 = flow.createNode("n1", new DemoTask("task1", 5, (person)->person.setName("tom")));
        TaskNode<Person> node2 = flow.createNode("n2", new DemoTask("task2", 2, (person)->person.setId(123)));
        TaskNode<Person> node3 = flow.createNode("n3", new DemoTask("task3", 2, (person)->person.setEmail("tom@qq.com")));
        TaskNode<Person> node4 = flow.createNode("n4", new DemoTask("task4", 2, (person)->person.setAge(30)));
        TaskNode<Person> node5 = flow.createNode("n5", new DemoTask("task5", 1, (person)->person.setGender(1)));
        TaskNode<Person> node6 = flow.createNode("n6", new DynamicTask("task6", 1));

        node3.addDependency(node2);
        node4.addDependency(node3);
        node4.addDependency(node5);
        node5.addDependency(node1);
        node5.addDependency(node3);
        node5.addDependency(node2);
        node6.addDependency(node1);
        node6.addWeakDependency(node4);

        // 打印DOT格式
        String dotGraph = flow.dump();
        System.out.println(dotGraph);

        Person.Builder resultBuilder = Person.newBuilder();

        try {
            flow.execute(resultBuilder.build(), () -> {
                long startTime = System.currentTimeMillis();

                Person result = node6.get();
                System.out.println(result);
                resultBuilder.mergeFrom(result);

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                System.out.println("-----finish. 耗时:" + duration + "ms------");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultBuilder.toString();
    }
}