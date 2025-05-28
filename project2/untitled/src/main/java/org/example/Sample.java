package org.example;

public class Sample {
    private String id;
    private String instrument;
    private double pitch;
    private double velocity;

    public Sample(String id, String instrument, double pitch, double velocity) {
        this.id = id;
        this.instrument = instrument;
        this.pitch = pitch;
        this.velocity = velocity;
    }

    public String getId() { return id; }
    public String getInstrument() { return instrument; }
    public double getPitch() { return pitch; }
    public double getVelocity() { return velocity; }
}

