package com.melancholia.educationplatform.registration;

import com.melancholia.educationplatform.user.User;
import com.melancholia.educationplatform.user.UserRepository;
import com.melancholia.educationplatform.user.groups.UniversityGroupService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(path = "registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserRepository userRepository;
    private final UniversityGroupService universityGroupService;

    @GetMapping
    public String register(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("groups", universityGroupService.getAllGroups());
        return "registration";
    }

    @PostMapping
    public String register(@Valid User user, BindingResult bindingResult, Model model,
                           @RequestParam("passwordRepeat") String passwordRepeat) {
        String passwordsDif = null;
        String emailExists = null;
        String usernameExists = null;

        if (!user.getPassword().equals(passwordRepeat)){
            passwordsDif = "Пароли не совпадают";
        }
        if (userRepository.findByEmail(user.getEmail()) != null){
            emailExists = String.format("Email %s уже занят", user.getEmail());
        }
        if (userRepository.findByUsername(user.getUsername()) != null){
            usernameExists = String.format("Имя %s уже занято", user.getUsername());
        }
        if (bindingResult.hasErrors() || passwordsDif != null || emailExists != null || usernameExists != null){
            model.addAttribute("passwordsError", passwordsDif);
            model.addAttribute("emailExists", emailExists);
            model.addAttribute("usernameExists", usernameExists);
            model.addAttribute("user", user);
            return "registration";
        }

        return registrationService.register(user);
    }

}
