package com.skilllinker.marketplace.ExceptionHandler;

public class NotFoundException extends RuntimeException{

    public NotFoundException(String message){
        super(message);
    }
}
