package etu2074.framework.annotations.apienum;

public enum RequestType {
    
    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE"),DEFAULT("DEFAULT");

    private String method;

    RequestType(String method) {
        this.method = method;
    }

    public String getMethod() {
        return this.method;
    }

    
}
