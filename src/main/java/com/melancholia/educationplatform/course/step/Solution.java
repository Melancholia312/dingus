package com.melancholia.educationplatform.course.step;
import com.melancholia.educationplatform.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity(name = "Solution")
@Table(name = "solution")
@NoArgsConstructor
@Data
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "solution_id",
            updatable = false
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "step_id")
    private Step step;

    @Column(name = "solution_date")
    private Date solutionDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "solution_text")
    private String solutionText;


    @Column(name = "is_solved_correct")
    private boolean isSolvedCorrect;

}
