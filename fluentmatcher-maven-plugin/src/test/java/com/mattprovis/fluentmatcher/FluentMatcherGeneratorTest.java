package com.mattprovis.fluentmatcher;

import org.junit.Test;

import java.io.StringWriter;
import java.util.List;

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

    @Test
    public void shouldSupportArrays() throws Exception {
        StringWriter stringWriter = new StringWriter();
        FluentMatcherGenerator.generateMatcherFor(ExampleWithArray.class, stringWriter);
//        System.out.println(stringWriter.toString());
    }

    @Test
    public void shouldSupportPrimitives() throws Exception {
        StringWriter stringWriter = new StringWriter();
        FluentMatcherGenerator.generateMatcherFor(ExampleWithPrimitive.class, stringWriter);
//        System.out.println(stringWriter.toString());
    }

    @Test
    public void shouldSupportLists() throws Exception {
        StringWriter stringWriter = new StringWriter();
        FluentMatcherGenerator.generateMatcherFor(ExampleWithList.class, stringWriter);
//        System.out.println(stringWriter.toString());
    }
}