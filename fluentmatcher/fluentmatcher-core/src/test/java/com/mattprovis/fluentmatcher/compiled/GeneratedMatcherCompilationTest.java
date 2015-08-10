package com.mattprovis.fluentmatcher.compiled;

import com.mattprovis.fluentmatcher.FluentMatcher;
import com.mattprovis.fluentmatcher.FluentMatcherGenerator;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import static com.mattprovis.fluentmatcher.util.compilation.Compiler.compileClassFromSource;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GeneratedMatcherCompilationTest {

    @Test
    public void canCompileAndExecuteGeneratedFluentMatcher() throws Exception {
        FluentMatcher matcherInstance = createMatcherFor(Example.class);

        Example samplePojoInstance = new Example("hello");
        assertThat(matcherInstance.matches(samplePojoInstance), is(true));
    }

    @Test
    public void canCorrectlyFailAMatch() throws Exception {
        FluentMatcher matcherInstance = createMatcherFor(Example.class);

        matcherInstance.getClass().getMethod("withMessage", String.class).invoke(matcherInstance, "goodbye");

        Example samplePojoInstance = new Example("hello");
        assertThat(matcherInstance.matches(samplePojoInstance), is(false));
    }

    private FluentMatcher createMatcherFor(Class<Example> pojoClass) throws Exception {
        String fluentMatcherSourceFile = generateMatcherClassFor(pojoClass);
        Class<?> compiledClass = compileMatcher(fluentMatcherSourceFile, pojoClass);
        return instantiateUsingStaticFactoryMethod(pojoClass, compiledClass);
    }

    private String generateMatcherClassFor(Class<?> pojoClass) throws IOException {
        StringWriter stringWriter = new StringWriter();
        new FluentMatcherGenerator(pojoClass, stringWriter).generateMatcher();
        return stringWriter.toString();
    }

    private Class<?> compileMatcher(String fluentMatcherSourceFile, Class<?> pojoClass) throws Exception {
        String matcherClassName = pojoClass.getName() + "Matcher";
        return compileClassFromSource(fluentMatcherSourceFile, matcherClassName);
    }

    private FluentMatcher instantiateUsingStaticFactoryMethod(Class<?> pojoClass, Class<?> compiledClass) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String matcherStaticFactoryMethod = uncapitalize(pojoClass.getSimpleName());
        return (FluentMatcher) compiledClass.getMethod(matcherStaticFactoryMethod).invoke(null);
    }

}
