package com.melancholia.educationplatform.course;

import com.melancholia.educationplatform.course.module.Module;
import com.melancholia.educationplatform.course.step.answer.Answer;
import com.melancholia.educationplatform.course.step.answer.AnswerService;
import com.melancholia.educationplatform.course.step.Step;
import com.melancholia.educationplatform.course.step.TestStep;
import com.melancholia.educationplatform.course.step.test.ConsoleTest;
import com.melancholia.educationplatform.course.step.test.ConsoleTestService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ChecklistService {

    private final int REQUIRED_MODULES = 2;
    private final int REQUIRED_STEPS = 3;
    private final AnswerService answerService;
    private final CourseService courseService;

    @PreAuthorize("hasPermission(#courseId, 'Course', 'write')")
    public CheckList check(long courseId) {
        boolean modulesEnough = false;
        boolean stepsEnough = false;
        boolean trueAnswersEnough = true;

        CheckList checkList = new CheckList();
        checkList.setModulesRequired(REQUIRED_MODULES);
        checkList.setStepRequired(REQUIRED_STEPS);

        Course course = courseService.findCourseToConstructById(courseId);
        checkList.setCourseName(course.getName());
        checkList.setCourseId(course.getId());
        if (course.getModules() == null) {
            checkList.setFinalCheck(false);
            return checkList;
        }
        checkList.setModulesCount(course.getModules().size());
        if (course.getModules().size() >= REQUIRED_MODULES) modulesEnough = true;

        Map<Module, Boolean> isEnoughSteps = new HashMap<>();
        for (Module module : course.getModules()) {
            if (module.getSteps() == null || module.getSteps().size() < REQUIRED_STEPS) {
                isEnoughSteps.put(module, false);
                continue;
            }
            isEnoughSteps.put(module, true);
        }
        if (isEnoughSteps.values().stream().filter(aBoolean -> aBoolean).toList().size() == isEnoughSteps.size())
            stepsEnough = true;
        checkList.setIsEnoughSteps(isEnoughSteps);

        Map<Module, Map<Step, Integer>> moduleWithTestSteps = new HashMap<>();

        for (Module module : course.getModules()) {
            int counter = 0;
            List<Step> testSteps = module.getSteps().stream().filter(step -> step.getClass().getSimpleName().equals("TestStep")).toList();
            Map<Step, Integer> stepIntegerMap = new HashMap<>();
            for (Step step : testSteps) {
                List<Answer> answers = answerService.findAnswerByTestStepId(step.getId());
                List<Answer> correctAnswers = answers.stream().filter(Answer::isCorrect).toList();
                stepIntegerMap.put(step, correctAnswers.size());
                if (correctAnswers.isEmpty() || answers.size() < 2) continue;
                if (!((TestStep) step).isMultiAnswers()) {
                    if (correctAnswers.size() != 1) continue;
                }
                counter++;
            }
            moduleWithTestSteps.put(module, stepIntegerMap);
            if (counter != testSteps.size()) trueAnswersEnough = false;
        }
        checkList.setModuleWithTestSteps(moduleWithTestSteps);

        checkList.setFinalCheck(modulesEnough && stepsEnough && trueAnswersEnough);
        return checkList;
    }
}
