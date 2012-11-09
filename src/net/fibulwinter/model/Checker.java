package net.fibulwinter.model;

public class Checker {
    private Pos pos;
    private double radius;
    private Pos speed=new Pos(0,0);

    public Checker(Pos pos, double radius) {
        this.pos = new Pos(pos.getX(), pos.getY());
        this.radius = radius;
    }

    public Pos getPos() {
        return pos;
    }

    public Pos getSpeed() {
        return speed;
    }

    public double getRadius() {
        return radius;
    }

    public void move(double dt){
        pos.addScaled(speed, dt);
    }
}
