package com.melancholia.educationplatform.course.step;


import com.melancholia.educationplatform.course.step.answer.Answer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "TestStep")
@Table(name = "test_step")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TestStep extends Step {

    @NotEmpty(message = "Вопрос не может быть пустым")
    @Size(min = 15, max = 75, message = "Вопрос должен быть длиной от 15 до 75 символов")
    @Column(name = "question", nullable = false, length = 15)
    private String question;

    @Column(name = "multi_answers", nullable = false)
    private boolean multiAnswers;

    @OneToMany(mappedBy = "testStep", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Answer> answers;

    @OneToMany(mappedBy = "step", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Solution> solutions;

    @Column(name = "image")
    private String image;

}
