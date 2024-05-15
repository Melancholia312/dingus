package com.melancholia.educationplatform.core.exception;

public class CourseNotFoundException extends RuntimeException {

    public CourseNotFoundException(String message){
        super(message);
    }
}
