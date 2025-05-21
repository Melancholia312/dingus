package com.melancholia.educationplatform.course.step.answer;

import com.melancholia.educationplatform.core.exception.AnswerNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    @PreAuthorize("hasPermission(#testStepId, 'Step', 'write')")
    public List<Answer> findAnswerByTestStepId(long testStepId) {
        return answerRepository.findAnswerByTestStepId(testStepId);
    }

    @PostAuthorize("hasPermission(#answerId, 'Answer', 'write')")
    public Answer findAnswerToConstructById(long answerId) {
        return findAnswerById(answerId);
    }

    @PreAuthorize("hasPermission(#answerId, 'Answer', 'write')")
    public void deleteAnswerById(long answerId) {
        answerRepository.deleteById(answerId);
    }

    public Answer findAnswerById(long id) {
        return answerRepository.findById(id).orElseThrow(
                () -> new AnswerNotFoundException(String.format("Answer with ID %s not found", id))
        );
    }

    public List<Answer> findCorrectAnswersByStepId(long id) {
        return answerRepository.findByTestStepIdAndCorrectTrue(id);
    }

    public void answerSave(Answer answer) {
        answerRepository.save(answer);
    }
}
