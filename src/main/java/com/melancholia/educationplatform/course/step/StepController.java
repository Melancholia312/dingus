package com.melancholia.educationplatform.course.step;

import com.melancholia.educationplatform.course.Course;
import com.melancholia.educationplatform.course.CourseService;
import com.melancholia.educationplatform.course.module.Module;
import com.melancholia.educationplatform.course.module.ModuleService;
import com.melancholia.educationplatform.course.module.ModulesWrapper;
import com.melancholia.educationplatform.user.User;
import com.melancholia.educationplatform.user.permissions.PrivilegeService;
import com.melancholia.educationplatform.util.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Controller("/step")
@AllArgsConstructor
public class StepController {
    public static String UPLOAD_PATH = System.getProperty("user.dir") + "/uploads/";
    private final CourseService courseService;
    private final ModuleService moduleService;
    private final StepService stepService;

    private final AnswerService answerService;

    private final PrivilegeService privilegeService;
    private final SolutionService solutionService;

    @GetMapping("/step/add")
    public String addModule(@RequestParam(name = "moduleId") long moduleId,
                            @RequestParam(name = "type") String type,
                            Model model) {
        Module module = moduleService.findModuleToConstructById(moduleId);
        if (module.getCourse().isPublished()){
            return "redirect:/";
        }
        model.addAttribute("module", module);
        Step step;
        switch (type) {
            case "word-answer-step" -> {
                step = new WordAnswerStep();
                model.addAttribute("step", step);
                return "step/add-word-answer-step";
            }
            case "test-step" -> {
                step = new TestStep();
                model.addAttribute("step", step);
                return "step/add-test-step";
            }
            default -> {
                step = new InformationTextStep();
                model.addAttribute("step", step);
                return "step/add-lection-step";
            }
        }
    }

    @PostMapping("/step/add-lection")
    public String createLectionStep(@RequestParam(name = "moduleId") long moduleId,
                                    @Valid @ModelAttribute("step") InformationTextStep step, BindingResult bindingResult, Model model,
                                    @RequestParam("file") MultipartFile file,
                                    Authentication authentication) throws IOException {
        Module module = moduleService.findModuleToConstructById(moduleId);
        if (module.getCourse().isPublished()){
            return "redirect:/";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("step", step);
            if (step.getId() == 0){
                model.addAttribute("module", module);
                return "step/add-lection-step";
            }
            return "step/edit-lection-step";
        }
        step.setModule(module);

        if (step.getId() == 0) step.setStepNumber(stepService.maxStepNumberModuleId(moduleId) + 1);

        if (!file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            step.setVideo(fileName);
            String uploadDir = "uploads";
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        }

        stepService.stepSave(step);
        privilegeService.addPermissionToUser(
                authentication,
                Step.class.getSimpleName(),
                String.valueOf(step.getId()),
                "write");

        return String.format("redirect:/module/%s/steps", step.getModule().getId());
    }

    @PostMapping("/step/add-word-answer")
    public String createWordAnswerStep(@RequestParam(name = "moduleId") long moduleId,
                                       @Valid @ModelAttribute("step") WordAnswerStep step, BindingResult bindingResult, Model model,
                                       @RequestParam("file") MultipartFile file,
                                       Authentication authentication) throws IOException {
        Module module = moduleService.findModuleToConstructById(moduleId);
        if (module.getCourse().isPublished()){
            return "redirect:/";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("step", step);
            if (step.getId() == 0) {
                model.addAttribute("module", module);
                return "step/add-word-answer-step";
            }
            return "step/edit-word-answer-step";
        }
        step.setModule(module);

        if (step.getId() == 0) step.setStepNumber(stepService.maxStepNumberModuleId(moduleId) + 1);

        if (!file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            step.setImage(fileName);
            String uploadDir = "uploads";
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        }

        stepService.stepSave(step);
        privilegeService.addPermissionToUser(
                authentication,
                Step.class.getSimpleName(),
                String.valueOf(step.getId()),
                "write");
        return String.format("redirect:/module/%s/steps", step.getModule().getId());
    }

    @PostMapping("/step/add-test")
    public String createTestStep(@RequestParam(name = "moduleId") long moduleId,
                                 @Valid @ModelAttribute("step") TestStep step, BindingResult bindingResult, Model model,
                                 @RequestParam("file") MultipartFile file,
                                 Authentication authentication) throws IOException {
        Module module = moduleService.findModuleToConstructById(moduleId);
        if (module.getCourse().isPublished()){
            return "redirect:/";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("step", step);
            if (step.getId() == 0) {
                model.addAttribute("module", module);
                return "step/add-test-step";
            }
            return "step/edit-test-step";
        }
        step.setModule(module);

        if (step.getId() == 0) step.setStepNumber(stepService.maxStepNumberModuleId(moduleId) + 1);

        if (!file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            step.setImage(fileName);
            String uploadDir = "uploads";
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        }

        stepService.stepSave(step);
        privilegeService.addPermissionToUser(
                authentication,
                Step.class.getSimpleName(),
                String.valueOf(step.getId()),
                "write");
        return String.format("redirect:/module/%s/steps", step.getModule().getId());
    }

