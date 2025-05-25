package com.melancholia.educationplatform.course.analyzer;

import com.puppycrawl.tools.checkstyle.*;
import com.puppycrawl.tools.checkstyle.api.*;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;
import org.springframework.stereotype.Component;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CheckstyleCodeAnalyzer {

    private final Configuration config;

    public CheckstyleCodeAnalyzer() throws Exception {
        this.config = ConfigurationLoader.loadConfiguration(
                new File("src/main/resources/checkstyle.xml").toURI().toURL().toString(),
                new PropertiesExpander(System.getProperties()),
                ConfigurationLoader.IgnoredModulesOptions.OMIT
        );
    }

    public List<String> analyze(String code) {
        List<String> results = new ArrayList<>();

        List<String> compilationIssues = checkCompilation(code);
        results.addAll(compilationIssues);

        if (!compilationIssues.isEmpty()) {
            return results;
        }

        try {
            AuditListener listener = new AuditListener() {
                @Override
                public void addError(AuditEvent event) {
                    results.add(String.format("Линия %d: %s (%s)",
                            event.getLine(),
                            event.getMessage(),
                            event.getSourceName()));
                }

                @Override
                public void addException(AuditEvent event, Throwable throwable) {
                    results.add("ОШИБКА: " + throwable.getMessage());
                }

                // Остальные методы интерфейса остаются пустыми
                @Override
                public void auditStarted(AuditEvent event) {
                }

                @Override
                public void auditFinished(AuditEvent event) {
                }

                @Override
                public void fileStarted(AuditEvent event) {
                }

                @Override
                public void fileFinished(AuditEvent event) {
                }
            };

            Path tempFile = Files.createTempFile("checkstyle_", ".java");
            Files.write(tempFile, code.getBytes(StandardCharsets.UTF_8));

            Checker checker = new Checker();
            checker.setModuleClassLoader(Checker.class.getClassLoader());
            checker.configure(config);
            checker.addListener(listener);

            checker.process(Collections.singletonList(tempFile.toFile()));
            checker.destroy();

        } catch (Exception e) {
            results.add("КРИТИЧЕСКАЯ ОШИБКА: " + e.getMessage());
            e.printStackTrace();
        }

        return results.stream()
                .map(issue -> issue.replace("(com.puppycrawl.tools.checkstyle.checks.", "(")
                        .replace("Check", ""))
                .collect(Collectors.toList());
    }

    private List<String> checkCompilation(String code) {
        List<String> issues = new ArrayList<>();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            issues.add("«ОШИБКА: Компилятор Java недоступен (вы работаете с JDK?)»");
            return issues;
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        try {
            JavaFileObject file = new SimpleJavaFileObject(
                    URI.create("string:///Main.java"),
                    JavaFileObject.Kind.SOURCE) {
                @Override
                public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                    return code;
                }
            };

            JavaCompiler.CompilationTask task = compiler.getTask(
                    null, fileManager, diagnostics, null, null, Collections.singletonList(file));

            boolean success = task.call();

            if (!success) {
                issues.addAll(diagnostics.getDiagnostics().stream()
                        .map(d -> String.format("Линия %d: %s",
                                d.getLineNumber(),
                                d.getMessage(null)))
                        .collect(Collectors.toList()));
            }
        } catch (Exception e) {
            issues.add("ОШИБКА во время проверки компиляции: " + e.getMessage());
        } finally {
            try {
                fileManager.close();
            } catch (IOException e) {
                issues.add("ВНИМАНИЕ: не удалось закрыть файловый менеджер: " + e.getMessage());
            }
        }

        return issues;
    }
}