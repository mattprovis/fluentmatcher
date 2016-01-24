package com.mattprovis.fluentmatcher.integrationtest.innerenum;

public class ClassWithEnum {
    public static enum Colour { RED, GREEN, BLUE }

    private Colour selected;

    public ClassWithEnum(Colour selected) {
        this.selected = selected;
    }
}
