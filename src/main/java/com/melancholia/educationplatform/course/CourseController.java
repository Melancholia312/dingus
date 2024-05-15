package com.melancholia.educationplatform.course;


import com.melancholia.educationplatform.core.exception.ModuleNotFoundException;
import com.melancholia.educationplatform.core.exception.StepNotFoundException;
import com.melancholia.educationplatform.course.module.Module;
import com.melancholia.educationplatform.course.module.ModuleService;
import com.melancholia.educationplatform.course.module.ModulesWrapper;
import com.melancholia.educationplatform.course.review.Review;
import com.melancholia.educationplatform.course.step.*;
import com.melancholia.educationplatform.course.step.comment.Comment;
import com.melancholia.educationplatform.user.User;
import com.melancholia.educationplatform.user.UserRepository;
import com.melancholia.educationplatform.user.permissions.PrivilegeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final PrivilegeService privilegeService;

    private final ModuleService moduleService;

    private final CourseRepository courseRepository;

    private final SolutionService solutionService;
    private final ChecklistService checklistService;

    private final UserRepository userRepository;

    @GetMapping("/")
    public String mainPageWithCourses() {
        return "redirect:/catalog/page/1";
    }

    @GetMapping("/course/add")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public String createCourse(Model model) {
        model.addAttribute("course", new Course());
        return "course/add";
    }

    @GetMapping("/course/{id}/edit")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public String editCourseForm(@PathVariable("id") long id, Model model) {
        Course course = courseService.findCourseToConstructById(id);
        if (course.isPublished()){
            return "redirect:/";
        }
        model.addAttribute("course", course);
        return "course/edit";
    }

    @PostMapping("/course/add")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public String createCourse(@Valid Course course, BindingResult bindingResult,
                               Model model,
                               Authentication authentication) {
        if (bindingResult.hasErrors()){
            model.addAttribute("course", course);
            return "course/add";
        }
        course.setCreationDate(new Date());
        course.addAuthor(((User) authentication.getPrincipal()));
        courseService.courseSave(course);

        privilegeService.addPermissionToUser(
                authentication,
                Course.class.getSimpleName(),
                String.valueOf(course.getId()),
                "write");

        return "redirect:/my-courses";
    }

    @PostMapping("/course/edit")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public String editCourse(@Valid Course course, BindingResult bindingResult,
                             Model model) {
        if (course.isPublished()){
            return "redirect:/";
        }
        if (bindingResult.hasErrors()){
            model.addAttribute("course", course);
            return "course/edit";
        }
        course.setCreationDate(new Date());
        courseService.courseSave(course);
        return "redirect:/my-courses";
    }

    @GetMapping("/course/{id}/delete")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public String deleteCourseForm(@PathVariable("id") long id, Model model) {
        Course course = courseService.findCourseToConstructById(id);
        if (course.isPublished()){
            return "redirect:/";
        }
        model.addAttribute("course", course);
        return "course/delete";
    }

    @PostMapping("/course/{id}/delete")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public String deleteCourse(@PathVariable("id") long id,
                               Authentication authentication) {
        Course course = courseService.findCourseToConstructById(id);
        if (course.isPublished()){
            return "redirect:/";
        }
        courseService.deleteCourseById(id);
        courseService.deleteCoursePermission(((User) authentication.getPrincipal()).getId(), id);
        return "redirect:/my-courses";
    }

    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public String getCreatedCourses(Authentication authentication,
                                    Model model) {
        model.addAttribute("courses", courseService.findCoursesWithUsersPermission());
        return "course/my-courses";
    }

    @GetMapping("/course/{id}/constructor")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public String getConstructorCourseById(@PathVariable("id") long id,
                                           Authentication authentication,
                                           Model model) {
        Course course = courseService.findCourseToConstructById(id);
        if (course.isPublished()){
            return "redirect:/";
        }
        model.addAttribute("course", course);
        ModulesWrapper modulesWrapper = new ModulesWrapper(moduleService.getCourseModules(id));
        model.addAttribute("modules", modulesWrapper);
        return "course/course-constructor";
    }

    @PostMapping("/course/{id}/publish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public String publishCourse(@PathVariable("id") long id) {
        if (checklistService.check(id).isFinalCheck()){
            courseService.publishCourse(id, true);
            return "redirect:/my-courses";
        } else {
            return String.format("redirect:/course/%s/checklist", id);
        }

    }

    @GetMapping("/course/{id}/checklist")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public String checklist(@PathVariable("id") long id,
                            Model model){
        Course course = courseService.findCourseToConstructById(id);
        if (course.isPublished()){
            return "redirect:/";
        }
        CheckList checkList = checklistService.check(id);
        model.addAttribute("checklist", checkList);
        return "course/checklist";
    }

    @PostMapping("/course/{id}/close")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public String closeCourse(@PathVariable("id") long id) {
        courseService.publishCourse(id, false);
        return "redirect:/my-courses";
    }

    @GetMapping("/course/{id}/promo")
    public String getPromoCourseById(@PathVariable("id") long id,
                                     Model model,
                                     Authentication authentication) {
        boolean isMember;
        if (authentication == null) {
            isMember = false;
        } else {
            isMember = courseService.isMember(((User) authentication.getPrincipal()).getId(), id);
        }
        Course course = courseService.findPublishedCourseById(id);
        course.sortModules();
        Module firstModule = course.getModules().get(0);
        firstModule.sortSteps();

        model.addAttribute("course", course);
        model.addAttribute("isMember", isMember);
        model.addAttribute("review", new Review());
        model.addAttribute("firstModuleId", firstModule.getId());
        model.addAttribute("firstStepId", firstModule.getSteps().get(0).getId());
        return "course/promo";

    }

    @PostMapping("/course/{id}/enroll")
    @PreAuthorize("isAuthenticated()")
    public String enrollToCourseById(@PathVariable("id") long id,
                                     Authentication authentication) {
        privilegeService.addPermissionToUser(
                authentication,
                Course.class.getSimpleName(),
                String.valueOf(id),
                "read");
        courseService.enrollUser(((User) authentication.getPrincipal()).getId(), id);
        return String.format("redirect:/course/%s/promo", id);
    }

    @GetMapping("/catalog/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                                @RequestParam(value = "sortField", required = false, defaultValue = "date") String sortField,
                                @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir,
                                Model model) {
        int pageSize = 6;

        Page<Course> page = courseService.findPaginated(pageNo, pageSize, sortField, sortDir);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("listCourses", page.getContent());
        return "course/catalog";
    }

    @GetMapping("/catalog/search/page/{pageNo}")
    public String search(@PathVariable(value = "pageNo") int pageNo,
                         @RequestParam(value = "keyword") String keyword,
                         Model model) {
        int pageSize = 6;

        Page<Course> page = courseService.search(pageNo, pageSize, keyword);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("keyword", keyword);
        model.addAttribute("listCourses", page.getContent());
        return "course/catalog";
    }

    @GetMapping("/course/{id}/passing")
    public String coursePassing(@PathVariable(value = "id") int id,
                                @RequestParam(value = "moduleId") String moduleId,
                                @RequestParam(value = "stepId") String stepId,
                                Model model,
                                Authentication authentication) {
        Course course = courseService.findCourseToLearnById(id);
        course.sortModules();
        Module module = course.getModuleById(Long.parseLong(moduleId));
        if (module == null) throw new ModuleNotFoundException("Module not found");
        List<Solution> solutions = solutionService.getUserSolutions(
                ((User) authentication.getPrincipal()).getId(),
                Long.parseLong(stepId));
        Comparator<Solution> compareByDate = Comparator
                .comparing(Solution::getSolutionDate).reversed();
        boolean isPassed = false;
        if (solutions.stream().filter(Solution::isSolvedCorrect).findAny().orElse(null) != null) {
            isPassed = true;
        }
        for (Module sortModule : course.getModules()) {
            sortModule.sortSteps();
        }
        Step step = module.getStepById(Long.parseLong(stepId));
        if (step == null) throw new StepNotFoundException("Step not found");
        Step nextStep = module.getNextStep(step);
        Module nextModule = module;
        if (nextStep == null) {
            nextModule = course.getNextModule(module);
            if (nextModule != null) {
                nextStep = nextModule.getSteps().get(0);
            }
        }

        model.addAttribute("course", course);
        model.addAttribute("module", module);
        model.addAttribute("step", step);
        model.addAttribute("nextModule", nextModule);
        model.addAttribute("nextStep", nextStep);
        model.addAttribute("newComment", new Comment());
        model.addAttribute("isPassed", isPassed);
        model.addAttribute("userSolutions", solutions.stream().sorted(compareByDate).collect(Collectors.toList()));
        model.addAttribute("userProgress",
                courseService.checkUserProgress(((User) authentication.getPrincipal()).getId(), List.of(course.getId())));
        return "course/passing";
    }

    @GetMapping("/my-learning")
    @PreAuthorize("isAuthenticated()")
    public String getLearningCourses(Authentication authentication,
                                     Model model) {
        List<Course> courses = courseService.findCoursesWithUserReadPermission();
        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        model.addAttribute("courses", courses);
        model.addAttribute("userProgressMap",
                courseService.checkUserProgress(((User) authentication.getPrincipal()).getId(), courseIds));
        return "course/my-learning";
    }


}
