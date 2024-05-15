package com.melancholia.educationplatform.registration;

import com.melancholia.educationplatform.user.User;
import com.melancholia.educationplatform.user.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserDetailsServiceImpl userDetailsService;

    public String register(User user) {
        userDetailsService.signUpUser(user);

        return "redirect:/login";
    }


}
