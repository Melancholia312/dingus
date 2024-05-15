package com.melancholia.educationplatform.course.step.comment;

import com.melancholia.educationplatform.course.Course;
import com.melancholia.educationplatform.course.CourseService;
import com.melancholia.educationplatform.course.review.ReviewService;
import com.melancholia.educationplatform.course.step.StepService;
import com.melancholia.educationplatform.user.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor
public class CommentController {
    private final CourseService courseService;
    private final StepService stepService;

    private final CommentService commentService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/add/{id}")
    public String addReview(@PathVariable("id") long id,
                            @Valid Comment comment, BindingResult bindingResult,
                            @RequestParam(value = "stepId") String stepId,
                            @RequestParam(value = "moduleId") String moduleId,
                            Authentication authentication){
        if (bindingResult.hasErrors()){
            return String.format("redirect:/course/%s/passing?moduleId=%s&stepId=%s&commentError", id, moduleId, stepId);

        }
        comment.setStep(stepService.findStepById(Long.parseLong(stepId)));
        comment.setAuthor(((User) authentication.getPrincipal()).getUsername());
        comment.setCreationDate(new Date());
        commentService.commentSave(comment);
        return String.format("redirect:/course/%s/passing?moduleId=%s&stepId=%s", id, moduleId, stepId);
    }

}
