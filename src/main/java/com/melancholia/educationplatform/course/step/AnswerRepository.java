package com.melancholia.educationplatform.course.step;

import com.melancholia.educationplatform.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    public List<Answer> findAnswerByTestStepId(long testStepId);

    List<Answer> findByTestStepIdAndCorrectTrue(long id);

}
