package com.melancholia.educationplatform.course;


import com.melancholia.educationplatform.course.module.Module;
import com.melancholia.educationplatform.course.review.Review;
import com.melancholia.educationplatform.user.User;
import com.melancholia.educationplatform.course.step.Step;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.*;
import java.util.stream.Collectors;


@Entity(name = "Course")
@Table(name = "course")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "course_id",
            updatable = false
    )
    private long id;

    @NotEmpty(message = "Название не может быть пустым")
    @Size(min = 3, max = 75,  message = "Название должно быть длинной от 3 до 75 символов")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price")
    private double price;

    @Column(name = "comments_allowed")
    private boolean commentsAllowed;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Module> modules;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "course_users",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "course_authors",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> authors = new HashSet<>();

    @NotEmpty(message = "Краткое описание не может быть пустым")
    @Size(min = 50, max = 150,  message = "Краткое описание должно быть длинной от 50 до 150 символов")
    @Column(name = "short_description", length = 150)
    private String shortDescription;

    @NotEmpty(message = "Описание не может быть пустым")
    @Size(min = 200, max = 5000,  message = "Описание должно быть длинной от 200 до 5000 символов")
    @Column(name = "description", length = 5000)
    private String description;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviews;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "is_published")
    private boolean isPublished = false;


    public void addAuthor(User user){
        authors.add(user);
    }

    public int countStudents(){
        return users.size();
    }

    public double avgRating(){
        return (double) Math.round(reviews.stream()
                .mapToDouble(Review::getStars)
                .average()
                .orElse(0) * 100) /  100;
    }

    public Module getModuleById(long moduleId){
        return modules
                .stream()
                .filter(module -> module.getId() == moduleId)
                .findAny()
                .orElse(null);
    }

    public void sortModules(){
        Comparator<Module> compareByNumber = Comparator
                .comparing(Module::getModuleNumber);
        modules = modules.stream()
                .sorted(compareByNumber)
                .collect(Collectors.toList());
    }

    public Module getNextModule(Module module){
        int index = modules.indexOf(module);
        if (index < 0 || index+1 == modules.size()) return null;
        return modules.get(index+1);
    }
}
