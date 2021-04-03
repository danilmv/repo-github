package J3.lesson7.hw;

import J3.lesson7.hw.annotation.AfterSuite;
import J3.lesson7.hw.annotation.BeforeSuite;
import J3.lesson7.hw.annotation.Priorities;
import J3.lesson7.hw.annotation.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTestExecutor {

    public static <T> void start(T obj) {
        Method before = null;
        Method after = null;
        Map<Priorities, List<Method>> tests = new HashMap<>();

        for (Method method : obj.getClass().getMethods()) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                if (before != null)
                    throw new RuntimeException("two examples of @BeforeSuite method");
                before = method;
            }
            if (method.isAnnotationPresent(AfterSuite.class)) {
                if (after != null)
                    throw new RuntimeException("two examples of @AfterSuite method");
                after = method;
            }
            if (method.isAnnotationPresent(Test.class)) {
                Priorities priority = method.getAnnotation(Test.class).priority();
                tests.putIfAbsent(priority, new ArrayList<>());
                tests.get(priority).add(method);
            }
        }

        try {
            if (before != null)
                before.invoke(obj);

            for (Priorities value : Priorities.values()) {
                List<Method> methods = tests.get(value);
                if (methods != null)
                    for (Method method : methods)
                        method.invoke(obj);
            }

            if (after != null)
                after.invoke(obj);

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
