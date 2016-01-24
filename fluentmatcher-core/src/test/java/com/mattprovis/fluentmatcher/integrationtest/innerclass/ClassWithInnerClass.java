package com.mattprovis.fluentmatcher.integrationtest.innerclass;

public class ClassWithInnerClass {
    public static class Colour {
        String name;

        public Colour() {
        }

        public Colour(String name) {
            this.name = name;
        }
    }

    private Colour selected;

    public ClassWithInnerClass(Colour selected) {
        this.selected = selected;
    }
}
