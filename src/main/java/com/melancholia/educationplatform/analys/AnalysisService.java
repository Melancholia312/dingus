package com.melancholia.educationplatform.analys;

import com.melancholia.educationplatform.course.Course;
import com.melancholia.educationplatform.course.CourseService;
import com.melancholia.educationplatform.course.step.Solution;
import com.melancholia.educationplatform.course.step.SolutionService;
import com.melancholia.educationplatform.user.User;
import com.melancholia.educationplatform.user.UserRepository;
import com.melancholia.educationplatform.user.groups.UniversityGroup;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AnalysisService {

    private final CourseService courseService;
    private final UserRepository userRepository;

    public AnalysisResult analysisByTeacher(List<User> teachers){
        AnalysisResult analysisResult = new AnalysisResult();
        Map<User, Double> marksPerTeacher = new HashMap<>();
        Map<User, Set<Course>> coursesPerTeacher = new HashMap<>();
        Set<User> uniqueUsers = new HashSet<>();

        for (User teacher : teachers){
            coursesPerTeacher.put(teacher, new HashSet<>(courseService.getCoursesByAuthor(teacher.getId())));
        }

        for (Map.Entry<User, Set<Course>> entry : coursesPerTeacher.entrySet()) {
            double sumOfMarks = 0;
            int studentsCount = 0;
            for (Course course : entry.getValue()){
                for (User user : course.getUsers()){
                    sumOfMarks += courseService.checkUserProgress(user.getId(), List.of(course.getId())).get(course.getId());
                    studentsCount++;
                    uniqueUsers.add(user);
                }
            }
            marksPerTeacher.put(entry.getKey(),  Math.round(sumOfMarks/studentsCount * 100.0) / 100.0);
        }

        analysisResult.setAverageMark(Math.round(marksPerTeacher.values().stream().mapToDouble(a -> a).average().getAsDouble() * 100.0) / 100.0);
        analysisResult.setBestObject(Collections.max(marksPerTeacher.entrySet(), Map.Entry.comparingByValue()).getKey());
        analysisResult.setWorstObject(Collections.min(marksPerTeacher.entrySet(), Map.Entry.comparingByValue()).getKey());
        analysisResult.setStudentsCount(uniqueUsers.size());
        analysisResult.setMarksPerObject(marksPerTeacher);
        analysisResult.setOption(false);
        analysisResult.formatMap();
        return analysisResult;
    }

    public AnalysisResult analysisByGroups(List<UniversityGroup> groups){
        AnalysisResult analysisResult = new AnalysisResult();
        Map<UniversityGroup, Double> marksPerGroup = new HashMap<>();
        Map<UniversityGroup, Set<Course>> coursesPerGroup = new HashMap<>();
        Set<User> uniqueUsers = new HashSet<>();

        for (UniversityGroup group : groups){
            coursesPerGroup.put(group, new HashSet<>(courseService.getCoursesByGroup(group.getId())));
        }

        for (Map.Entry<UniversityGroup, Set<Course>> entry : coursesPerGroup.entrySet()) {
            double sumOfMarks = 0;
            int studentsCount = 0;
            for (Course course : entry.getValue()){
                for (User user : userRepository.findByGroupId(entry.getKey().getId())){
                    if (user.getIsTeacher()) continue;
                    sumOfMarks += courseService.checkUserProgress(user.getId(), List.of(course.getId())).get(course.getId());
                    studentsCount++;
                    uniqueUsers.add(user);
                }
            }
            marksPerGroup.put(entry.getKey(),  Math.round(sumOfMarks/studentsCount * 100.0) / 100.0);
        }
       
        analysisResult.setAverageMark(Math.round(marksPerGroup.values().stream().mapToDouble(a -> a).average().getAsDouble() * 100.0) / 100.0);
        analysisResult.setBestObject(Collections.max(marksPerGroup.entrySet(), Map.Entry.comparingByValue()).getKey());
        analysisResult.setWorstObject(Collections.min(marksPerGroup.entrySet(), Map.Entry.comparingByValue()).getKey());
        analysisResult.setStudentsCount(uniqueUsers.size());
        analysisResult.setMarksPerObject(marksPerGroup);
        analysisResult.setOption(true);
        analysisResult.formatMap();
        return analysisResult;
    }


}
