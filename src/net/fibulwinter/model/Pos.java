package net.fibulwinter.model;

public class Pos {
    private double x;
    private double y;

    public Pos(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setXY(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getLength(){
        return Math.sqrt(x*x+y*y);
    }

    public void scale(double scale){
        x=x*scale;
        y=y*scale;
    }

    public void add(Pos pos){
        x+=pos.x;
        y+=pos.y;
    }

    public void addScaled(Pos other, double scale) {
        x+=other.x*scale;
        y+=other.y*scale;
    }
}
