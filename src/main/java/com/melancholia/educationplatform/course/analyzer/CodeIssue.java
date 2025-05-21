package com.melancholia.educationplatform.course.analyzer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CodeIssue {
    private int line;
    private int column;
    private String message;
    private String severity;
}
