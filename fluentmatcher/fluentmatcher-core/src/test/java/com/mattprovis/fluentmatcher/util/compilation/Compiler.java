package com.mattprovis.fluentmatcher.util.compilation;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.PrintWriter;

import static java.util.Arrays.asList;

public class Compiler {

    private static final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

    public static Class<?> compileClassFromSource(String javaSource, String className) throws Exception {

        MemoryBackedJavaFileManager fileManager = new MemoryBackedJavaFileManager(
                javac.getStandardFileManager(null, null, null));

        SourceCode sourceCode = new SourceCode(className, javaSource);
        boolean result = javac.getTask(new PrintWriter(System.err), fileManager, null, null, null, asList(sourceCode)).call();
        if (!result) {
            throw new ClassNotFoundException("Failed to compile: " + className);
        }
        return fileManager.getClassLoader(null).loadClass(className);
    }

}
