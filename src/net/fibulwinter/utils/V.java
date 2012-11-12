package net.fibulwinter.utils;

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

    public double distance(V v){
        return subtract(v).getLength();
    }

    public boolean inDistance(V v, double d){
        double dx = x - v.x;
        double dy = y - v.y;
        return dx*dx+dy*dy<=d*d;
    }

    public V addLength(double dlen){
        double newLength = getLength() +dlen;
        if(newLength<=0) return new V(0,0);
        else {
            V normal = normal();
            return new V(normal.x*newLength, normal.getY()*newLength);
        }
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

    @Override
    public String toString() {
        return "("+x+" "+y+")";
    }

    public V limitLength(double maxLength) {
        if(getLength()<=maxLength)return this;
        else return normal().scale(maxLength);
    }

    public V left() {
        return new V(y,-x);
    }

    public V right() {
        return new V(-y,x);
    }

    public static V middle(V p1, V p2) {
        return new V((p1.x+p2.x)/2,(p1.y+p2.y)/2);
    }
}
