package com.mattprovis.fluentmatcher.demo;

import org.junit.Test;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.util.List;

import static com.mattprovis.fluentmatcher.demo.ExampleMatcher.example;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class ExampleTest {

    @Test
    public void shouldMatch() throws Exception {

        List<Shape> shapes = Arrays.<Shape>asList(
                new Rectangle(2, 3),
                new Ellipse2D.Double());
        Example actual = new Example("hello world", shapes, 6);

        assertThat(actual, is(example()
                .withMessage(is("hello world"))
                .withShapes(hasItem(isA(Rectangle.class)))
                .withNumber(is(6))));

        // Again, but without using is() methods.
        // We are expecting an exact match as defined by equals() on each field.
        assertThat(actual, example()
                .withMessage("hello world")
                .withShapes(asList(
                        new Rectangle(2, 3),
                        new Ellipse2D.Double()))
                .withNumber(6));
    }

    @Test
    public void shouldNotMatch() throws Exception {

        List<Shape> shapes = Arrays.<Shape>asList(
                new Rectangle(2, 3),
                new Ellipse2D.Double());

        Example actual = new Example("hello world", shapes, 6);
        assertThat(actual, example()
                .withNumber(not(3)));
    }
}