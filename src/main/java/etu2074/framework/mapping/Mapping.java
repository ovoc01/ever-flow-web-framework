package etu2074.framework.mapping;

import java.lang.reflect.Method;

import etu2074.framework.annotations.apienum.RequestType;

public class Mapping {
    String class_name;
    Method method;
    private Class<?> aClass;
    private RequestType type;

    public Class<?> getaClass() {
        return aClass;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public RequestType getType() {
        return type;
    }

    public void setaClass(Class<?> aClass) {
        this.aClass = aClass;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
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
    public Mapping(String class_name,Method method, Class<?> c){
        setMethod(method);
        setaClass(c);
        setClass_name(class_name);
    }
    public Mapping(){

    }
}
