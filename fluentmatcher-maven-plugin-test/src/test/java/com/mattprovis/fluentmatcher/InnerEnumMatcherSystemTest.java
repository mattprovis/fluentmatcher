package com.mattprovis.fluentmatcher;

import org.junit.Test;

import static com.mattprovis.fluentmatcher.EntertainmentSystem.Component.CdPlayer;
import static com.mattprovis.fluentmatcher.EntertainmentSystem.Component.Radio;
import static com.mattprovis.fluentmatcher.EntertainmentSystemMatcher.entertainmentSystem;
import static com.mattprovis.fluentmatcher.WheelMatcher.TyreMatcher.tyre;
import static com.mattprovis.fluentmatcher.WheelMatcher.wheel;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class InnerEnumMatcherSystemTest {

    private EntertainmentSystem actualEntertainmentSystem = new EntertainmentSystem(asList(CdPlayer, Radio));

    @Test
    public void shouldBeAbleToCreateAMatcherForAPublicInnerEnum() throws Exception {
        assertThat(actualEntertainmentSystem, entertainmentSystem().withComponents(hasItem(CdPlayer)));
    }

    @Test
    public void shouldFailToMatch() throws Exception {
        try {
            assertThat(actualEntertainmentSystem, entertainmentSystem().withComponents(empty()));
        } catch(AssertionError expected) {
            assertThat(expected.getMessage(), containsString("components <[CdPlayer, Radio]>"));
        }
    }

}