    @GetMapping("/module/{id}/steps")
    public String viewMySteps(@PathVariable("id") long id,
                              Model model) {
        Module module =  moduleService.findModuleToConstructById(id);
        if (module.getCourse().isPublished()){
            return "redirect:/";
        }
        model.addAttribute("module", module);
        StepsWrapper stepsWrapper = new StepsWrapper(stepService.findStepsByModuleId(id));
        model.addAttribute("steps", stepsWrapper);
        return "step/all-steps";
    }

    @GetMapping("/step/{id}/edit")
    public String editStep(@PathVariable("id") long id, Model model,
                           @RequestParam(name = "type") String type) {
        Step step = stepService.findStepToConstructById(id);
        if (step.getModule().getCourse().isPublished()){
            return "redirect:/";
        }
        model.addAttribute("step", step);
        switch (type) {
            case "WordAnswerStep" -> {
                return "step/edit-word-answer-step";
            }
            case "TestStep" -> {
                return "step/edit-test-step";
            }
            default -> {
                return "step/edit-lection-step";
            }
        }

    }

    @GetMapping("/step/{id}/delete")
    public String deleteStepForm(@PathVariable("id") long id, Model model) {
        Step step = stepService.findStepToConstructById(id);
        if (step.getModule().getCourse().isPublished()){
            return "redirect:/";
        }
        model.addAttribute("step", step);
        return "step/delete";
    }

    @PostMapping("/step/{id}/delete")
    public String deleteStep(@PathVariable("id") long id,
                             @RequestParam(name = "moduleId") long moduleId,
                             Authentication authentication) {
        Module module = moduleService.findModuleToConstructById(moduleId);
        if (module.getCourse().isPublished()){
            return "redirect:/";
        }
        stepService.deleteStepById(id);
        courseService.deleteStepPermissions(((User) authentication.getPrincipal()).getId(), id);
        return String.format("redirect:/module/%s/steps", moduleId);
    }

    @PostMapping("/steps/switch")
    public String switchSteps(@RequestParam(name = "moduleId") long moduleId,
                              @ModelAttribute StepsWrapper steps) {
        Module module = moduleService.findModuleToConstructById(moduleId);
        if (module.getCourse().isPublished()){
            return "redirect:/";
        }
        for (Step step : steps.getSteps()) {
            stepService.updateStepNumber(step.getId(), step.getStepNumber());
        }
        return String.format("redirect:/module/%s/steps", moduleId);
    }

    @GetMapping("/test-step/{id}/answers")
    public String getTestStepAnswers(@PathVariable("id") long id,
                                     Model model) {
        Step step = stepService.findStepToConstructById(id);
        if (step.getModule().getCourse().isPublished()){
            return "redirect:/";
        }
        model.addAttribute("step", step);
        model.addAttribute("answers", answerService.findAnswerByTestStepId(id));
        return "step/all-answers";
    }

    @GetMapping("/test-step/answer/add")
    public String addTestStepAnswer(@RequestParam(name = "testStepId") long testStepId,
                                    Model model) {
        Step step = stepService.findStepToConstructById(testStepId);
        if (step.getModule().getCourse().isPublished()){
            return "redirect:/";
        }
        model.addAttribute("step", step);
        model.addAttribute("answer", new Answer());
        return "step/add-answer";
    }


    @PostMapping("/test-step/answer/add")
    public String createTestStepAnswer(@RequestParam(name = "testStepId") long stepId,
                                       @Valid Answer answer, BindingResult bindingResult, Model  model,
                                       @RequestParam("file") MultipartFile file,
                                       Authentication authentication) throws IOException {
        Step testStep = stepService.findStepToConstructById(stepId);
        if (testStep.getModule().getCourse().isPublished()){
            return "redirect:/";
        }
        if (bindingResult.hasErrors()){
            model.addAttribute("step", testStep);
            model.addAttribute("answer", answer);
            if (answer.getId() == 0) return "step/add-answer";
            return "step/edit-answer";
        }
        answer.setTestStep(testStep);
        if (!file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            answer.setImage(fileName);
            String uploadDir = "uploads";
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        }

        answerService.answerSave(answer);
        privilegeService.addPermissionToUser(
                authentication,
                Answer.class.getSimpleName(),
                String.valueOf(answer.getId()),
                "write");
        return String.format("redirect:/test-step/%s/answers", stepId);
    }

    @GetMapping("test-step/answer/edit")
    public String editModule(@RequestParam(name = "answerId") long answerId,
                             @RequestParam(name = "stepId") long stepId,
                             Model model) {
        Step step = stepService.findStepToConstructById(stepId);
        if (step.getModule().getCourse().isPublished()){
            return "redirect:/";
        }
        model.addAttribute("step", step);
        model.addAttribute("answer", answerService.findAnswerToConstructById(answerId));
        return "step/edit-answer";
    }

