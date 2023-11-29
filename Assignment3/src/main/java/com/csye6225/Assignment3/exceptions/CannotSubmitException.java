package com.csye6225.Assignment3.exceptions;

public class CannotSubmitException extends RuntimeException{
    public CannotSubmitException(String message) {
        super(message);
    }
}
