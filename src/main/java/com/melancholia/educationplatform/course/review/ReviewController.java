package com.melancholia.educationplatform.course.review;

import com.melancholia.educationplatform.course.Course;
import com.melancholia.educationplatform.course.CourseService;
import com.melancholia.educationplatform.user.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final CourseService courseService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/review/add/{id}")
    public String addReview(@PathVariable("id") long id,
                            @Valid Review review, BindingResult bindingResult,
                            Authentication authentication){
        Course course = courseService.findPublishedCourseById(id);
        if (bindingResult.hasErrors()){
            StringBuilder urlParams = new StringBuilder("?");
            List<String> fieldErrorList = bindingResult.getFieldErrors().stream().map(FieldError::getField).toList();
            for (String fieldError : fieldErrorList){
                urlParams.append(fieldError).append("&");
            }
            if (urlParams.length() > 1){
                urlParams.deleteCharAt(urlParams.length()-1);
                return String.format("redirect:/course/%s/promo%s", id, urlParams);
            }
            return String.format("redirect:/course/%s/promo", id);

        }
        reviewService.deleteByAuthorAndCourseId(((User) authentication.getPrincipal()).getUsername(), course.getId());
        review.setCourse(course);
        review.setAuthor(((User) authentication.getPrincipal()).getUsername());
        reviewService.reviewSave(review);
        return String.format("redirect:/course/%s/promo", id);
    }

}
