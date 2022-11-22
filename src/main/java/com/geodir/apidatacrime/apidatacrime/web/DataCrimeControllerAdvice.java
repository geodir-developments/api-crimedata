package com.geodir.apidatacrime.apidatacrime.web;

import com.geodir.apidatacrime.apidatacrime.domain.security.KeyFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public class DataCrimeControllerAdvice {
    @ExceptionHandler(KeyFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String bookAlreadyExistsHandler(KeyFailedException ex) {
        return ex.getMessage();
    }

}
