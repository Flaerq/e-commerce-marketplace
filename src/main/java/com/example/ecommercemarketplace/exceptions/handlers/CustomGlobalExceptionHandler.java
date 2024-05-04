package com.example.ecommercemarketplace.exceptions.handlers;


import com.example.ecommercemarketplace.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomGlobalExceptionHandler {

    @ExceptionHandler(value = {
            UserNotFoundException.class,
            EmailConfirmationTokenNotFoundException.class,
            UsernameNotFoundException.class,
            MerchantNotFoundException.class,
            CartItemNotFoundException.class,
            CartItemNotFoundInCartException.class,
            ShoppingCartNotFoundException.class,
            PasswordResetTokenNotFoundException.class,
            OrderNotFoundException.class,
            OrderItemNotFoundException.class
    })
    public ResponseEntity<ErrorObject> handleEntityNotFoundException(RuntimeException ex) {
        ErrorObject errorObject = generateErrorObject(HttpStatus.NOT_FOUND, ex);
        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {
            OrderNotFoundInUserException.class,
            EmptyShoppingCartException.class
    })
    public ResponseEntity<ErrorObject> handleBadRequestException(RuntimeException exception){
        ErrorObject errorObject = generateErrorObject(HttpStatus.BAD_REQUEST, exception);
        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorObject> handleSideExceptions(Exception exception) {
        ErrorObject errorObject = generateErrorObject(HttpStatus.INTERNAL_SERVER_ERROR, exception);
        return new ResponseEntity<>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorObject> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getAllErrors().forEach(error -> {
            if (error instanceof FieldError) {
                String fieldName = ((FieldError) error).getField();
                String errorName = error.getDefaultMessage();
                errors.put(fieldName, errorName);
            } else {
                errors.put("Fields", error.getDefaultMessage());
            }
        });

        ErrorObject errorObject = ErrorObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errors)
                .build();
        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoginAttemptExceedingException.class)
    public ResponseEntity<ErrorObject> handleLoginAttemptExceedingException(LoginAttemptExceedingException exception) {
        ErrorObject errorObject = generateErrorObject(HttpStatus.LOCKED, exception);
        return new ResponseEntity<>(errorObject, HttpStatus.LOCKED);
    }

    @ExceptionHandler(MissingAuthorizationHeaderException.class)
    public ResponseEntity<ErrorObject> handleMissingAuthorizationHeaderException(MissingAuthorizationHeaderException exception) {
        ErrorObject errorObject = generateErrorObject(HttpStatus.UNAUTHORIZED, exception);
        return new ResponseEntity<>(errorObject, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {
            AuthenticationException.class,
            AccessDeniedException.class,
    })
    public ResponseEntity<ErrorObject> handleAuthenticationException(Exception exception) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        if (exception.getCause() instanceof LoginAttemptExceedingException) {
            status = HttpStatus.LOCKED;
        }

        ErrorObject errorObject = generateErrorObject(status, exception);
        return new ResponseEntity<>(errorObject, status);
    }

    public static ErrorObject generateErrorObject(HttpStatus code, Exception exception) {
        return ErrorObject.builder()
                .timestamp(LocalDateTime.now())
                .status(code.value())
                .message(exception.getMessage())
                .build();
    }
}

