package etu2074.framework.error;

import jakarta.servlet.ServletException;

public class MissMatchMethodException extends ServletException {
    public MissMatchMethodException(String message) {
        super(message);
    }
}
