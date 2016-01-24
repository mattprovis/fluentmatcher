package com.mattprovis.fluentmatcher.integrationtest;

import com.mattprovis.fluentmatcher.FluentMatcher;
import com.mattprovis.fluentmatcher.FluentMatcherGenerator;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import static com.mattprovis.fluentmatcher.util.compilation.Compiler.compileClassFromSource;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

public final class GeneratedMatcherTestUtils {

    private GeneratedMatcherTestUtils() {
    }

    public static FluentMatcher createMatcherFor(Class<?> pojoClass) throws Exception {
        FluentMatcherGenerator fluentMatcherGenerator = new FluentMatcherGenerator(pojoClass);
        String matcherSourceFileContents = generateMatcher(fluentMatcherGenerator);
        String matcherClassName = fluentMatcherGenerator.getFullMatcherClassName();
        Class<?> compiledClass = compileClassFromSource(matcherSourceFileContents, matcherClassName);
        return instantiateUsingStaticFactoryMethod(pojoClass, compiledClass);
    }

    private static String generateMatcher(FluentMatcherGenerator fluentMatcherGenerator) throws IOException {
        StringWriter stringWriter = new StringWriter();
        fluentMatcherGenerator.generateMatcher(stringWriter);
        return stringWriter.toString();
    }

    private static FluentMatcher instantiateUsingStaticFactoryMethod(Class<?> pojoClass, Class<?> compiledClass) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String matcherStaticFactoryMethod = uncapitalize(pojoClass.getSimpleName());
        return (FluentMatcher) compiledClass.getMethod(matcherStaticFactoryMethod).invoke(null);
    }
}
