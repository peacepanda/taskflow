package org.example.simpleDemo;

import org.taskflow.core.INode;
import org.taskflow.core.ITask;

import java.util.List;

public class CustomTask implements ITask<Integer> {

    private INode<Integer> node;
    @Override
    public void setNode(INode<Integer> node) {
        this.node = node;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Integer preprocess(List<Integer> results) {
        return results.stream().reduce(Integer::sum).orElse(0);
    }

    @Override
    public Integer execute(Integer input) {
        int result = 0;
        if (input != null) result += input;

        // 核心逻辑： 上游输入如果是偶数，则动态依赖n4节点的结果，如果是奇数，则直接运算。
        if (result % 2 == 0) {
            int n4Result = this.node.getDependencyResult("n4");
            result += n4Result;
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ignored) {
        }

        return ++result;
    }
}