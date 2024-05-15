package com.melancholia.educationplatform.course.module;

import com.melancholia.educationplatform.course.module.Module;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModulesWrapper {
    private List<Module> modules;

    public void addModule(Module module) {
        this.modules.add(module);
    }

}