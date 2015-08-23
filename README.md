# Fluentmatcher

##### *For more readable, more reliable tests.*

A Maven plugin for generating JUnit/Hamcrest compatible matcher classes with a fluent style. Simultaneously match as many (or as few) fields as you want. Easily leverage existing matchers from Hamcrest or your own codebase for complex assertions.

Given a class like this:

    public class Car {

        String registration;

        int topSpeed;

        String[] gears;
    }

You can write tests like this:

    import static com.mattprovis.fluentmatcher.CarMatcher.car;

    ...

    @Test
    public void shouldHaveTheRightRegistration() {
        ...
        assertThat(actualCar, is(car()
                .withRegistration("ABC-123"))
        );
    }

Or with some help from Hamcrest, something a little more powerful:

    import static org.hamcrest.Matchers.greaterThan;
    import static org.hamcrest.Matchers.hasItemInArray;

    ...

    @Test
    public void shouldBeAFastCarWithAnAutomaticTransmission() {
        ...
        assertThat(actualCar, is(car()
                .withTopSpeed(greaterThan(200))
                .withGears(hasItemInArray("D"))
        ));
    }

All criteria you specify will be matched, and anything you don't is simply ignored.

Fluentmatcher supports:

* A static factory method in each matcher for more readable tests
* Typesafe `withX(...)` methods for all fields in the class
* Assert an exact match of the field's value (as defined by that class's `equals()` method)
* Assert by delegating to another matcher
* Match values in private fields, even without exposed getter methods
* Convenient `isX()` and `isNotX()` for boolean fields
* Matcher is recreated if the matched class changes
* Java 7 and 8 compatible

## Usage

> Fluentmatcher is not yet available from the Maven Central repository. Please checkout this project from github and use `mvn install` to deploy the plugin to your your local repository, before adding it to your project.

Add the plugin to your project's `pom.xml`, and list the classes for which you require matchers to be generated in `execution/configuration/pojos`.

    <plugin>
        <groupId>com.mattprovis</groupId>
        <artifactId>fluentmatcher-maven-plugin</artifactId>
        <version>0.1-SNAPSHOT</version>
        <executions>
            <execution>
                <configuration>
                    <pojos>
                        <pojo>com.mattprovis.fluentmatcher.Car</pojo>
                        <pojo>com.mattprovis.fluentmatcher.Passenger</pojo>
                    </pojos>
                </configuration>
                <goals>
                    <goal>generate</goal>
                </goals>
            </execution>
        </executions>
    </plugin>

The matchers will be generated when you next run Maven's `generate-test-sources` phase (which runs by default as part of the usual `install` process).

The generated matcher classes are placed in your project's `target/generated-test-sources` directory and should automatically be visible to your IDE. There's no need to commit the matchers to your source control, as they'll be generated fresh each time.

## Current build status

Live build status for the master branch: [![Build Status](https://travis-ci.org/mattprovis/fluentmatcher.svg?branch=master)](https://travis-ci.org/mattprovis/fluentmatcher)
