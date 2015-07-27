package com.mattprovis.fluentmatcher.demo;

import org.hamcrest.Matchers;
import org.junit.Test;

import static com.mattprovis.fluentmatcher.demo.CarMatcher.car;
import static com.mattprovis.fluentmatcher.demo.PassengerMatcher.passenger;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class CarTest {

    private Car actualCar = new Car("ABC-123",
            150,
            asList(
                    new Passenger("Alice", true),
                    new Passenger("Bob", false),
                    new Passenger("Christine", false),
                    new Passenger("Don", false)
            ),
            new String[] {"P", "R", "N", "D", "2", "L"});

    @Test
    public void shouldMatchAnExactValue() throws Exception {
        assertThat(actualCar, is(car().withRegistration("ABC-123")));
    }

    @Test
    public void shouldMatchAComplexCondition() throws Exception {
        assertThat(actualCar, is(car()
                .withRegistration(startsWith("ABC"))
                .withTopSpeed(greaterThan(100))
                .withGears(hasItemInArray("D"))
                ));

        assertThat(actualCar, is(car()
                .withPassengers(hasItem(passenger().withDriver(true)))));
    }

    @Test
    public void shouldNotMatch() throws Exception {
        assertThat(actualCar, car().withRegistration(not("XYZ-789")));
    }
}