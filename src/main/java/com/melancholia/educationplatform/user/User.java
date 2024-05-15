package com.melancholia.educationplatform.user;

import com.melancholia.educationplatform.course.Course;
import com.melancholia.educationplatform.course.review.Review;
import com.melancholia.educationplatform.user.groups.UniversityGroup;
import com.melancholia.educationplatform.user.permissions.Privilege;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity(name = "User")
@Table(name = "user")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "user_id",
            updatable = false
    )
    private Long id;

    @NotEmpty(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 75,  message = "Имя пользователя дожно быть длинной от 3 до 75 символов")
    @Column(name = "username", nullable = false, unique = true, length = 75)
    private String username;

    @NotEmpty(message = "Email не может быть пустым")
    @Size(max = 150,  message = "Email должен быть не больше 150 символов")
    @Email(message = "Некорректный Email")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private UniversityGroup group;

    @NotEmpty(message = "Пароль не может быть пустым")
    @Size(max = 150,  message = "Пароль должен быть не больше 100 символов")
    @Column(name = "password", length = 100)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_privileges",
            joinColumns =
            @JoinColumn(name = "user_id"),
            inverseJoinColumns =
            @JoinColumn(name = "privilege_id"))
    private Set<Privilege> privileges = new HashSet<>();

    private Boolean locked = false;
    private Boolean enabled = true;
    private Boolean isTeacher = false;

    public void addPrivilege(Privilege privilege){
        privileges.add(privilege);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Privilege privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege.getName()));
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
