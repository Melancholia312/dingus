package com.melancholia.educationplatform.analys;

import com.melancholia.educationplatform.user.User;
import com.melancholia.educationplatform.user.groups.UniversityGroup;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class AnalysisResult {

    private Object bestObject;
    private Object worstObject;
    private int studentsCount;
    private double averageMark;
    private Object marksPerObject;
    private boolean option;
    private Map<String, Double> marksPerObjectFormated;

    public void formatMap(){
        if (option){
            Map<UniversityGroup, Double> map = ((Map<UniversityGroup, Double>) marksPerObject);
            Map<String, Double> newMap = new HashMap<>();
            for (var entry : map.entrySet()) {
                newMap.put(String.format("%s-%s-%s%s", entry.getKey().getSubGroup(), entry.getKey().getGroupName(),
                        entry.getKey().getCourseNumber(), entry.getKey().getGroupNumber()), entry.getValue()*100);
            }
            marksPerObjectFormated = newMap;
        } else {
            Map<User, Double> map = ((Map<User, Double>) marksPerObject);
            Map<String, Double> newMap = new HashMap<>();
            for (var entry : map.entrySet()) {
                List<String> name = Arrays.stream(entry.getKey().getUsername().split(" ")).toList();
                newMap.put(String.format("%s %s.%s.", name.get(0), name.get(1).charAt(0), name.get(2).charAt(0)), entry.getValue()*100);
            }
            marksPerObjectFormated = newMap;
        }
    }

}
