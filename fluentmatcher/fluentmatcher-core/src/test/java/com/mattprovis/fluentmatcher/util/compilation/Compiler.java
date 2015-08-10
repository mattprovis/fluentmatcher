package com.mattprovis.fluentmatcher.util.compilation;

import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class Compiler {

    public static Class<?> compileClassFromSource(String javaSource, String className) throws Exception {
        return compileClass(className, new MemorySource(className, javaSource));
    }

    private static Class<?> compileClass(String className, MemorySource compilationUnit) throws ClassNotFoundException {
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

        SpecialClassLoader specialClassLoader = new SpecialClassLoader();

        SpecialJavaFileManager fileManager = new SpecialJavaFileManager(
                javac.getStandardFileManager(null, null, null),
                specialClassLoader);

        List<String> options = Collections.emptyList();

        DiagnosticListener<? super JavaFileObject> diagnosticListener = null;
        JavaCompiler.CompilationTask compile = javac.getTask(new PrintWriter(System.err), fileManager, diagnosticListener, options, null, asList(compilationUnit));
        Boolean result = compile.call();
        if (!result) {
            throw new ClassNotFoundException("Failed to compile: " + className);
        }
        return specialClassLoader.findClass(className);
    }
}
