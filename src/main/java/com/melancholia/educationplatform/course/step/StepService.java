package com.melancholia.educationplatform.course.step;

import com.melancholia.educationplatform.core.exception.ModuleNotFoundException;
import com.melancholia.educationplatform.core.exception.StepNotFoundException;
import com.melancholia.educationplatform.course.module.Module;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StepService {

    private final StepRepository stepRepository;


    public void stepSave(Step step){
        stepRepository.save(step);
    }

    @PostAuthorize("hasPermission(#id, 'Step', 'write')")
    public Step findStepToConstructById(long id){
        return findStepById(id);
    }

    @PreAuthorize("hasPermission(#id, 'Step', 'write')")
    public void deleteStepById(long id){
        stepRepository.deleteById(id);
    }


    public Step findStepById(long id){
        return stepRepository.findById(id).orElseThrow(
                () -> new StepNotFoundException(String.format("Step with ID %s not found", id))
        );
    }

    @PreAuthorize("hasPermission(#stepId, 'Step', 'write')")
    public void updateStepNumber(long stepId, long stepNumber){
        stepRepository.updateStepNumber(stepNumber, stepId);
    }

    @PreAuthorize("hasPermission(#moduleId, 'Module', 'write')")
    public List<Step> findStepsByModuleId(long moduleId){
        return stepRepository.findStepsByModuleIdOrderByStepNumber(moduleId);
    }

    public long maxStepNumberModuleId(long moduleId){
        Long value = stepRepository.maxStepNumberModuleId(moduleId);
        if (value == null) return 0;
        return value;
    }

}
