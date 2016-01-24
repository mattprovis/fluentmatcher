package com.mattprovis.fluentmatcher.integrationtest.innerclass;

import com.mattprovis.fluentmatcher.FluentMatcher;
import org.junit.Ignore;
import org.junit.Test;

import static com.mattprovis.fluentmatcher.integrationtest.GeneratedMatcherTestUtils.createMatcherFor;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InnerClassMatcherTest {

    @Test
    public void shouldCompileAndExecuteGeneratedFluentMatcherForClassThatHasAnInnerClass() throws Exception {
        FluentMatcher matcherInstance = createMatcherFor(ClassWithInnerClass.class);

        ClassWithInnerClass samplePojoInstance = new ClassWithInnerClass(new ClassWithInnerClass.Colour("red"));
        assertThat(matcherInstance.matches(samplePojoInstance), is(true));
    }

    @Test
    public void shouldCompileAndExecuteGeneratedFluentMatcherForInnerClassOnly() throws Exception {
        FluentMatcher matcherInstance = createMatcherFor(SparePartsFactory.GearStick.class);

        SparePartsFactory.GearStick samplePojoInstance = new SparePartsFactory.GearStick();
        assertThat(matcherInstance.matches(samplePojoInstance), is(true));
    }

}
