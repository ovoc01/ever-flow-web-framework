package testFramework;

import etu2074.framework.controller.Model_view;
import etu2074.framework.url.Link;

public class Employe {
    @Link(url = "try")
    public Model_view controller(){
        double i = 0;
        Model_view model_view = new Model_view("Hello.jsp");
        model_view.addItem("number",i);
        return model_view;
    }

    @Link(url="try/select")
    public Model_view select (){
        return null;
    }
    public static <T> T dynamicCast(Object obj, Class<T> cls) {
        return cls.cast(obj);
    }
}
