package com.melancholia.educationplatform.user.groups;

import com.melancholia.educationplatform.core.exception.GroupNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UniversityGroupService {

    private final UniversityGroupRepository universityGroupRepository;

    public List<UniversityGroup> getAllGroups(){
        return universityGroupRepository.findAll();
    }

    public void deleteGroupById(long groupId){
        universityGroupRepository.deleteById(groupId);
    }

    public UniversityGroup findGroupById(long groupId){
        return universityGroupRepository.findById(groupId).orElseThrow(
                () -> new GroupNotFoundException(String.format("Group with ID %s not found", groupId)));
    }

    public void groupSave(UniversityGroup group){
        universityGroupRepository.save(group);
    }

}
