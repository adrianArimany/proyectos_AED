package org.example;

public class Sample {
    private String id;
    private String instrument;
    private double tempo;
    private double velocity;
    private double duration;

    public Sample(String id, String instrument, double tempo, double velocity, double duration) {
        this.id = id;
        this.instrument = instrument;
        this.tempo = tempo;
        this.velocity = velocity;
        this.duration = duration;
    }

    public String getId() { return id; }
    public String getInstrument() { return instrument; }
    public double getTempo() { return tempo; }
    public double getVelocity() { return velocity; }
    public double getDuration() { return duration; }
}