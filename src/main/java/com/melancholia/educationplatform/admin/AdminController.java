package com.melancholia.educationplatform.admin;

import com.melancholia.educationplatform.user.User;
import com.melancholia.educationplatform.user.UserDetailsServiceImpl;
import com.melancholia.educationplatform.user.UserRepository;
import com.melancholia.educationplatform.user.permissions.Privilege;
import com.melancholia.educationplatform.user.permissions.PrivilegeRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@AllArgsConstructor
@PostAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final PrivilegeRepository privilegeRepository;

    @GetMapping("/admin-panel")
    public String viewAllUsers(Model model) {
        List<User> userList = userRepository.findAll();
        model.addAttribute("users", userList);
        return "admin/panel";
    }

    @GetMapping("/user/{id}/ban")
    public String banUser(@PathVariable("id") long id,
                          Authentication authentication) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (((User) authentication.getPrincipal()).getEmail().equals(user.getEmail())) return "redirect:/admin-panel";
        userDetailsService.banAppUser(user.getEmail());
        return "redirect:/admin-panel";
    }

    @GetMapping("/user/{id}/unban")
    public String unbanUser(@PathVariable("id") long id,
                            Authentication authentication) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (((User) authentication.getPrincipal()).getEmail().equals(user.getEmail())) return "redirect:/admin-panel";
        userDetailsService.enableAppUser(user.getEmail());
        return "redirect:/admin-panel";
    }

    @GetMapping("/user/{id}/make-teacher")
    public String makeTeacher(@PathVariable("id") long id,
                            Authentication authentication) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (((User) authentication.getPrincipal()).getEmail().equals(user.getEmail())) return "redirect:/admin-panel";
        user.setIsTeacher(true);
        Privilege teacher = privilegeRepository.findByName("ROLE_TEACHER");
        userRepository.addPermission(user.getId(), teacher.getId());
        return "redirect:/admin-panel";
    }

    @GetMapping("/user/{id}/remove-teacher")
    public String removeTeacher(@PathVariable("id") long id,
                              Authentication authentication) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (((User) authentication.getPrincipal()).getEmail().equals(user.getEmail())) return "redirect:/admin-panel";
        user.setIsTeacher(false);
        Privilege teacher = privilegeRepository.findByName("ROLE_TEACHER");
        userRepository.deletePermission(user.getId(), teacher.getId());
        return "redirect:/admin-panel";
    }
}
