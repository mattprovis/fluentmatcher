package com.mattprovis.fluentmatcher;

import com.mattprovis.fluentmatcher.compiled.Example;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Test;

import java.io.StringWriter;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
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

    @After
    public void tearDown() throws Exception {
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getDefault()));
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldSupportPrimitives() throws Exception {
        new FluentMatcherGenerator(ExampleWithPrimitive.class, stringWriter).generateMatcher();
        String generatedSource = stringWriter.toString();

        assertThat(generatedSource, containsString("public <SubclassOfMatcher extends ExampleWithPrimitiveMatcher> SubclassOfMatcher withAnInt(Integer expectedValue)"));
        assertThat(generatedSource, containsString("public <SubclassOfMatcher extends ExampleWithPrimitiveMatcher> SubclassOfMatcher withAnInt(Matcher<? super Integer> matcher)"));
    }

    @Test
    public void shouldSupportArrays() throws Exception {
        new FluentMatcherGenerator(ExampleWithArray.class, stringWriter).generateMatcher();
        String generatedSource = stringWriter.toString();

        assertThat(generatedSource, containsString("public <SubclassOfMatcher extends ExampleWithArrayMatcher> SubclassOfMatcher withIntArray(int[] expectedValue)"));
        assertThat(generatedSource, containsString("public <SubclassOfMatcher extends ExampleWithArrayMatcher> SubclassOfMatcher withIntArray(Matcher<int[]> matcher)"));
    }

    @Test
    public void shouldSupportLists() throws Exception {
        new FluentMatcherGenerator(ExampleWithList.class, stringWriter).generateMatcher();
        String generatedSource = stringWriter.toString();

        assertThat(generatedSource, containsString("public <SubclassOfMatcher extends ExampleWithListMatcher> SubclassOfMatcher withStringsList(List<? extends String> expectedValue)"));
        assertThat(generatedSource, containsString("public <SubclassOfMatcher extends ExampleWithListMatcher> SubclassOfMatcher withStringsList(Matcher<? super List<Object>> matcher)"));
    }

    @Test
    public void shouldSupportBooleansWithAdditionalMethods() throws Exception {
        new FluentMatcherGenerator(ExampleWithBoolean.class, stringWriter).generateMatcher();
        String generatedSource = stringWriter.toString();

        assertThat(generatedSource, containsString("public <SubclassOfMatcher extends ExampleWithBooleanMatcher> SubclassOfMatcher withOkay(Boolean expectedValue)"));
        assertThat(generatedSource, containsString("public <SubclassOfMatcher extends ExampleWithBooleanMatcher> SubclassOfMatcher isOkay()"));
        assertThat(generatedSource, containsString("public <SubclassOfMatcher extends ExampleWithBooleanMatcher> SubclassOfMatcher isNotOkay()"));
        assertThat(generatedSource, containsString("public <SubclassOfMatcher extends ExampleWithBooleanMatcher> SubclassOfMatcher withOkay(Matcher<? super Boolean> matcher)"));

        assertThat(generatedSource, containsString("public <SubclassOfMatcher extends ExampleWithBooleanMatcher> SubclassOfMatcher withGood(Boolean expectedValue)"));
        assertThat(generatedSource, containsString("public <SubclassOfMatcher extends ExampleWithBooleanMatcher> SubclassOfMatcher isGood()"));
        assertThat(generatedSource, containsString("public <SubclassOfMatcher extends ExampleWithBooleanMatcher> SubclassOfMatcher isNotGood()"));
        assertThat(generatedSource, containsString("public <SubclassOfMatcher extends ExampleWithBooleanMatcher> SubclassOfMatcher withGood(Matcher<? super Boolean> matcher)"));
    }

    @Test
    public void shouldIncludeGeneratedAnnotation() throws Exception {
        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(10));
        DateTimeUtils.setCurrentMillisFixed(new DateTime(2015, 8, 12, 10, 33, 0).getMillis());

        new FluentMatcherGenerator(Example.class, stringWriter).generateMatcher();

        String generatedAnnotationLine = getLineBeginningWith("@Generated", stringWriter.toString());
        assertThat(generatedAnnotationLine, is("@Generated(value = \"com.mattprovis.fluentmatcher.FluentMatcherGenerator\", date = \"2015-08-12T10:33:00.000+10:00\")"));
    }

    @Test
    public void generatedAnnotationDateShouldWorkInOtherTimezones() throws Exception {
        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(1));
        DateTimeUtils.setCurrentMillisFixed(new DateTime(2015, 8, 12, 10, 33, 0).getMillis());

        new FluentMatcherGenerator(Example.class, stringWriter).generateMatcher();

        String generatedAnnotationLine = getLineBeginningWith("@Generated", stringWriter.toString());
        assertThat(generatedAnnotationLine, containsString("\"2015-08-12T10:33:00.000+01:00\""));
    }

    private String getLineBeginningWith(String expectedPrefix, String lines) {
        Pattern lineSearchPattern = Pattern.compile("\\s*(" + Pattern.quote(expectedPrefix) + ".*)");
        Matcher m = lineSearchPattern.matcher(lines);
        if (m.find()) {
            return m.group(1);
        }

        return null;
    }
}