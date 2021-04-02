package J3.lesson7.reflection.service.impl;

import J3.lesson7.reflection.annotation.Service;
import J3.lesson7.reflection.service.Animal;
import J3.lesson7.reflection.service.Flyable;

@Service
public class Penguin implements Animal, Flyable {
    @Override
    public void doSomething() {
        System.out.println("I'm penguin");
    }

    @Override
    public void fly() {
        System.out.println("I can't fly");
    }
}
