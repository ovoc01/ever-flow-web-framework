package etu2074.framework.redirection;

import java.util.HashMap;

public class RedirectAttribute {
    private HashMap<String,Object> redirectAttribute= new HashMap<>();
    public RedirectAttribute(){

    }

    public void addRedirectAttribute(String key,Object value){
        redirectAttribute.put(key,value);
    }

    public Object getRedirectAttribute(String key){
        return redirectAttribute.get(key);
    }

    public HashMap<String,Object> getAllRedirectAttribute(){
        return redirectAttribute;
    }

    public void setRedirectAttribute(HashMap<String, Object> redirectAttribute) {
        this.redirectAttribute = redirectAttribute;
    }
}
