package com.melancholia.educationplatform.course.module;

import com.melancholia.educationplatform.course.Course;
import com.melancholia.educationplatform.course.step.Step;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity(name = "Module")
@Table(name = "module")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "module_id",
            updatable = false
    )
    private long id;

    @Column(name = "module_number")
    private long moduleNumber;

    @NotEmpty(message = "Название не может быть пустым")
    @Size(min = 3, max = 75,  message = "Название должно быть длинной от 3 до 75 символов")
    @Column(name = "module_name", length = 75)
    private String name;

    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Step> steps;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Map<String, Long> countSteps(){
        return steps
                .stream()
                .collect(Collectors.groupingBy(
                        step -> step.getClass().getSimpleName(), Collectors.counting()));
    }

    public Step getStepById(long stepId){
        return steps
                .stream()
                .filter(step -> step.getId() == stepId)
                .findAny()
                .orElse(null);
    }

    public void sortSteps(){
        Comparator<Step> compareByNumber = Comparator
                .comparing(Step::getStepNumber);
        steps = steps.stream()
                .sorted(compareByNumber)
                .collect(Collectors.toList());
    }

    public Step getNextStep(Step step){
        int index = steps.indexOf(step);
        if (index < 0 || index+1 == steps.size()) return null;
        return steps.get(index+1);
    }
}
