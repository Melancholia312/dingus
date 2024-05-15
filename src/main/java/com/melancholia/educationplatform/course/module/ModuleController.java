package com.melancholia.educationplatform.course.module;

import com.melancholia.educationplatform.course.Course;
import com.melancholia.educationplatform.course.CourseService;
import com.melancholia.educationplatform.user.User;
import com.melancholia.educationplatform.user.permissions.PrivilegeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller("module")
@AllArgsConstructor
public class ModuleController {

    private final CourseService courseService;

    private final ModuleService  moduleService;

    private final PrivilegeService privilegeService;


    @GetMapping("/module/add")
    public String addModule(@RequestParam(name = "courseId") long courseId,
                            Model model){
        Course course = courseService.findCourseToConstructById(courseId);
        if (course.isPublished()){
            return "redirect:/";
        }
        Module module = new Module();
        model.addAttribute("module", module);
        model.addAttribute("course", course);
        return "module/add";
    }

    @PostMapping("/module/add")
    public String createModule(@RequestParam(name = "courseId") long courseId,
                               @Valid Module module, BindingResult bindingResult,
                               Authentication authentication, Model model){

        Course course = courseService.findCourseToConstructById(courseId);
        if (course.isPublished()){
            return "redirect:/";
        }
        if (bindingResult.hasErrors()){
            model.addAttribute("course", course);
            model.addAttribute("module", module);
            return "module/add";
        }
        module.setCourse(course);
        module.setModuleNumber(moduleService.maxModuleNumberCourseId(courseId)+1);

        moduleService.moduleSave(module);
        privilegeService.addPermissionToUser(
                authentication,
                Module.class.getSimpleName(),
                String.valueOf(module.getId()),
                "write");
        return String.format("redirect:/course/%s/constructor", courseId);
    }

    @PostMapping("/module/edit")
    public String editModule(@RequestParam(name = "courseId") long courseId,
                             @Valid Module module, BindingResult bindingResult, Model model){
        Course course = courseService.findCourseToConstructById(courseId);
        if (course.isPublished()){
            return "redirect:/";
        }
        if (bindingResult.hasErrors()){
            model.addAttribute("module", module);
            return "module/edit";
        }
        moduleService.moduleSave(module);
        return String.format("redirect:/course/%s/constructor", courseId);
    }

    @GetMapping("/module/{id}/edit")
    public String editModule(@PathVariable("id") long id, Model model){
        Module module =  moduleService.findModuleToConstructById(id);
        if (module.getCourse().isPublished()){
            return "redirect:/";
        }
        model.addAttribute("module", module);
        return "module/edit";
    }

    @GetMapping("/module/{id}/delete")
    public String deleteModuleForm(@PathVariable("id") long id, Model model){
        Module module =  moduleService.findModuleToConstructById(id);
        if (module.getCourse().isPublished()){
            return "redirect:/";
        }
        model.addAttribute("module", module);
        return "module/delete";
    }

    @PostMapping("/module/{id}/delete")
    public String deleteModule(@RequestParam(name = "courseId") long courseId,
                               @PathVariable("id") long id,
                               Authentication authentication){
        Module module =  moduleService.findModuleToConstructById(id);
        if (module.getCourse().isPublished()){
            return "redirect:/";
        }
        moduleService.deleteModuleById(id);
        courseService.deleteModulePermissions(((User) authentication.getPrincipal()).getId(), id);
        return String.format("redirect:/course/%s/constructor", courseId);
    }

    @PostMapping("/module/switch")
    public String switchModules(@RequestParam(name = "courseId") long courseId,
                                @ModelAttribute ModulesWrapper modules){
        Course course = courseService.findCourseToConstructById(courseId);
        if (course.isPublished()){
            return "redirect:/";
        }
        for (Module module : modules.getModules()){
            moduleService.updateModuleNumber(module.getId(), module.getModuleNumber());
        }
        return String.format("redirect:/course/%s/constructor", courseId);
    }


}
