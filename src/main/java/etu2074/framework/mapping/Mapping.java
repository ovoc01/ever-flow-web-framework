package etu2074.framework.mapping;

import etu2074.framework.utilities.Utilities;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

public class Mapping {
    String class_name;
    Method method;
    private Class<?> aClass;
    HashMap<Method,HashMap<String,Class<?>>> method_parameter = new HashMap<>();

    public Class<?> getaClass() {
        return aClass;
    }

    public void setaClass(Class<?> aClass) {
        this.aClass = aClass;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
        setUp_method_parameter(this.method);
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }
    public Mapping(String class_name,Method method){
        setClass_name(class_name);
        setMethod(method);
    }

    public HashMap<Method, HashMap<String, Class<?>>> getMethod_parameter() {
        return method_parameter;
    }

    public void setMethod_parameter(HashMap<Method, HashMap<String, Class<?>>> method_parameter) {
        this.method_parameter = method_parameter;
    }

    public Mapping(String class_name, Method method, Class<?> c){
        setMethod(method);
        setaClass(c);
        setClass_name(class_name);
    }
    public Mapping(){

    }


    private void setUp_method_parameter(Method method)
    {
        //System.out.println(method);
        Utilities.setUp_matchingType(); //initialize
        Parameter [] m_parameter = method.getParameters();
        Class<?>[] classList = new Class[m_parameter.length];
        HashMap<String,Class<?>> me_parameter=new HashMap<>();
        for (Parameter params: m_parameter) {
            try {
               // System.out.println(params.getParameterizedType().getTypeName());
                System.out.println(Utilities.matchingType.get(params.getParameterizedType().getTypeName()));
                Class<?> temp = Class.forName(Utilities.matchingType.get(params.getParameterizedType().getTypeName()));
                me_parameter.put(params.getName(),temp);
                method_parameter.put(method,me_parameter);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
