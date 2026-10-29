package com.umesh.urlshortener.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse>handleApiException(ApiException ex, HttpServletRequest request){
        return buildResponse(ex.getStatus(),ex.getMessage(),request.getRequestURI(), Map.of());
    }
     private  ResponseEntity<ApiErrorResponse>buildResponse(HttpStatus status,String message,String path,Map<String,String> validationErrors){
        ApiErrorResponse responseBody=createError(status,message,path, validationErrors);
        return ResponseEntity.status(status).body(responseBody);

    }
   private ApiErrorResponse createError(HttpStatus status,String message,String path,Map<String,String>validationErrors){
        String errorName=status.getReasonPhrase();
        return new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                errorName,
                message,
                path,
                validationErrors
        );
    }

}
