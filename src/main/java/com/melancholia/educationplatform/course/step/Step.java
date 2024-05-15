package com.melancholia.educationplatform.course.step;

import com.melancholia.educationplatform.course.Course;
import com.melancholia.educationplatform.course.module.Module;
import com.melancholia.educationplatform.course.step.comment.Comment;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "Step")
@Table(name = "step")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "step_id",
            updatable = false
    )
    private long id;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;

    @Column(name = "step_number")
    private long stepNumber;

    @OneToMany(mappedBy = "step", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Comment> comments;

    public boolean equals(Object o) {
        if(!(o instanceof Step other)) return false;
        return (this.id == other.id);
    }
}
