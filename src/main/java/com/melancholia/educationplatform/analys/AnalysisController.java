package com.melancholia.educationplatform.analys;

import com.melancholia.educationplatform.course.step.SolutionService;
import com.melancholia.educationplatform.user.User;
import com.melancholia.educationplatform.user.UserRepository;
import com.melancholia.educationplatform.user.groups.UniversityGroupService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@PostAuthorize("hasRole('ROLE_ADMIN')")
public class AnalysisController {

    private final UniversityGroupService universityGroupService;
    private final UserRepository userRepository;
    private final AnalysisService analysisService;

    @GetMapping("/analysis")
    public String viewAnalysis(Model model) {
        AnalysisParameters analysisParameters = new AnalysisParameters(List.of("По преподавателям", "По группам"),
                userRepository.findByIsTeacherTrue(),
                universityGroupService.getAllGroups());
        model.addAttribute("parameters", analysisParameters);
        return "admin/analysis";
    }

    @PostMapping("/analysis")
    public String getAnalysis(AnalysisParameters analysisParameters, Model model) {
        if (!AnalysisParameters.validate(analysisParameters)) return "redirect:/analysis";

        AnalysisResult analysisResult;

        if (analysisParameters.getOptions().get(0).equals("По преподавателям")){
            analysisResult = analysisService.analysisByTeacher(analysisParameters.getTeachers());
        } else {
            analysisResult = analysisService.analysisByGroups(analysisParameters.getGroups());
        }
        model.addAttribute("result", analysisResult);

        return "admin/analysis-results";
    }


}
