package com.melancholia.educationplatform.course.step.test;

import com.melancholia.educationplatform.course.step.answer.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsoleTestRepository extends JpaRepository<ConsoleTest, Long> {

    List<ConsoleTest> findConsoleTestByConsoleStepId(long consoleStepId);


}
