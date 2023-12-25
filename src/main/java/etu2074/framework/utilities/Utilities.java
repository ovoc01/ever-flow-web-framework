package etu2074.framework.utilities;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public  class Utilities {
    public static String createSetter(String field) {
        return "set" + capitalizeFirstLetter(field);
    }
    public static String capitalizeFirstLetter(String input) {
        String var10000 = input.substring(0, 1).toUpperCase();
        return var10000 + input.substring(1);

    }


    public static ArrayList<Class<?>> findClasses(String packageName) throws IOException, ClassNotFoundException {

        ArrayList<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace(".", "/");
        System.out.println(path);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        System.out.println(classLoader);
        
        String classPath = classLoader.getResource(path).getPath();

        File[] files = new File(classPath).listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively search subpackages
                    classes.addAll(findClasses(packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    // Load the class and add it to the list
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                        Class<?> clazz = Class.forName(className);
                        classes.add(clazz);

                }
            }
        }
        System.out.println(classes.size());
        return classes;
    }

    public static String GET_CURRENT_LOCATION(){
        return System.getProperty("user.dir");
    }

}
