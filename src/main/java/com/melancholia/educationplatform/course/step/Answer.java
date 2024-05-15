package com.melancholia.educationplatform.course.step;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "Answer")
@Table(name = "answer")
@Data
@NoArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "answer_id",
            updatable = false
    )
    private long id;

    @Column(name = "is_correct")
    private boolean correct;

    @NotEmpty(message = "Текст ответа не может быть пустым")
    @Size(min = 1, max = 50,  message = "Текст ответа должен быть длиной от 1 до 50 символов")
    @Column(name = "answer_text", nullable = false)
    private String answerText;

    @Column(name = "image")
    private String image;

    @ManyToOne
    @JoinColumn(name = "step_id")
    private Step testStep;

}
