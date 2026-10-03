package com.stark.studentmanagement.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        int statusCode = 500;
        String errorMessage = "An unexpected internal server error occurred.";

        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            errorMessage = "The page or resource you requested could not be found.";
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            errorMessage = "You do not have permission to access this resource.";
        } else if (message != null && !message.toString().trim().isEmpty()) {
            errorMessage = message.toString();
        } else if (exception != null && exception instanceof Throwable) {
            Throwable t = (Throwable) exception;
            errorMessage = t.getMessage() != null ? t.getMessage() : t.toString();
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("requestUri", requestUri != null ? requestUri.toString() : "");
        model.addAttribute("title", "Error " + statusCode);
        model.addAttribute("theme", "theme-student");

        return "error";
    }
}
