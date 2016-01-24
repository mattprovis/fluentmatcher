package com.mattprovis.fluentmatcher;

import org.junit.Test;

import java.util.Collections;

import static com.mattprovis.fluentmatcher.WheelMatcher.TyreMatcher.tyre;
import static com.mattprovis.fluentmatcher.WheelMatcher.wheel;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.junit.Assert.assertThat;

public class ClassWithInnerClassMatcherSystemTest {

    private Wheel actualWheel = new Wheel(new Wheel.Tyre("Symmetrical"));

    @Test
    public void shouldBeAbleToCreateAMatcherForAPublicStaticInnerClass() throws Exception {
        assertThat(actualWheel, wheel().withTyre(tyre().withTreadPattern(is("Symmetrical"))));
    }

    @Test
    public void shouldFailToMatch() throws Exception {
        try {
            assertThat(actualWheel, wheel().withTyre(tyre().withTreadPattern("Directional")));
        } catch(AssertionError expected) {
            assertThat(expected.getMessage(), containsString("treadPattern was \"Symmetrical\""));
        }
    }

}
