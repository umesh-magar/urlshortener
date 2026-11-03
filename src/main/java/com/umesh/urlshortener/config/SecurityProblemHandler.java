package com.umesh.urlshortener.config;

import com.umesh.urlshortener.exceptions.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

@Component
public class SecurityProblemHandler implements AuthenticationEntryPoint , AccessDeniedHandler {
    private  final ObjectMapper objectMapper;
   public SecurityProblemHandler(ObjectMapper objectMapper){
       this.objectMapper=objectMapper;
   }
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        write(
                request,
                response,
                HttpStatus.UNAUTHORIZED,
                "Authentication is required or token is invalid or expired"
        );
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
      write(
              request,
              response,
              HttpStatus.FORBIDDEN,
              "You do not have permission to perform this action"
      );
    }
    private void write(HttpServletRequest request, HttpServletResponse response, HttpStatus status,String message) throws IOException{
        if(response.isCommitted()){
            return;
        }
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ApiErrorResponse errorResponse=new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                null
        );
        objectMapper.writeValue(response.getOutputStream(),
                errorResponse);
    }

}
