package net.fibulwinter.utils;

import com.google.common.base.Function;

import java.util.Collection;

public class CollectUtils {
    public static <T> T getMin(Collection<T> items, Function<T, Double> function){
        T minItem=null;
        double minV=Double.POSITIVE_INFINITY;
        for(T item:items){
            Double v = function.apply(item);
            if(v<minV){
                minV=v;
                minItem=item;
            }
        }
        return minItem;
    }
}
