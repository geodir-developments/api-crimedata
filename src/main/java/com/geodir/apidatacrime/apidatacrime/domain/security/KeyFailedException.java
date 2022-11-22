package com.geodir.apidatacrime.apidatacrime.domain.security;
public class KeyFailedException  extends RuntimeException{

    public KeyFailedException(String key){
        super(key + " failed for authentication");
    }

}