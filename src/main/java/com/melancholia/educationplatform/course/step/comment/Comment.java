package com.melancholia.educationplatform.course.step.comment;

import com.melancholia.educationplatform.course.Course;
import com.melancholia.educationplatform.course.step.Step;
import com.melancholia.educationplatform.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity(name = "Comment")
@Table(name = "comment")
@Data
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "review_id",
            updatable = false
    )
    private Long id;

    @NotEmpty
    @Size(max = 100)
    @Column(name = "reviewText", nullable = false, length = 100)
    private String reviewText;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "creation_date", nullable = false)
    private Date creationDate;

    @ManyToOne
    @JoinColumn(name = "step_id")
    private Step step;

}
