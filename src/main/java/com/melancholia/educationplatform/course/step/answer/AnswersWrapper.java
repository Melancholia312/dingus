package com.melancholia.educationplatform.course.step.answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswersWrapper {
    private List<Answer> answers;

    public void addModule(Answer answer) {
        this.answers.add(answer);
    }

}
