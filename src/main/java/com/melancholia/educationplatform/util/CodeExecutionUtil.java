package com.melancholia.educationplatform.util;

import com.melancholia.educationplatform.course.step.test.ConsoleTest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class CodeExecutionUtil {

    private static final int EXECUTION_TIMEOUT = 5;
    private static final String MAIN_CLASS_NAME = "Main";

    public static class ExecutionResult {
        private final boolean success;
        private final String output;
        private final String error;

        public ExecutionResult(boolean success, String output, String error) {
            this.success = success;
            this.output = output;
            this.error = error;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getOutput() {
            return output;
        }

        public String getError() {
            return error;
        }

        @Override
        public String toString() {
            return output + "\n" + error;
        }
    }

    public static boolean isCodeValid(String code) {
        String[] forbiddenPatterns = {
                "Runtime.getRuntime()",
                "ProcessBuilder",
                "System.exit",
                "File",
                "java.nio",
                "exec(",
                "fork()",
                "Process",
                "Thread",
                "Reflection",
                "JNI",
                "Class.forName",
                "ClassLoader"
        };

        for (String pattern : forbiddenPatterns) {
            if (code.contains(pattern)) {
                return false;
            }
        }
        return true;
    }

    public static ExecutionResult executeWithTests(String code, List<ConsoleTest> testCases) {
        if (!isCodeValid(code)) {
            return new ExecutionResult(false, "", "Код содержит запрещенные конструкции");
        }

        Path tempDir = Paths.get("temp_" + UUID.randomUUID());
        try {
            Files.createDirectory(tempDir);
            Path javaFile = tempDir.resolve(MAIN_CLASS_NAME + ".java");
            Files.write(javaFile, code.getBytes());

            // Компиляция
            String compileError = compileJavaFile(javaFile);
            if (!compileError.isEmpty()) {
                return new ExecutionResult(false, "", "Ошибка компиляции:\n" + compileError);
            }

            // Выполнение тестов
            List<String> testResults = new ArrayList<>();
            boolean allTestsPassed = true;

            for (ConsoleTest testCase : testCases) {
                ExecutionResult result = executeSingleTest(tempDir, testCase);
                if (!result.isSuccess()) {
                    allTestsPassed = false;
                }
                testResults.add(formatTestResult(testCase, result));
            }

            return new ExecutionResult(
                    allTestsPassed,
                    String.join("\n\n", testResults),
                    allTestsPassed ? "" : "Некоторые тесты не пройдены"
            );

        } catch (Exception e) {
            return new ExecutionResult(false, "", "Ошибка выполнения: " + e.getMessage());
        } finally {
            deleteDirectory(tempDir.toFile());
        }
    }

    private static String compileJavaFile(Path javaFile) throws IOException, InterruptedException {
        ProcessBuilder compileProcessBuilder = new ProcessBuilder(
                "javac",
                "-Xlint:all",
                javaFile.toString()
        );

        compileProcessBuilder.redirectErrorStream(true);
        Process compileProcess = compileProcessBuilder.start();

        String compileOutput = readProcessOutput(compileProcess);
        compileProcess.waitFor();

        return compileProcess.exitValue() == 0 ? "" : compileOutput;
    }

    private static ExecutionResult executeSingleTest(Path tempDir, ConsoleTest testCase)
            throws IOException, InterruptedException {

        // Преобразуем входные данные в аргументы командной строки
        String[] args = testCase.getInput().split("\\s+");

        ProcessBuilder runProcessBuilder = new ProcessBuilder(
                "java",
                "-cp",
                tempDir.toString(),
                MAIN_CLASS_NAME
        );

        // Добавляем аргументы
        for (String arg : args) {
            runProcessBuilder.command().add(arg);
        }

        runProcessBuilder.redirectErrorStream(true);
        Process runProcess = runProcessBuilder.start();

        // Чтение вывода с таймаутом
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> readProcessOutput(runProcess));

        try {
            String output = future.get(EXECUTION_TIMEOUT, TimeUnit.SECONDS);
            boolean success = normalizeOutput(output).equals(normalizeOutput(testCase.getOutput()));
            return new ExecutionResult(success, output, success ? "" : "Неверный вывод");
        } catch (TimeoutException e) {
            runProcess.destroyForcibly();
            return new ExecutionResult(false, "", "Превышено время выполнения");
        } catch (ExecutionException e) {
            return new ExecutionResult(false, "", "Ошибка выполнения: " + e.getCause().getMessage());
        } finally {
            executor.shutdownNow();
            runProcess.destroy();
        }
    }

    private static String readProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString().trim();
    }

    private static String normalizeOutput(String output) {
        return output.replace("\r", "").trim();
    }

    private static String formatTestResult(ConsoleTest testCase, ExecutionResult result) {
        return String.format(
                "Входные данные: %s\nОжидаемый вывод: %s\nФактический вывод: %s\nРезультат: %s",
                testCase.getInput(),
                testCase.getOutput(),
                result.getOutput(),
                result.isSuccess() ? "✅ Успех" : "❌ Ошибка: " + result.getError()
        );
    }

    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
}