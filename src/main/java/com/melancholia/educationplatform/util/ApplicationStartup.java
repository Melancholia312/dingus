package com.melancholia.educationplatform.util;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


@Component
public class ApplicationStartup {

    @EventListener(ApplicationReadyEvent.class)
    public void start(ApplicationReadyEvent event) throws IOException, InterruptedException {


    }
}
