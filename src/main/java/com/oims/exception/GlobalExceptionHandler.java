package com.oims.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView notFound(NotFoundException ex, HttpServletRequest req) {
        return errorPage(404, "Record not found", ex.getMessage(), req);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView badRequest(Exception ex, HttpServletRequest req) {
        return errorPage(400, "Bad request", "The link you followed has an invalid or missing value.", req);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView noPage(Exception ex, HttpServletRequest req) {
        return errorPage(404, "Page not found", "There is no page at this address.", req);
    }

    // a business rule that was not caught in the controller itself
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ModelAndView business(BusinessException ex, HttpServletRequest req) {
        return errorPage(422, "Action not allowed", ex.getMessage(), req);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView other(Exception ex, HttpServletRequest req) {
        log.error("Unhandled error on {}", req.getRequestURI(), ex);
        return errorPage(500, "Something went wrong", "An unexpected error occurred. Please try again.", req);
    }

    private ModelAndView errorPage(int code, String title, String message, HttpServletRequest req) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("code", code);
        mav.addObject("title", title);
        mav.addObject("message", message);
        mav.addObject("path", req.getRequestURI());
        return mav;
    }
}
