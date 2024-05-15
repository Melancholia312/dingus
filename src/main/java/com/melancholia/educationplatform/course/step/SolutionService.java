package com.melancholia.educationplatform.course.step;

import com.melancholia.educationplatform.course.step.comment.Comment;
import com.melancholia.educationplatform.course.step.comment.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SolutionService {

    private final SolutionRepository solutionRepository;

    public void solutionSave(Solution solution){
        solutionRepository.save(solution);
    }

    public List<Solution> getUserSolutions(long userId, long stepId){
        return solutionRepository.findByUserIdAndStepId(userId, stepId);
    }

    public List<Solution> getAllSolutions(){
        return solutionRepository.findAll();
    }
}
