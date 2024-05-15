package com.melancholia.educationplatform.course.step;

import com.melancholia.educationplatform.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StepRepository extends JpaRepository<Step, Long> {

    List<Step> findStepsByModuleIdOrderByStepNumber(long moduleId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE step SET step_number = ?1 WHERE step_id = ?2", nativeQuery = true)
    int updateStepNumber(long stepNumber, long stepId);

    @Query(value = "SELECT MAX(step_number) from Step WHERE module_id = ?1", nativeQuery = true)
    Long maxStepNumberModuleId(long moduleId);

}
