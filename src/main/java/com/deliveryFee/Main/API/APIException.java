package com.deliveryFee.Main.API;

import org.hibernate.engine.internal.ImmutableEntityEntry;

import java.security.MessageDigest;
import java.util.Optional;

public class APIException extends Exception{
    private final int value;
    private final String message;

    public APIException(int value, String message){
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
