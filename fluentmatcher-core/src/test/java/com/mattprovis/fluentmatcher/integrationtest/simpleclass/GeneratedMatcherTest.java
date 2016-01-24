package com.mattprovis.fluentmatcher.integrationtest.simpleclass;

import com.mattprovis.fluentmatcher.FluentMatcher;
import org.junit.Test;

import static com.mattprovis.fluentmatcher.integrationtest.GeneratedMatcherTestUtils.createMatcherFor;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GeneratedMatcherTest {

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

}
