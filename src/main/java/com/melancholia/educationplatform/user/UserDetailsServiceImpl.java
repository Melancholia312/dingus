package com.melancholia.educationplatform.user;

import com.melancholia.educationplatform.course.Course;
import com.melancholia.educationplatform.course.CourseRepository;
import com.melancholia.educationplatform.user.permissions.PrivilegeRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final CourseRepository courseRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        return user;
    }

    @Transactional
    public void signUpUser(User user) {
        String encodedPassword = passwordEncoder
                .encode(user.getPassword());

        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    public int enableAppUser(String email) {
        return userRepository.enableAppUser(email);
    }

    public int banAppUser(String email) {
        User user = userRepository.findByEmail(email);
        courseRepository.unpublishAllUserCourses(user.getId());
        return userRepository.banAppUser(email);
    }



}
