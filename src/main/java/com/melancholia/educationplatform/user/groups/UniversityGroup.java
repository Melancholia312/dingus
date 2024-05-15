package com.melancholia.educationplatform.user.groups;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Entity(name = "UniversityGroup")
@Table(name = "university_group")
@Data
@NoArgsConstructor
public class UniversityGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "group_id",
            updatable = false
    )
    private long id;

    @NotEmpty(message = "Название подгруппы не может быть пустым")
    @Size(min = 2, max = 2,  message = "Название подгруппы должно быть длинной 2")
    @Column(name = "sub_group", nullable = false, length = 2)
    private String subGroup;

    @NotEmpty(message = "Название группы не может быть пустым")
    @Size(min = 4, max = 4,  message = "Название группы должно быть длинной 4")
    @Column(name = "group_name", nullable = false, length = 10)
    private String groupName;

    @Range(min = 1, max = 5)
    @Column(name = "course_number", nullable = false, length = 1)
    private int courseNumber;

    @Range(min = 1, max = 5)
    @Column(name = "group_number", nullable = false, length = 1)
    private int groupNumber;

}
