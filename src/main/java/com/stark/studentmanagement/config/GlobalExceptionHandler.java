package com.stark.studentmanagement.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Exception ex, Model model) {
        logger.error("Unhandled exception caught by GlobalExceptionHandler", ex);

        String errorDetails = ex.getMessage() != null ? ex.getMessage() : "An unexpected server error occurred.";
        
        model.addAttribute("statusCode", 500);
        model.addAttribute("errorMessage", errorDetails);
        model.addAttribute("title", "Server Error (500)");
        model.addAttribute("theme", "theme-student");

        return "error";
    }
}
