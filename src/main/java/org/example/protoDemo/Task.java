package org.example.protoDemo;

import com.example.myapp.PersonOuterClass.Person;
import org.taskflow.core.INode;
import org.taskflow.core.ITask;

import java.util.List;
import java.util.Optional;

public abstract class Task implements ITask<Person> {

    protected INode<Person> node;

    @Override
    public void setNode(INode<Person> node) {
        this.node = node;
    }

    @Override
    public Person preprocess(List<Person> results) {
        Optional<Person> r = results.stream().reduce((p1, p2) -> p1.toBuilder().mergeFrom(p2).build());
        return r.orElse(null);
    }

}