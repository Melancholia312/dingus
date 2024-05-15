package com.melancholia.educationplatform.core.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AnswerNotFoundException.class)
    public String handleAnswerNotFoundException(AnswerNotFoundException exception) {
        return "404";
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public String handleCourseNotFoundException(CourseNotFoundException exception) {
        return "404";
    }

    @ExceptionHandler(ModuleNotFoundException.class)
    public String handleModuleNotFoundException(ModuleNotFoundException exception) {
        return "404";
    }

    @ExceptionHandler(StepNotFoundException.class)
    public String handleStepNotFoundException(StepNotFoundException exception) {
        return "404";
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFoundException(UsernameNotFoundException exception) {
        return "404";
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public String handleGroupNotFoundException(GroupNotFoundException exception) {
        return "404";
    }
}
