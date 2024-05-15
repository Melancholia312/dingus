package com.melancholia.educationplatform.user;

import com.melancholia.educationplatform.course.step.Step;
import com.melancholia.educationplatform.user.groups.UniversityGroupService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UniversityGroupService universityGroupService;


    @GetMapping("/settings")
    @PreAuthorize("isAuthenticated()")
    public String editUserForm(Authentication authentication,
                               Model model) {
        User user = userRepository.findByEmail(((User) authentication.getPrincipal()).getEmail());
        model.addAttribute("user", user);
        model.addAttribute("groups", universityGroupService.getAllGroups());
        return "settings";
    }


    @PostMapping("/settings")
    @PreAuthorize("isAuthenticated()")
    public String editUser(@Valid User user, BindingResult bindingResult,
                           Authentication authentication,
                           Model model) {
        String emailExists = null;
        String usernameExists = null;
        User userFromBd = userRepository.findByEmail(((User) authentication.getPrincipal()).getEmail());
        if (userRepository.findByEmail(user.getEmail()) != null &&
                !userRepository.findByEmail(user.getEmail()).getEmail().equals(userFromBd.getEmail())){
            emailExists = String.format("Email %s уже занят", user.getEmail());
        }
        if (userRepository.findByUsername(user.getUsername()) != null &&
                !userRepository.findByUsername(user.getUsername()).getUsername().equals(userFromBd.getUsername())){
            usernameExists = String.format("Имя %s уже занято", user.getUsername());
        }
        if (bindingResult.hasErrors() || emailExists != null || usernameExists != null){
            model.addAttribute("emailExists", emailExists);
            model.addAttribute("usernameExists", usernameExists);
            model.addAttribute("user", user);
            model.addAttribute("groups", universityGroupService.getAllGroups());
            return "settings";
        }
        userFromBd.setEmail(user.getEmail());
        userFromBd.setUsername(user.getUsername());
        userFromBd.setGroup(user.getGroup());
        userRepository.save(userFromBd);
        Authentication auth = new UsernamePasswordAuthenticationToken(userFromBd, null, userFromBd.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return "redirect:/settings";
    }
}
