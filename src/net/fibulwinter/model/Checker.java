package net.fibulwinter.model;

public class Checker {
    private Pos pos;
    private double radius;

    public Checker(Pos pos, double radius) {
        this.pos = new Pos(pos.getX(), pos.getY());
        this.radius = radius;
    }

    public Pos getPos() {
        return pos;
    }

    public double getRadius() {
        return radius;
    }
}
