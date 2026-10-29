package com.umesh.urlshortener.exceptions;

import org.springframework.http.HttpStatus;

public class UnAuthorizedException extends ApiException{
    public UnAuthorizedException(String message){
        super(HttpStatus.UNAUTHORIZED,message);
    }
}
