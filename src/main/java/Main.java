
import etu2074.framework.annotations.RequestParameter;
import testFramework.Client;
import testFramework.Departement;
import testFramework.connection.Connnection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Vector;

public class Main {

    public static void main(String[] args) throws Exception {
        Vector<String> test = new Vector<>();

        test.add("Mirindra");test.add("bota");
        String[]d = test.toArray(test.toArray(new String[0]));
        Arrays.stream(d).forEach(element-> System.out.println(element));
//        Class<?> test = String.class; // Class object representing the String class
//        Object obj = "Ito ny akizivavay ny tompo"; // Object reference pointing to a String object
//
//        // Cast obj to String using the cast() method on the Class object
//        Object str = test.cast(obj);
//        Method method = client.getClass().getMethod("gana",  test);
//        method.invoke(client,str);
//        System.out.println(String.valueOf(String.class));
//        Arrays.stream(methods).forEach(method -> System.out.println(method.getName()));

    }

    public static void testMethod(@RequestParameter(name="d") Double d, String a, boolean c) {
        // Method implementation
        System.out.println("gana");
    }
}
