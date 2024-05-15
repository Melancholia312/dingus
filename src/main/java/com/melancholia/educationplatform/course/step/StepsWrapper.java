package com.melancholia.educationplatform.course.step;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class StepsWrapper {
    private List<Step> steps;

    public void addStep(Step step) {
        this.steps.add(step);
    }

}

