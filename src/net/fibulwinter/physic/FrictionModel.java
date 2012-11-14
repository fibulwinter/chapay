package net.fibulwinter.physic;

import net.fibulwinter.utils.V;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class FrictionModel {
    public static class FrictionRegion{
        private Disk shape;
        private double friction;
        private int color;

        public FrictionRegion(Disk shape, double friction, int color) {
            this.shape = shape;
            this.friction = friction;
            this.color = color;
        }

        public Disk getShape() {
            return shape;
        }

        public double getFriction() {
            return friction;
        }

        public int getColor() {
            return color;
        }


    }

    private double defaultFriction=0;

    private List<FrictionRegion> regions=newArrayList();

    public List<FrictionRegion> getRegions() {
        return regions;
    }

    public double getFriction(V pos){
        double friction=defaultFriction;
        for(FrictionRegion frictionRegion:regions){
            if(frictionRegion.getShape().contains(pos)){
                friction=frictionRegion.getFriction();
            }
        }
        return friction;
    }
}
