package net.fibulwinter.model;

public class Checker {
    private int color;
    private V pos;
    private double radius;
    private double mass=1;
    private V speed;

    public Checker(double x, double y, double radius, int color) {
        this.color = color;
        this.pos = new V(x,y);
        this.radius = radius;
        this.speed = new V(0,0);
    }

    public int getColor() {
        return color;
    }

    public V getPos() {
        return pos;
    }

    public V getSpeed() {
        return speed;
    }

    public double getRadius() {
        return radius;
    }

    public double getMass() {
        return mass;
    }

    public void move(double dt){
        pos=pos.addScaled(speed, dt);
        speed = speed.addLength(-0.5*dt);
    }

    public boolean isTouched(Checker checker) {
        return pos.inDistance(checker.getPos(), checker.getRadius()+radius);
    }

    public void setSpeed(V speed) {
        this.speed = speed;
    }

    public void setPosX(double x) {
        this.pos=new V(x,pos.getY());
    }

    public void setPosY(double y) {
        this.pos=new V(pos.getX(),y);
    }

    public void setPos(V pos) {
        this.pos = pos;
    }
}
