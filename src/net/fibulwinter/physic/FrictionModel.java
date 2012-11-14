package net.fibulwinter.physic;

import com.google.common.base.Optional;
import net.fibulwinter.geometry.GeometryStack;
import net.fibulwinter.geometry.Region;
import net.fibulwinter.geometry.V;

import static com.google.common.collect.Lists.newArrayList;

public class FrictionModel {
    private double defaultFriction;

    private static final GeometryStack.DataExtractor<Double> FRICTION_EXTRACTOR = new GeometryStack.DataExtractor<Double>() {
        @Override
        public Optional<Double> getData(Region region) {
            return region.getFriction();
        }
    };

    private GeometryStack geometryStack;

    public FrictionModel(double defaultFriction, GeometryStack geometryStack) {
        this.defaultFriction = defaultFriction;
        this.geometryStack = geometryStack;
    }

    public double getFriction(V pos){
        return geometryStack.getTopValue(pos, FRICTION_EXTRACTOR).or(defaultFriction);
    }
}
