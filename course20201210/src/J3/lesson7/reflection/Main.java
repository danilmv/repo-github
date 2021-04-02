package J3.lesson7.reflection;

// в этом пакете мы определили аннотации, которыми отметили классы и поля, которые должны быть инициализированны,
// в зависимости от выставленных аннотаций (@Service)
// в аннотации SetUp мы провисываем путь, где лежат классы, отмеченные @Service
// класс Context для указанного класса проверяет, что у него есть аннотация SetUp, и далее по казанному пакету
// с помощью библиотеки org.reflections:reflections получаем список классов с указанной аннотацией
// далее создаем инстанции для найденных классов и помещаем их в мапу
// далее создаем инстанцию указанного класса, и для полей, отмечанных аннотацией @Autowired, прописываем значения из мапы

import J3.lesson7.reflection.annotation.Autowired;
import J3.lesson7.reflection.annotation.SetUp;
import J3.lesson7.reflection.core.Context;
import J3.lesson7.reflection.service.Animal;
import J3.lesson7.reflection.service.Flyable;

@SetUp(scanPackage = "J3.lesson7.reflection.service.impl")
public class Main {
    @Autowired
    private Animal animal;
    @Autowired
    private Flyable flyable;

    public static void main(String[] args) {
        Main instance = Context.getInstanceObject(Main.class);
        instance.flyable.fly();
        instance.animal.doSomething();
    }
}
