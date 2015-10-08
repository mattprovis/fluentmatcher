package com.mattprovis.fluentmatcher;

import junit.framework.TestResult;
import org.junit.Test;

import static com.mattprovis.fluentmatcher.CarMatcher.car;
import static com.mattprovis.fluentmatcher.PassengerMatcher.passenger;
import static java.util.Arrays.asList;
import static junit.framework.TestResultMatcher.testResult;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class FluentMatcherMavenPluginTest {

    private final Passenger alice = new Passenger("Alice", true);
    private final Passenger bob = new Passenger("Bob", false);
    private final Passenger christine = new Passenger("Christine", false);
    private final Passenger don = new Passenger("Don", false);

    private Car actualCar = new Car(
            "ABC-123",
            150,
            asList(alice, bob, christine, don),
            new String[]{"P", "R", "N", "D", "2", "L"});

    @Test
    public void shouldMatchAnythingIfNoConditionsGiven() throws Exception {
        assertThat(alice, is(passenger()));
        assertThat(bob, is(passenger()));
    }

    @Test
    public void shouldMatchAnExactValue() throws Exception {
        assertThat(actualCar, is(car().withRegistration("ABC-123")));
    }

    @Test
    public void shouldFailIfConditionDoesNotMatch() throws Exception {
        try {
            assertThat(actualCar, car().withRegistration("XYZ-789"));
        } catch (AssertionError expected) {
            return;
        }

        // This must be outside of the try, because the AssertionError it throws would satisfy the catch type!
        fail();
    }

    @Test
    public void shouldMatchANestedMatcher() throws Exception {
        assertThat(actualCar, is(car()
                .withPassengers(
                        hasItem(alice))));
    }

    @Test
    public void shouldMatchAComplexMixedCondition() throws Exception {
        assertThat(actualCar, is(car()
                .withRegistration("ABC-123")     // true
                .withTopSpeed(greaterThan(100))  // true
                .withGears(hasItemInArray("D"))  // true
        ));
    }

    @Test
    public void shouldFailIfAnyMatchInAComplexMixedConditionFails() throws Exception {
        try {
            assertThat(actualCar, is(car()
                    .withRegistration("ZZZ-999")     // false
                    .withTopSpeed(greaterThan(100))  // true
                    .withGears(hasItemInArray("D"))  // true
            ));
        } catch (AssertionError expected) {
            return;
        }

        // This must be outside of the try, because the AssertionError it throws would satisfy the catch type!
        fail();
    }

    @Test
    public void shouldSupportSimplifiedBooleans() throws Exception {
        assertThat(alice, is(passenger().isDriver()));
        assertThat(bob, is(passenger().isNotDriver()));
    }

    @Test
    public void shouldSupportMatchingBySuperclass() throws Exception {
        SportsCar sportsCar = new SportsCar("ZOOM", 400, asList(alice), new String[]{"R", "N", "1", "2", "3", "4", "5", "6"});
        assertThat(sportsCar, is(car()));
    }

    @Test
    public void shouldSupportPojoClassesInTestScope() throws Exception {
        TestResult testScopedObject = new TestResult();
        assertThat(testScopedObject, is(testResult()));
    }
}