package net.fibulwinter.model;

public class V {
    private final double x;
    private final double y;

    public V(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public V(V v) {
        this(v.x, v.y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getLength(){
        return Math.sqrt(x*x+y*y);
    }

    public V scale(double sx, double sy) {
        return new V(x*sx,y*sy);
    }

    public V scale(double scale){
        return scale(scale,scale);
    }

    public V add(V v){
        return new V(x+ v.x,y+ v.y);
    }

    public V subtract(V v){
        return new V(x- v.x,y- v.y);
    }

    public V addScaled(V other, double scale) {
        return new V(x+other.x*scale,y+other.y*scale);
    }

    public V normal() {
        return scale(1d/getLength());
    }

    public double dot(V other) {
        return x*other.x+y*other.y;
    }
}
