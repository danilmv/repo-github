package J3.lesson7.reflection.core;

import J3.lesson7.reflection.annotation.Autowired;
import J3.lesson7.reflection.annotation.Service;
import J3.lesson7.reflection.annotation.SetUp;
import J3.lesson7.reflection.core.exception.CannotCreateClassObjectException;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Context {
    public static <T> T getInstanceObject(Class<T> template) {

        if (template.isAnnotationPresent(SetUp.class)) {
            SetUp setUpAnnotation = template.getAnnotation(SetUp.class);
            String packageForScan = setUpAnnotation.scanPackage();
            Set<Class<?>> services = scanPackageAndFindClassesByAnnotation(packageForScan, Service.class);
            Map<String, Object> instancesMap = getInstancesOfServices(services);

            return configAndGetObjectOfClass(template, instancesMap);
        } else
            throw new IllegalArgumentException("It's not SetUp class");

    }

    private static <T> T configAndGetObjectOfClass(Class<T> template, Map<String, Object> instancesMap) {
        T instance = getClassInstanceByEmptyConstructor(template);
        Field[] fields = template.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)){
                Object dependency = instancesMap.get(field.getType().getName());
                setFieldValue(field, instance, dependency);
            }
        }

        return instance;
    }

    private static void setFieldValue(Field field, Object obj, Object value){
        try{
            field.setAccessible(true); //по-хорошему нужен setter
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> getInstancesOfServices(Set<Class<?>> services) {
        Map<String, Object> serviceMap = new HashMap<>();
        for (Class<?> serviceClass : services) {
            Class<?>[] interfaces = serviceClass.getInterfaces();
            Object serviceInstance = getClassInstanceByEmptyConstructor(serviceClass);

            for (Class<?> anInterface : interfaces) {
                serviceMap.put(anInterface.getName(), serviceInstance);
            }
        }
        return serviceMap;
    }

    private static <T> T getClassInstanceByEmptyConstructor(Class<T> aClass){
        try {
            return aClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new CannotCreateClassObjectException("Cannot create object of " + aClass.getName() + " class");
        }
    }

    private static Set<Class<?>> scanPackageAndFindClassesByAnnotation(String scanPackage,
                                                                       Class<? extends Annotation> annotationClass) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(scanPackage)));

        return reflections.getTypesAnnotatedWith(annotationClass);
    }


}
