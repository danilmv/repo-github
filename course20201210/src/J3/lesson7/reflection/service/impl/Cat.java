package J3.lesson7.reflection.service.impl;

import J3.lesson7.reflection.annotation.Service;
import J3.lesson7.reflection.service.Animal;

//@Service
public class Cat implements Animal {
    @Override
    public void doSomething() {
        System.out.println("Meow");
    }
}