    @GetMapping("test-step/answer/delete")
    public String deleteAnswerForm(@RequestParam(name = "answerId") long answerId,
                                   @RequestParam(name = "stepId") long stepId,
                                   Model model) {
        Step step = stepService.findStepToConstructById(stepId);
        if (step.getModule().getCourse().isPublished()){
            return "redirect:/";
        }
        model.addAttribute("step", step);
        model.addAttribute("answer", answerService.findAnswerToConstructById(answerId));
        return "step/delete-answer";
    }

    @PostMapping("test-step/answer/delete")
    public String deleteAnswer(@RequestParam(name = "answerId") long answerId,
                               @RequestParam(name = "stepId") long stepId,
                               Authentication authentication) {
        Step step = stepService.findStepToConstructById(stepId);
        if (step.getModule().getCourse().isPublished()){
            return "redirect:/";
        }
        answerService.deleteAnswerById(answerId);
        courseService.deleteAnswerPermissions(((User) authentication.getPrincipal()).getId(), answerId);
        return String.format("redirect:/test-step/%s/answers", stepId);
    }

    @PostMapping("/word-answer/{id}/check")
    public String checkWordAnswer(@PathVariable(name = "id") long id,
                                  @ModelAttribute WordAnswerStep step,
                                  @RequestParam(name = "moduleId") long moduleId,
                                  @RequestParam(name = "courseId") long courseId,
                                  Authentication authentication) {
        WordAnswerStep stepFromBd = ((WordAnswerStep) stepService.findStepById(id));
        Solution solution = new Solution();
        solution.setSolutionDate(new Date());
        solution.setStep(stepFromBd);
        solution.setSolvedCorrect(step.getAnswer().equals(stepFromBd.getAnswer()));
        solution.setUser((User) authentication.getPrincipal());
        String solutionText = (step.getAnswer() == null || step.getAnswer().isEmpty()) ? "Ответ не был выбран" : step.getAnswer();
        solution.setSolutionText(solutionText);
        solutionService.solutionSave(solution);
        return String.format("redirect:/course/%s/passing?moduleId=%s&stepId=%s", courseId, moduleId, id);
    }

    @PostMapping("/test-answer/{id}/check")
    public String checkTestStepAnswer(@PathVariable(name = "id") long id,
                                      @ModelAttribute TestStep step,
                                      @RequestParam(name = "moduleId") long moduleId,
                                      @RequestParam(name = "courseId") long courseId,
                                      Authentication authentication) {
        Answer selectedAnswer = step.getAnswers()
                .stream()
                .filter(answer -> answer.getAnswerText() != null)
                .findAny()
                .orElse(null);
        boolean isCorrect = false;
        Solution solution = new Solution();
        solution.setSolutionText("Ответ не был выбран");
        if (selectedAnswer != null) {
            Answer answerFromBd = answerService.findAnswerById(selectedAnswer.getId());
            isCorrect = answerFromBd.isCorrect();
            solution.setSolutionText(selectedAnswer.getAnswerText());
        }

        TestStep stepFromBd = ((TestStep) stepService.findStepById(id));
        solution.setSolutionDate(new Date());
        solution.setStep(stepFromBd);
        solution.setSolvedCorrect(isCorrect);
        solution.setUser((User) authentication.getPrincipal());
        solutionService.solutionSave(solution);
        return String.format("redirect:/course/%s/passing?moduleId=%s&stepId=%s", courseId, moduleId, id);
    }

    @PostMapping("/test-answer-multiple/{id}/check")
    public String checkMultiTestStepAnswer(@PathVariable(name = "id") long id,
                                           @ModelAttribute TestStep step,
                                           @RequestParam(name = "moduleId") long moduleId,
                                           @RequestParam(name = "courseId") long courseId,
                                           Authentication authentication) {
        List<Long> selectedAnswers = step.getAnswers()
                .stream()
                .filter(answer -> answer.getAnswerText() != null)
                .map(Answer::getId)
                .toList();
        List<Long> correctAnswers = answerService.findCorrectAnswersByStepId(id)
                .stream()
                .map(Answer::getId)
                .toList();

        TestStep stepFromBd = ((TestStep) stepService.findStepById(id));
        Solution solution = new Solution();
        solution.setSolutionDate(new Date());
        solution.setStep(stepFromBd);
        solution.setSolvedCorrect(new HashSet<>(selectedAnswers).equals(new HashSet<>(correctAnswers)));
        List<String> answerTexts = step.getAnswers()
                .stream()
                .filter(answer -> answer.getAnswerText() != null)
                .map(Answer::getAnswerText)
                .toList();
        if (answerTexts.isEmpty()) answerTexts = List.of("Ответ не был выбран");
        solution.setSolutionText(String.join(", ", answerTexts));
        solution.setUser(((User) authentication.getPrincipal()));
        solutionService.solutionSave(solution);
        return String.format("redirect:/course/%s/passing?moduleId=%s&stepId=%s", courseId, moduleId, id);
    }

}
