package com.vedrax.security.exception;

import com.vedrax.security.enveloppe.ResponseWrapper;
import com.vedrax.security.enveloppe.Status;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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

    //keep 400 by default
    status = HttpStatus.BAD_REQUEST;

    return handleExceptionInternal(ex, toWrapper(status.value(), ex.getLocalizedMessage(), ex.getMessage()),
      new HttpHeaders(), status, request);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
    MissingServletRequestParameterException ex, HttpHeaders headers,
    HttpStatus status, WebRequest request) {

    String error = ex.getParameterName() + " parameter is missing";
    return handler(ex, HttpStatus.BAD_REQUEST, error);

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
    return handler(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
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
    return handler(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
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
    return handler(ex, HttpStatus.UNAUTHORIZED, ex.getMessage());
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
    return handler(ex, HttpStatus.FORBIDDEN, ex.getMessage());
  }

  /**
   * Handler for 500 errors
   *
   * @param ex
   * @param request
   * @return
   */
  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
    logger.info(ex.getClass().getName());
    logger.info(request.getContextPath());
    logger.error("error", ex);

    return handler(ex, HttpStatus.INTERNAL_SERVER_ERROR, "error occurred");
  }

  private ResponseEntity<Object> handler(Exception ex, HttpStatus status, String error) {

    return new ResponseEntity<Object>(
      toWrapper(status.value(), ex.getLocalizedMessage(), error), new HttpHeaders(), status);
  }

  private ResponseWrapper toWrapper(int code, String type, String error) {
    ResponseWrapper wrapper = new ResponseWrapper();
    Status statusObj = new Status();
    statusObj.setError(true);
    statusObj.setCode(code);
    statusObj.setType(type);
    statusObj.setMessage(error);
    wrapper.setStatus(statusObj);
    return wrapper;
  }

}
