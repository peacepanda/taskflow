package org.example.protoDemo;

import com.example.myapp.PersonOuterClass.Person;

public class DynamicTask extends Task {

    private final String name;
    private final Integer sleepSecond;

    public DynamicTask(String name, Integer sleepSecond) {
        this.name = name;
        this.sleepSecond = sleepSecond;
    }

    @Override
    public String getDescription() {
        return name + "(" + this.sleepSecond + "s)";
    }

    @Override
    public Person execute(Person input) {

        System.out.println("开始执行：" + this.name);
        try {
            Thread.sleep(sleepSecond * 1000);
        } catch (InterruptedException ignored) {
        }

        Person output = input;
        Person p = this.node.getDependencyResult("n4");
        output =  input.toBuilder().mergeFrom(p).build();

        System.out.println("完成执行：" + this.name);
        return output;
    }
}
