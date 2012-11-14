package net.fibulwinter.geometry;

import com.google.common.base.Optional;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class GeometryStack {
    private List<Region> regions=newArrayList();

    public List<Region> getRegions() {
        return regions;
    }

    public static interface DataExtractor<T>{
        Optional<T> getData(Region region);
    }

    public <T> Optional<T> getTopValue(V pos, DataExtractor<T> extractor){
        Optional<T> value=Optional.absent();
        for(Region region :regions){
            if(region.getShape().contains(pos)){
                Optional<T> extractedData = extractor.getData(region);
                if(extractedData.isPresent()){
                    value=extractedData;
                }
            }
        }
        return value;
    }

}
