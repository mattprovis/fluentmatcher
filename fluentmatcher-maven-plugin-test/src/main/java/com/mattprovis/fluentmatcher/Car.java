package com.mattprovis.fluentmatcher;

import java.util.List;

public class Car {

    private String registration;

    private int topSpeed;

    private List<Passenger> passengers;

    private String[] gears;

    public Car(String registration, int topSpeed, List<Passenger> passengers, String[] gears) {
        this.registration = registration;
        this.topSpeed = topSpeed;
        this.passengers = passengers;
        this.gears = gears;
    }

}
