package com.melancholia.educationplatform.course.step;

import com.melancholia.educationplatform.course.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {
    List<Solution> findByUserIdAndStepId(long userId, long stepId);
}
