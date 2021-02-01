/* Top Secret */
package com.example.demo.context;

import com.example.demo.annotation.Component;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Context {

    Map<String, Object> map;

    BeanFactory beanFactory;

    private Set<Class> classes;

    public Context() {
        map = new ConcurrentHashMap<>();
        beanFactory = new BeanFactory();
        classes = new HashSet<>();
//        map.put(this.getClass().getSimpleName())
    }

    public Context(String... pkgs) {
        this();
        scanAndLoad(pkgs);
    }

    /**
     * scan all classes
     *
     * @param pkgs
     */
    public void scanAndLoad(String... pkgs) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urls = null;
        Set<String> filePath = new HashSet<>();
        try {
            for (String pkg : pkgs) {
                String path = pkg.replace(".", "/");
                urls = classLoader.getResources(path);
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    filePath.add(url.getFile());
                }

                File file;
                for (String clazzFile : filePath) {
                    file = new File(clazzFile);
                    classes.addAll(find(file, pkg));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("load class error: {}", e.getMessage());
        }
    }

    private Set<Class> find(File file, String pkg) throws ClassNotFoundException {
        Set<Class> tmpClasses = new HashSet<>();
        if (!file.exists()) {
            return tmpClasses;
        } else {
            File[] classFile = file.listFiles();
            for (File f : classFile) {
                String nextPkg = pkg + "." + f.getName();
                if (f.isDirectory()) {
                    if (!f.getName().contains(".")) {
                        tmpClasses.addAll(find(f, nextPkg));
                    }
                } else if (f.getName().endsWith(".class")) {
                    Class clazz = Class.forName(nextPkg.substring(0, nextPkg.length() - ".class".length()));
                    tmpClasses.add(clazz);
                    log.debug("load class: {}", clazz.getName());
                }
            }

        }
        return tmpClasses;
    }

    public void refresh() {
        Iterator<Class> classIterator = classes.iterator();
        while (classIterator.hasNext()) {
            Class clazz = classIterator.next();
            registerBean(clazz);
        }
    }


    @SuppressWarnings("unchecked")
    public <T> T getBean(Class clazz) {
        String name = clazz.getSimpleName().toLowerCase();
        return (T) map.get(name);
    }

    public void registerBean(Class clazz) {
        if (clazz.isAnnotation()) {
            return;
        }
        Component component = (Component) clazz.getAnnotation(Component.class);

        String name = clazz.getSimpleName().toLowerCase();
        if (null != component) {
            if (!"".equals(component.value())) {
                name = component.value();
            }
            Object obj = beanFactory.createBean(clazz, this);
            this.addBean(name, obj);
        }
    }

    public void addBean(String name, Object obj) {
        map.put(name, obj);
    }


}
