import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class Main {

    public static void main(String[] args) {
        try {
            Class<?> controllerClass = Main.class;

            // Get the Method object for the method to retrieve argument names and types
            Class<?>[] params = new Class[3];
            params[0] = Double.class;
            params[1] = int.class;
            params[2] = boolean.class;
            Method method = controllerClass.getMethod("testMethod", params);
            // Get the array of Parameter objects representing the parameters of the method
            Parameter[] parameters = method.getParameters();

            // Retrieve the argument names and types from the Parameter objects
            Class<?>[] classList = new Class[parameters.length];
            int i = 0;
            for (Parameter p : parameters) {
                String className = p.getParameterizedType().getTypeName();
                System.out.println();
                if (className.equals("int")) {
                    className = "java.lang.Integer"; // Use Integer wrapper class for int
                }
                classList[i] = Class.forName(className);
                System.out.println("Argument name: " + p.getName());
                System.out.println("Argument type: " + classList[i]);
                i++;
            }

            // Dynamically instantiate objects of argument types
            Object[] arguments = new Object[parameters.length];
            for (i = 0; i < parameters.length; i++) {
                arguments[i] = classList[i].getDeclaredConstructor().newInstance();
                System.out.println("Argument instance: " + arguments[i]);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public void testMethod(Double arg1, int arg2, boolean arg3) {
        // Method implementation
    }
}
