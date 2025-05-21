package com.melancholia.educationplatform.course.step;

import com.melancholia.educationplatform.course.step.test.ConsoleTest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "ConsoleStep")
@Table(name = "console_step")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ConsoleStep extends Step {

    @NotEmpty(message = "Задание не может быть пустым")
    @Column(name = "task", nullable = false)
    private String task;

    @OneToMany(mappedBy = "consoleStep", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ConsoleTest> tests;

    @OneToMany(mappedBy = "step", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Solution> solutions;

}
