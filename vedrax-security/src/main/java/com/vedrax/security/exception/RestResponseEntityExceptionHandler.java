package com.vedrax.security.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

/**
 * Global exception handler
 *
 * @author remypenchenat
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * When {@link MethodArgumentNotValidException} is thrown, returns HTTP
     * status 400
     *
     * @param ex      the exception
     * @param headers the header
     * @param status  the status
     * @param request the web request
     * @return the response entity
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        return handler(ex, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * When {@link ConstraintViolationException} is thrown, returns HTTP status
     * 400
     *
     * @param ex      the constraint violation exception
     * @param request the web request
     * @return the response entity
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        return handler(ex, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * When {@link ApiException} is thrown, returns HTTP status 400
     *
     * @param ex      the api exception
     * @param request the web request
     * @return the response entity
     */
    @ExceptionHandler({ApiException.class})
    public ResponseEntity<Object> handleApiException(
            ApiException ex, WebRequest request) {
        return handler(ex, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * When {@link BadCredentialsException} is thrown, returns HTTP status 401
     *
     * @param ex      the bad credentials exception
     * @param request the web request
     * @return the response entity
     */
    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<Object> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {
        return handler(ex, HttpStatus.UNAUTHORIZED, request);
    }

    /**
     * When {@link AccessDeniedException} is thrown, returns HTTP status 403
     *
     * @param ex      the access denied exception
     * @param request the web request
     * @return the response entity
     */
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        return handler(ex, HttpStatus.FORBIDDEN, request);
    }

    /**
     * Utility method for handling exception
     *
     * @param ex      the exception
     * @param status  the HTTP status
     * @param request the web request
     * @return the response entity
     */
    private ResponseEntity<Object> handler(Exception ex, HttpStatus status, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), status, request);
    }

}
