package com.example.usermanagementsystem.usermanagementsystem.Exception;

import com.example.usermanagementsystem.usermanagementsystem.DTO.ResponseDTO.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalException {

    @ExceptionHandler(NotValidAgeException.class)
    public ResponseEntity<ApiResponse<?>> handleAgeValidation(NotValidAgeException ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(null, ex.getMessage(), false));
    }

    @ExceptionHandler(NotValidEmailException.class)
    public ResponseEntity<ApiResponse<?>> handleEmailValidation(NotValidEmailException ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(null, ex.getMessage(), false));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUserNotFound(UsernameNotFoundException ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(null, ex.getMessage(), false));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleRequestObjectValidation(MethodArgumentNotValidException ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(null, Objects.requireNonNull(ex.getFieldError()).getDefaultMessage(), false));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleValidFormatInRequest(HttpMessageNotReadableException ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(null, ex.getMessage(), false));
    }

    @ExceptionHandler(UserAlreadyAvailable.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateEmailUser(UserAlreadyAvailable ex){
        log.error((ex.getLocalizedMessage()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(null, ex.getMessage(), false));
    }

    @ExceptionHandler(PartialUserAlreadyAvailable.class)
    public ResponseEntity<ApiResponse<?>> handlePartialUserUpdate(PartialUserAlreadyAvailable ex){
        log.error((ex.getMessage()));
        return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(new ApiResponse<>(null, ex.getMessage(), true));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidApiMethod(HttpRequestMethodNotSupportedException ex){
        log.error((ex.getMessage()));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ApiResponse<>(null, ex.getMessage() + ", Check the API and Request Method Correctly", false));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleForbidden(AccessDeniedException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(null, ex.getMessage()+", you dont have access to the method",false));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodParameterMismatch(MethodArgumentTypeMismatchException ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(null, ex.getMessage(),false));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoHandleMethodForApi(NoHandlerFoundException ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(null, ex.getMessage(),false));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintInRecordAndEntity(ConstraintViolationException ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(null, ex.getMessage(),false));
    }
}
