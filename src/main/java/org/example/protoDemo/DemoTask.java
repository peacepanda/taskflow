package org.example.protoDemo;

import com.example.myapp.PersonOuterClass.Person;

import java.util.function.Function;

public class DemoTask extends Task {

    private final String name;
    private final Integer sleepSecond;
    private final Function<Person.Builder, Person.Builder> func;


    public DemoTask(String name, Integer sleepSecond, Function<Person.Builder, Person.Builder> func) {
        this.name = name;
        this.sleepSecond = sleepSecond;
        this.func = func;
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("完成执行：" + this.name + "。时长：" + sleepSecond + "秒");
        return this.func.apply(input.toBuilder()).build();
    }
}
