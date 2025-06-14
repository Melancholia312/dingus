package com.melancholia.educationplatform.course.step.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findAnswerByTestStepId(long testStepId);

    List<Answer> findByTestStepIdAndCorrectTrue(long id);

}
