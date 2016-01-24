package com.mattprovis.fluentmatcher;

import org.junit.Test;

import static com.mattprovis.fluentmatcher.SparePartsFactoryGearStickMatcher.gearStick;
import static com.mattprovis.fluentmatcher.WheelMatcher.TyreMatcher.tyre;
import static com.mattprovis.fluentmatcher.WheelMatcher.wheel;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class InnerClassOnlyMatcherSystemTest {

    private SparePartsFactory.GearStick actualGearStick = new SparePartsFactory.GearStick("manual 5-speed");

    @Test
    public void shouldBeAbleToCreateAMatcherForAPublicStaticInnerClassInsideAClassThatHasNoMatcher() throws Exception {
        assertThat(actualGearStick, is(gearStick().withType("manual 5-speed")));
    }

    @Test
    public void shouldFailToMatch() throws Exception {
        try {
            assertThat(actualGearStick, is(gearStick().withType("automatic 4-speed with overdrive")));
        } catch(AssertionError expected) {
            assertThat(expected.getMessage(), containsString("type was \"manual 5-speed\""));
        }
    }
}
