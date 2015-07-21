package com.mattprovis.fluentmatcher.demo;

import org.junit.Test;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.util.List;

import static com.mattprovis.fluentmatcher.demo.ExampleMatcher.example;
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
                new Polygon(),
                new Ellipse2D.Double());

        Example actual = new Example("hello world", shapes, 6);
        assertThat(actual, is(example()
                .withMessage(is("hello world"))
                .withShapes(hasItem(isA(Rectangle.class)))
                .withNumber(is(6))));
    }

    @Test
    public void shouldNotMatch() throws Exception {

        List<Shape> shapes = Arrays.<Shape>asList(
                new Rectangle(2, 3),
                new Polygon(),
                new Ellipse2D.Double());

        Example actual = new Example("hello world", shapes, 6);
        assertThat(actual, example()
                .withNumber(not(3)));
    }
}