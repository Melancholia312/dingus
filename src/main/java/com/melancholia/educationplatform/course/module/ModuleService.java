package com.melancholia.educationplatform.course.module;

import com.melancholia.educationplatform.core.exception.CourseNotFoundException;
import com.melancholia.educationplatform.core.exception.ModuleNotFoundException;
import com.melancholia.educationplatform.course.Course;
import com.melancholia.educationplatform.course.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;

    @PreAuthorize("hasPermission(#courseId, 'Course', 'write')")
    public List<Module> getCourseModules(long courseId){
        return moduleRepository.findByCourseIdOrderByModuleNumber(courseId);
    }

    public void moduleSave(Module module){
        moduleRepository.save(module);
    }

    @PreAuthorize("hasPermission(#id, 'Module', 'write')")
    public void deleteModuleById(long id){
        moduleRepository.deleteById(id);
    }

    @PostAuthorize("hasPermission(#id, 'Module', 'write')")
    public Module findModuleToConstructById(long id){
        return findModuleById(id);
    }

    @PreAuthorize("hasPermission(#moduleId, 'Module', 'write')")
    public void updateModuleNumber(long moduleId, long moduleNumber){
        moduleRepository.updateModuleNumber(moduleNumber, moduleId);
    }

    public Module findModuleById(long id){
        return moduleRepository.findById(id).orElseThrow(
                () -> new ModuleNotFoundException(String.format("Module with ID %s not found", id))
        );
    }

    public long maxModuleNumberCourseId(long courseId){
        Long value = moduleRepository.maxModuleNumberCourseId(courseId);
        if (value == null) return 0;
        return value;
    }
}
