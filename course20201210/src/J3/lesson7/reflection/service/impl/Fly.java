package J3.lesson7.reflection.service.impl;

import J3.lesson7.reflection.annotation.Service;
import J3.lesson7.reflection.service.Flyable;

//@Service
public class Fly implements Flyable {
    @Override
    public void fly() {
        System.out.println("I'm flying");
    }
}
