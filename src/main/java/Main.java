import etu2074.framework.annotations.RequestParameter;
import java.util.Arrays;
import java.util.Vector;

public class Main {

    public static void main(String[] args) throws Exception {
        Vector<String> test = new Vector<>();
        test.add("Mirindra");test.add("bota");
        String[]d = test.toArray(test.toArray(new String[0]));
        Arrays.stream(d).forEach(element-> System.out.println(element));

    }

    public static void testMethod(@RequestParameter(name="d") Double d, String a, boolean c) {
        System.out.println("gana");
    }
}
