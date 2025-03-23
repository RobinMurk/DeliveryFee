package com.deliveryFee.Main.API;

import org.hibernate.engine.internal.ImmutableEntityEntry;

import java.security.MessageDigest;
import java.util.Optional;

public class APIException extends Exception{
    private final int value;

    public APIException(int value, String message){
        super(message);
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
