package com.vedrax.errorhandling;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Global exception handler
 *
 * @author remypenchenat
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {


  /**
   * When {@link IllegalArgumentException} is thrown, returns HTTP status
   * 400
   *
   * @param ex      the constraint violation exception
   * @param request the web request
   * @return the response entity
   */
  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<Object> handleIllegalArgumentException(
    IllegalArgumentException ex, WebRequest request) {

    ApiError apiError = new ApiError(BAD_REQUEST, ex.getMessage(), ex);
    return buildResponseEntity(apiError);
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

    ApiError apiError = new ApiError(BAD_REQUEST, "Validation error", ex);
    return buildResponseEntity(apiError);
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

    ApiError apiError = new ApiError(BAD_REQUEST, ex.getMessage(), ex);
    return buildResponseEntity(apiError);
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

    ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex);
    return buildResponseEntity(apiError);
  }

  /**
   * Handle DataIntegrityViolationException, inspects the cause for different DB causes.
   *
   * @param ex the DataIntegrityViolationException
   * @return the ApiError object
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                                WebRequest request) {
    if (ex.getCause() instanceof ConstraintViolationException) {
      return buildResponseEntity(new ApiError(HttpStatus.CONFLICT, "Database error", ex.getCause()));
    }
    return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
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

    ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, ex.getMessage(), ex);
    return buildResponseEntity(apiError);
  }

  /**
   * Handle javax.persistence.EntityNotFoundException
   */
  @ExceptionHandler(EntityNotFoundException.class)
  protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
    return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
  }

  /**
   * Handler for 500 errors
   *
   * @param ex      the exception
   * @param request the request
   * @return ResponseEntity
   */
  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
    logger.info(ex.getClass().getName());
    logger.info(request.getContextPath());
    logger.error("error", ex);

    ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    return buildResponseEntity(apiError);
  }

  private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

}
