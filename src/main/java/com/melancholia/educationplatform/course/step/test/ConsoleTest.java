package com.melancholia.educationplatform.course.step.test;

import com.melancholia.educationplatform.course.step.Step;
import com.melancholia.educationplatform.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity(name = "ConsoleTest")
@Table(name = "consoleTest")
@Data
@NoArgsConstructor
public class ConsoleTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "console_test_id",
            updatable = false
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "step_id")
    private Step consoleStep;

    @NotEmpty(message = "Входные данные не могут быть пустыми")
    @Column(name = "input")
    private String input;

    @NotEmpty(message = "Выходные данные не могут быть пустыми")
    @Column(name = "output")
    private String output;

}
