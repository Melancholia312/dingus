package com.melancholia.educationplatform.course;

import com.melancholia.educationplatform.course.step.Step;
import com.melancholia.educationplatform.course.module.Module;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CheckList {
    private boolean finalCheck;
    private Long courseId;
    private String courseName;
    private int modulesCount;
    private int modulesRequired;
    private int stepRequired;
    private Map<Module, Boolean> isEnoughSteps;
    private Map<Module, Map<Step, Integer>> moduleWithTestSteps;
}
