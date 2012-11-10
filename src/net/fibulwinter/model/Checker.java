package net.fibulwinter.model;

public class Checker {
    private int color;
    private V pos;
    private double radius;
    private double mass=1;
    private V speed;

    public Checker(double x, double y, double radius, double vx, double vy) {
        this.pos = new V(x,y);
        this.radius = radius;
        this.speed = new V(vx,vy);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
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
        if(speed.getLength()<0.1)speed=new V(0,0);
        else speed=speed.scale(Math.min(0.95,speed.getLength()/5));
    }

    public boolean isTouched(Checker checker) {
        return pos.subtract(checker.getPos()).getLength()<(checker.getRadius()+radius);
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
}
