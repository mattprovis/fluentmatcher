package com.mattprovis.fluentmatcher;

import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.Matchers.hasItemInArray;
import static org.junit.Assert.assertThat;

public class MatcherSubclassingSystemTest {

    private Car actualCar = new Car(
            "ABC-123",
            150,
            Collections.<Passenger>emptyList(),
            new String[]{"P", "R", "N", "D", "2", "L"});

    @Test
    public void shouldBeAbleToExtendAMatcherBySubclassing() throws Exception {
        assertThat(actualCar, ExtendedCarMatcher.car().canGoBackwards());
    }

    @Test
    public void shouldBeAbleToChainCallsToInheritedAndLocalMethodsWhenSubclassing() throws Exception {
        assertThat(actualCar, ExtendedCarMatcher.car()
                .<ExtendedCarMatcher>withTopSpeed(150)
                .canGoBackwards());
    }

    private static class ExtendedCarMatcher extends CarMatcher {

        public static ExtendedCarMatcher car() {
            return new ExtendedCarMatcher();
        }

        ExtendedCarMatcher canGoBackwards() {
            return withGears(hasItemInArray("R"));
        }
    }
}
