package net.fibulwinter.model;

import com.google.common.base.Optional;
import net.fibulwinter.geometry.GeometryStack;
import net.fibulwinter.geometry.Region;
import net.fibulwinter.geometry.V;

public class FlyTypeModel {
    private static final GeometryStack.DataExtractor<FlyType> FLY_OFF_EXTRACTOR = new GeometryStack.DataExtractor<FlyType>() {
        @Override
        public Optional<FlyType> getData(Region region) {
            return region.getFlyType();
        }
    };

    private GeometryStack geometryStack;

    public FlyTypeModel(GeometryStack geometryStack) {
        this.geometryStack = geometryStack;
    }

    public FlyType getFlyType(V pos){
        return geometryStack.getTopValue(pos, FLY_OFF_EXTRACTOR).or(FlyType.PIT);
    }
}
