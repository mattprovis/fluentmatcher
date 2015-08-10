package com.mattprovis.fluentmatcher;

import org.junit.Test;

import java.io.StringWriter;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class FluentMatcherGeneratorTest {

    private static class ExampleWithArray {
        int[] intArray;
    }

    private static class ExampleWithPrimitive {
        int anInt;
    }

    private static class ExampleWithList {
        List<String> stringsList;
    }

    private static class ExampleWithBoolean {
        boolean okay;
        Boolean good;
    }

    private StringWriter stringWriter = new StringWriter();

    @Test
    public void shouldSupportPrimitives() throws Exception {
        new FluentMatcherGenerator(ExampleWithPrimitive.class, stringWriter).generateMatcher();
        assertThat(stringWriter.toString(), containsString("public ExampleWithPrimitiveMatcher withAnInt(Integer expectedValue)"));
        assertThat(stringWriter.toString(), containsString("public ExampleWithPrimitiveMatcher withAnInt(Matcher<? super Integer> matcher)"));
    }

    @Test
    public void shouldSupportArrays() throws Exception {
        new FluentMatcherGenerator(ExampleWithArray.class, stringWriter).generateMatcher();
        assertThat(stringWriter.toString(), containsString("public ExampleWithArrayMatcher withIntArray(int[] expectedValue)"));
        assertThat(stringWriter.toString(), containsString("public ExampleWithArrayMatcher withIntArray(Matcher<int[]> matcher)"));
    }

    @Test
    public void shouldSupportLists() throws Exception {
        new FluentMatcherGenerator(ExampleWithList.class, stringWriter).generateMatcher();
        assertThat(stringWriter.toString(), containsString("public ExampleWithListMatcher withStringsList(List<? extends String> expectedValue)"));
        assertThat(stringWriter.toString(), containsString("public ExampleWithListMatcher withStringsList(Matcher<? super List<Object>> matcher)"));
    }

    @Test
    public void shouldSupportBooleansWithAdditionalMethods() throws Exception {
        new FluentMatcherGenerator(ExampleWithBoolean.class, stringWriter).generateMatcher();

        assertThat(stringWriter.toString(), containsString("public ExampleWithBooleanMatcher withOkay(Boolean expectedValue)"));
        assertThat(stringWriter.toString(), containsString("public ExampleWithBooleanMatcher isOkay()"));
        assertThat(stringWriter.toString(), containsString("public ExampleWithBooleanMatcher isNotOkay()"));
        assertThat(stringWriter.toString(), containsString("public ExampleWithBooleanMatcher withOkay(Matcher<? super Boolean> matcher)"));

        assertThat(stringWriter.toString(), containsString("public ExampleWithBooleanMatcher withGood(Boolean expectedValue)"));
        assertThat(stringWriter.toString(), containsString("public ExampleWithBooleanMatcher isGood()"));
        assertThat(stringWriter.toString(), containsString("public ExampleWithBooleanMatcher isNotGood()"));
        assertThat(stringWriter.toString(), containsString("public ExampleWithBooleanMatcher withGood(Matcher<? super Boolean> matcher)"));
    }
}