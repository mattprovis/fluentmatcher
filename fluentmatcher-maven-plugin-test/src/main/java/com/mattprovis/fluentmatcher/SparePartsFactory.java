package com.mattprovis.fluentmatcher;

public class SparePartsFactory {

    public static class Headlight {
        int brightness;
    }

    public static class GearStick {
        String type;

        public GearStick(String type) {
            this.type = type;
        }
    }

}
