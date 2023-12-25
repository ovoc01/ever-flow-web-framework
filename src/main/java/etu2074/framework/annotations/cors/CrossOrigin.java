package etu2074.framework.annotations.cors;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CrossOrigin {
    String[] origins() default {};

    String[] allowedHeaders() default {}; // Specify the allowed headers as an array of strings.

    String[] exposedHeaders() default {}; // Specify the exposed headers as an array of strings.

    String[] methods() default {}; // Specify the allowed HTTP methods as an array of strings.

    boolean allowCredentials() default false; // Specify whether to allow credentials (e.g., cookies).

    long maxAge() default -1;

}
