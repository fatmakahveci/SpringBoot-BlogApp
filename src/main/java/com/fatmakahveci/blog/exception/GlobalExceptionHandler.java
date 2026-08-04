package com.fatmakahveci.blog.exception;

import java.time.Instant;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({PostNotFoundException.class, TagNotFoundException.class, NoResourceFoundException.class})
    public Object handleNotFound(Exception exception, HttpServletRequest request) {
        String message = exception instanceof NoResourceFoundException
                ? "The requested resource could not be found."
                : exception.getMessage();
        return response(HttpStatus.NOT_FOUND, message, request);
    }

    @ExceptionHandler({
            BindException.class,
            HandlerMethodValidationException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public Object handleBadRequest(Exception exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "The request is invalid.", request);
    }

    @ExceptionHandler({
            DuplicatePostTitleException.class,
            DuplicateTagNameException.class,
            DuplicateUsernameException.class,
            DataIntegrityViolationException.class
    })
    public Object handleConflict(Exception exception, HttpServletRequest request) {
        String message = exception instanceof DataIntegrityViolationException
                ? "The request conflicts with existing data."
                : exception.getMessage();
        return response(HttpStatus.CONFLICT, message, request);
    }

    @ExceptionHandler(Exception.class)
    public Object handleUnexpected(Exception exception, HttpServletRequest request) {
        Sentry.captureException(exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", request);
    }

    private Object response(HttpStatus status, String message, HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/api/")) {
            ApiError error = new ApiError(
                    Instant.now(),
                    status.value(),
                    status.getReasonPhrase(),
                    message,
                    request.getRequestURI());
            return ResponseEntity.status(status).body(error);
        }

        ModelAndView modelAndView = new ModelAndView("error/error");
        modelAndView.setStatus(status);
        modelAndView.addObject("status", status.value());
        modelAndView.addObject("error", status.getReasonPhrase());
        modelAndView.addObject("message", message);
        modelAndView.addObject("path", request.getRequestURI());
        return modelAndView;
    }
}
