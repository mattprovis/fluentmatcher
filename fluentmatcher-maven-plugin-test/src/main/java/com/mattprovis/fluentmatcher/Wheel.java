package com.mattprovis.fluentmatcher;

public class Wheel {

    Tyre tyre;

    public Wheel(Tyre tyre) {
        this.tyre = tyre;
    }

    public static class Tyre {
        String treadPattern;

        public Tyre(String treadPattern) {
            this.treadPattern = treadPattern;
        }
    }
}
