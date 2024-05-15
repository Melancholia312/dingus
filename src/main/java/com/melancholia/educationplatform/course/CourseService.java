package com.melancholia.educationplatform.course;

import com.melancholia.educationplatform.core.exception.CourseNotFoundException;
import com.melancholia.educationplatform.course.module.Module;
import com.melancholia.educationplatform.course.step.InformationTextStep;
import com.melancholia.educationplatform.course.step.Solution;
import com.melancholia.educationplatform.course.step.SolutionService;
import com.melancholia.educationplatform.course.step.Step;
import com.melancholia.educationplatform.user.User;
import com.melancholia.educationplatform.user.UserRepository;
import com.melancholia.educationplatform.user.permissions.Privilege;
import com.melancholia.educationplatform.user.permissions.PrivilegeRepository;
import com.melancholia.educationplatform.user.permissions.PrivilegeService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final SolutionService solutionService;

    private final PrivilegeService privilegeService;
    private final PrivilegeRepository privilegeRepository;
    private final UserRepository userRepository;

    public List<Course> getAllCourses(){
        return courseRepository.findAll();
    }

    public Course findCourseById(long id){
        return courseRepository.findById(id).orElseThrow(
                () -> new CourseNotFoundException(String.format("Course with ID %s not found", id))
        );
    }

    public Course findPublishedCourseById(long id){
        return courseRepository.findByIdAndIsPublishedTrue(id).orElseThrow(
                () -> new CourseNotFoundException(String.format("Course with ID %s not found", id))
        );
    }

    @PostAuthorize("hasPermission(#id, 'Course', 'write')")
    public Course findCourseToConstructById(long id){
        return findCourseById(id);
    }

    @PreAuthorize("hasPermission(#id, 'Course', 'read')")
    public Course findCourseToLearnById(long id){
        return findCourseById(id);
    }

    @PreAuthorize("hasPermission(#id, 'Course', 'write')")
    public void deleteCourseById(long id){
        courseRepository.deleteById(id);
    }

    @PreAuthorize("hasPermission(#id, 'Course', 'write')")
    public void publishCourse(long id, boolean publish){
        courseRepository.publishCourseById(id, publish);
    }

    @PostFilter("hasPermission(filterObject.id, 'Course', 'write')")
    public List<Course> findCoursesWithUsersPermission(){
        return courseRepository.findAll();
    }

    @PostFilter("hasPermission(filterObject.id, 'Course', 'read')")
    public List<Course> findCoursesWithUserReadPermission(){
        return courseRepository.findAll();
    }

    public Page<Course> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        if (sortField.equals("rating")){
            if (sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())) return courseRepository.findCoursesOrderByStarsAsc(pageable);
            return courseRepository.findCoursesOrderByStarsDesc(pageable);
        } else if (sortField.equals("students")){
            if (sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())) return courseRepository.findCoursesOrderByUsersCountAsc(pageable);
            return courseRepository.findCoursesOrderByUsersCountDesc(pageable);
        }
        return courseRepository.findAllPublishedCourses(pageable);
    }

    public Page<Course> search(int pageNo, int pageSize, String keyword) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return courseRepository.search(pageable, keyword);
    }

    public void courseSave(Course course){
        courseRepository.save(course);
    }

    public void enrollUser(long userId, long courseId){
        courseRepository.enrollUser(userId, courseId);
    }

    public boolean isMember(long userId, long courseId){
        return courseRepository.isMember(courseId, userId);
    }

    public Map<Long, Double> checkUserProgress(long userId, List<Long> courseIds){
        Map<Long, Double> userCourseMap = new HashMap<>();
        for (Long courseId : courseIds){
            double userProgress = 0;
            double stepsCount = 0;
            Course course = courseRepository.findByIdAndIsPublishedTrue(courseId).orElse(null);
            if (course == null) continue;
            for (Module module : course.getModules()){
                for (Step step : module.getSteps()){
                    if (!(step instanceof InformationTextStep)){
                        stepsCount++;
                        if (solutionService.getUserSolutions(userId, step.getId()).stream()
                                .filter(Solution::isSolvedCorrect).findAny().orElse(null) != null){
                            userProgress++;
                        }
                    }
                }
            }
            userCourseMap.put(courseId, Math.round(userProgress/stepsCount * 100.0) / 100.0);
        }
        return userCourseMap;
    }

    @Transactional
    public void unsubscribe(User user, long courseId){
        Privilege privilege = privilegeRepository.findByName(String.format("COURSE_%s_READ",courseId));
        privilegeService.deletePermissionUser(user.getId(), privilege.getId());
        userRepository.unsubscribe(user.getId(), courseId);
    }

    @Transactional
    public void deleteCoursePermission(long userId, long courseId){
        Privilege privilegeWrite = privilegeRepository.findByName(String.format("COURSE_%s_WRITE", courseId));
        privilegeService.deletePermissionUser(userId, privilegeWrite.getId());
    }

    @Transactional
    public void deleteModulePermissions(long userId, long moduleIde){
        Privilege privilegeWrite = privilegeRepository.findByName(String.format("MODULE_%s_WRITE", moduleIde));
        privilegeService.deletePermissionUser(userId, privilegeWrite.getId());
    }

    @Transactional
    public void deleteStepPermissions(long userId, long stepId){
        Privilege privilegeWrite = privilegeRepository.findByName(String.format("STEP_%s_WRITE", stepId));
        privilegeService.deletePermissionUser(userId, privilegeWrite.getId());
    }

    @Transactional
    public void deleteAnswerPermissions(long userId, long answerId){
        Privilege privilegeWrite = privilegeRepository.findByName(String.format("ANSWER_%s_WRITE", answerId));
        privilegeService.deletePermissionUser(userId, privilegeWrite.getId());
    }

    public List<Course> getCoursesByAuthor(long userId){
        return courseRepository.getCoursesByAuthor(userId);
    }

    public List<Course> getCoursesByGroup(long groupId){
        return courseRepository.getCoursesByGroup(groupId);
    }
}
