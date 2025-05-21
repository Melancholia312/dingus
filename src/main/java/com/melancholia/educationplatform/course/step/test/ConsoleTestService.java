package com.melancholia.educationplatform.course.step.test;

import com.melancholia.educationplatform.core.exception.AnswerNotFoundException;
import com.melancholia.educationplatform.core.exception.ConsoleTestNotFoundException;
import com.melancholia.educationplatform.course.step.answer.Answer;
import com.melancholia.educationplatform.course.step.answer.AnswerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ConsoleTestService {

    private final ConsoleTestRepository consoleTestRepository;

    @PreAuthorize("hasPermission(#consoleStepId, 'Step', 'write')")
    public List<ConsoleTest> findTestByConsoleStepId(long consoleStepId) {
        return consoleTestRepository.findConsoleTestByConsoleStepId(consoleStepId);
    }

    @PostAuthorize("hasPermission(#consoleTestId, 'ConsoleTest', 'write')")
    public ConsoleTest findConsoleTestToConstructById(long consoleTestId) {
        return findConsoleTestById(consoleTestId);
    }

    @PreAuthorize("hasPermission(#consoleTestId, 'ConsoleTest', 'write')")
    public void deleteConsoleTestById(long consoleTestId) {
        consoleTestRepository.deleteById(consoleTestId);
    }

    public ConsoleTest findConsoleTestById(long id) {
        return consoleTestRepository.findById(id).orElseThrow(
                () -> new ConsoleTestNotFoundException(String.format("Test with ID %s not found", id))
        );
    }

    public void consoleTestSave(ConsoleTest consoleTest) {
        consoleTestRepository.save(consoleTest);
    }
}
