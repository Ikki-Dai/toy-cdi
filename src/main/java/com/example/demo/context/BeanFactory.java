/* Top Secret */
package com.example.demo.context;

import com.example.demo.annotation.Component;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class BeanFactory {

    private Set<Class> wait;

    public BeanFactory() {
        this.wait = new HashSet<>();
    }

    public <T> T createBeanWithNoArgs(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("init Bean {} with error", clazz.getName(), e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T createBeanWithArgs(Class<T> clazz, Constructor constructor, Context context) {
        Class[] classes = constructor.getParameterTypes();
        Object[] consParam = new Object[classes.length];
        // check params
        for (int i = 0; i < classes.length; i++) {
            Class clz = classes[i];
            Object o = context.getBean(clz);
            if (null == o) {
                if (check(clz)) {
                    throw new IllegalStateException(String.format("find cycle dependency: %n\t %s --> %s", clazz.getName(), clz.getName()));
                } else {
                    wait.add(clazz);
                    Component component = (Component) clz.getAnnotation(Component.class);
                    if (null == component) {
                        throw new IllegalStateException(String.format("Class: %s is not a valid bean with out annotation : @Component", clz.getName()));
                    } else {
                        Object oBean = createBean(clz, context);
                        context.addBean(clz.getSimpleName().toLowerCase(), oBean);
                        consParam[i] = oBean;
                    }

                }
            } else {
                consParam[i] = o;
            }
        }

        try {
            Object o = constructor.newInstance(consParam);
            wait.remove(clazz);
            return (T) o;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("init Bean {} with error", clazz.getName(), e);
        }
        return null;
    }

    private boolean check(Class clazz) {
        return wait.contains(clazz);
    }


    /**
     * 构造函数注入
     *
     * @param clazz
     * @param context
     * @param <T>
     * @return
     */
    public <T> T createBean(Class<T> clazz, Context context) {
        Constructor[] constructors = clazz.getConstructors();
        for (Constructor cons : constructors) {
            if (cons.getParameterCount() == 0) {
                return this.createBeanWithNoArgs(clazz);
            } else {
                return createBeanWithArgs(clazz, cons, context);
            }
        }
        return null;
    }

}
