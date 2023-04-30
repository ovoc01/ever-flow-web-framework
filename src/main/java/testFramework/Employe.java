package testFramework;

import etu2074.framework.controller.ModelView;
import etu2074.framework.annotations.Link;

public class Employe {
    @Link(url = "try")
    public ModelView controller(){
        double i = 0;
        ModelView model_view = new ModelView("Hello.jsp");
        model_view.addItem("number",i);
        return model_view;
    }

    @Link(url="try/select")
    public ModelView select (){
        return null;
    }
}
