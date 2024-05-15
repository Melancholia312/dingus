package com.melancholia.educationplatform.user.groups;

import com.melancholia.educationplatform.course.Course;
import com.melancholia.educationplatform.course.module.Module;
import com.melancholia.educationplatform.user.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor
@PostAuthorize("hasRole('ROLE_ADMIN')")
public class UniversityGroupController {

    private final UniversityGroupService universityGroupService;

    @GetMapping("/group-manager")
    public String viewAllGroups(Model model){
        List<UniversityGroup> groupList = universityGroupService.getAllGroups();
        model.addAttribute("groups", groupList);
        return "group/group-manager";
    }

    @GetMapping("/group/add")
    public String addGroup(Model model){
        model.addAttribute("group", new UniversityGroup());
        return "group/add";
    }

    @PostMapping("/group/add")
    public String createGroup(@Valid @ModelAttribute("group") UniversityGroup group, BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()){
            model.addAttribute("group", group);
            return "group/add";
        }

        universityGroupService.groupSave(group);

        return "redirect:/group-manager";
    }

    @GetMapping("/group/{id}/delete")
    public String deleteGroupForm(@PathVariable("id") long id, Model model) {
        UniversityGroup group = universityGroupService.findGroupById(id);

        model.addAttribute("group", group);
        return "group/delete";
    }

    @PostMapping("/group/{id}/delete")
    public String deleteGroup(@PathVariable("id") long id) {
        universityGroupService.deleteGroupById(id);
        return "redirect:/group-manager";
    }

    @PostMapping("/group/edit")
    public String editGroup(@Valid @ModelAttribute("group") UniversityGroup group, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("group", group);
            return "group/edit";
        }
        universityGroupService.groupSave(group);
        return "redirect:/group-manager";
    }

    @GetMapping("/group/{id}/edit")
    public String editGroupForm(@PathVariable("id") long id, Model model){
        UniversityGroup group = universityGroupService.findGroupById(id);
        model.addAttribute("group", group);
        return "group/edit";
    }
}
