package com.mattprovis.fluentmatcher;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GeneratedMatcherClassMetaTest {

    @Test
    public void shouldBeASubclassOfFluentMatcher() throws Exception {
        Class<?> superclass = CarMatcher.class.getSuperclass();
        Class<?> fluentMatcherClass = FluentMatcher.class;

        assertThat(superclass.equals(fluentMatcherClass), is(true));
    }
}
