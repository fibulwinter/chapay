package net.fibulwinter.model;

import net.fibulwinter.physic.DynamicBody;
import net.fibulwinter.geometry.Disk;
import net.fibulwinter.geometry.V;

public class Checker {
    private int color;
    private DynamicBody dynamicBody;
    private Disk disk;
    private boolean dead=false;

    public Checker(double x, double y, double radius, int color) {
        this.color = color;
        this.disk=new Disk(new V(x,y), radius);
        dynamicBody =new DynamicBody(disk, 1.0, new V(0,0));
        dynamicBody.setMaxSpeed(radius*2);
    }

    public int getColor() {
        return color;
    }

    public DynamicBody getDynamicBody() {
        return dynamicBody;
    }

    public Disk getDisk() {
        return disk;
    }

    public boolean isMoving(){
        return getDynamicBody().getVelocity().getLength()>1;
    }

    public void die(){
        this.dead=true;
    }

    public boolean isDead() {
        return dead;
    }
}
