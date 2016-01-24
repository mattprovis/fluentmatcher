package com.mattprovis.fluentmatcher;

import java.util.List;

public class EntertainmentSystem {

    private List<Component> components;

    public EntertainmentSystem(List<Component> components) {
        this.components = components;
    }

    public enum Component {
        CdPlayer,
        Radio
    }
}
