package com.melancholia.educationplatform.analys;

import com.melancholia.educationplatform.user.User;
import com.melancholia.educationplatform.user.groups.UniversityGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisParameters {
    private List<String> options;
    private List<User> teachers;
    private List<UniversityGroup> groups;

    public static boolean validate(AnalysisParameters analysisParameters){
        if (analysisParameters.options.get(0).equals("По группам") && analysisParameters.teachers.isEmpty()) return false;
        if (analysisParameters.options.get(0).equals("По преподавателям") && analysisParameters.groups.isEmpty()) return false;
        return true;
    }

}
