package com.melancholia.educationplatform.course.step;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "WordAnswerStep")
@Table(name = "word_answer_step")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WordAnswerStep extends Step {

    @NotEmpty(message = "Вопрос не может быть пустым")
    @Size(min = 15, max = 75,  message = "Вопрос должен быть длинной от 15 до 75 символов")
    @Column(name = "question", nullable = false, length = 75)
    private String question;

    @NotEmpty(message = "Ответ не может быть пустым")
    @Size(min = 1, max = 50,  message = "Ответ должен быть длинной от 1 до 50 символов")
    @Column(name = "answer", nullable = false, length = 50)
    private String answer;

    @OneToMany(mappedBy = "step", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Solution> solutions;

    @Column(name = "image")
    private String image;

}