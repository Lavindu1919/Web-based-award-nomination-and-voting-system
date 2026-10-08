package com.sliit.awardvote.common.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Application-wide fallback for exceptions that would otherwise surface as
 * Spring Boot's generic "Whitelabel Error Page".
 *
 * Individual controllers (e.g. RegistrationController) still catch
 * DataIntegrityViolationException locally when they can offer a precise,
 * field-specific message (e.g. "username already taken"). This handler is
 * the safety net for anywhere else a unique-constraint race condition (or
 * any other DB integrity violation, or unexpected exception) could slip
 * through uncaught.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Catches DB constraint violations - most commonly a unique-key race
     * condition, e.g. two concurrent submissions both passing a
     * "does this already exist?" check before either insert commits.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                HttpServletRequest request,
                                                Model model) {
        log.warn("Data integrity violation on {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        model.addAttribute("error", "That value is already in use, or conflicts with existing data. Please check your input and try again.");
        model.addAttribute("errorTitle", "Couldn't save your changes");
        return "error/friendly-error";
    }

    /**
     * Last-resort catch-all so an unexpected exception still shows a normal
     * page instead of the Whitelabel Error Page. Kept generic on purpose -
     * no stack trace or internal detail is shown to the user.
     */
    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception ex,
                                    HttpServletRequest request,
                                    Model model) {
        log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);
        model.addAttribute("error", "Something went wrong on our end. Please try again in a moment.");
        model.addAttribute("errorTitle", "Unexpected error");
        return "error/friendly-error";
    }
}
