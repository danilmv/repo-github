package J3.lesson7.hw;

import J3.lesson7.hw.annotation.AfterSuite;
import J3.lesson7.hw.annotation.BeforeSuite;
import J3.lesson7.hw.annotation.PRIORITIES;
import J3.lesson7.hw.annotation.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTestExecutor {

    public static <T> void start(T obj) {
        Method[] methods = obj.getClass().getMethods();
        Method before = null;
        Method after = null;
        Map<PRIORITIES, List<Method>> tests = new HashMap<>();

        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                if (before != null)
                    throw new RuntimeException("two examples of @BeforeSuite method");
                before = method;
            } else if (method.isAnnotationPresent(AfterSuite.class)) {
                if (after != null)
                    throw new RuntimeException("two examples of @AfterSuite method");
                after = method;
            } else if (method.isAnnotationPresent(Test.class)) {
                PRIORITIES priority = method.getAnnotation(Test.class).priority();
                tests.putIfAbsent(priority, new ArrayList<>());
                List<Method> list = tests.get(priority);
                list.add(method);
            }
        }

        try {
            if (before != null)
                before.invoke(obj);

            for (PRIORITIES value : PRIORITIES.values()) {
                List<Method> methods2 = tests.get(value);
                if (methods2 != null)
                    for (Method method : methods2) {
                        method.invoke(obj);
                    }
            }

            if (after != null)
                after.invoke(obj);

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
