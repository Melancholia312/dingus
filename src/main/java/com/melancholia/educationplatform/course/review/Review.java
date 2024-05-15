package com.melancholia.educationplatform.course.review;

import com.melancholia.educationplatform.course.Course;
import com.melancholia.educationplatform.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Entity(name = "Review")
@Table(name = "review")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "review_id",
            updatable = false
    )
    private Long id;

    @Size(max = 100,  message = "Текст отзыва не должнен превышать 100 символов")
    @Column(name = "reviewText", nullable = false, length = 100)
    private String reviewText;

    @Column(name = "author", nullable = false)
    private String author;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "stars")
    @Range(min = 1, max = 5, message = "Выберите количество звезд")
    private int stars;

}
