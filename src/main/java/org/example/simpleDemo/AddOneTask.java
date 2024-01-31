package org.example.simpleDemo;

import org.taskflow.core.INode;
import org.taskflow.core.ITask;

import java.util.List;

public class AddOneTask implements ITask<Integer> {
    @Override
    public void setNode(INode<Integer> node) {}

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

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ignored) {
        }

        return ++result;
    }
}