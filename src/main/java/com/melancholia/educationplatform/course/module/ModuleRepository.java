package com.melancholia.educationplatform.course.module;

import com.melancholia.educationplatform.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    public List<Module> findByCourseIdOrderByModuleNumber(long courseId);

    @Query(value = "SELECT MAX(module_number) from Module WHERE course_id = ?1", nativeQuery = true)
    Long maxModuleNumberCourseId(long courseId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE module SET module_number = ?1 WHERE module_id = ?2", nativeQuery = true)
    int updateModuleNumber(long moduleNumber, long moduleId);
}
