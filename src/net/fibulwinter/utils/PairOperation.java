package net.fibulwinter.utils;

import android.util.Log;
import com.google.common.base.Optional;

public abstract class PairOperation<T, U, V> {
    private final Class<T> clazz1;
    private final Class<U> clazz2;

    public PairOperation(Class<T> clazz1, Class<U> clazz2){
        this.clazz1 = clazz1;
        this.clazz2 = clazz2;
    }

    public Optional<V> tryPerformOn(Object object1, Object object2){
        if(clazz1.isInstance(object1) && clazz2.isInstance(object2)){
            return performWithPair(clazz1.cast(object1), clazz2.cast(object2));
        }
        if(clazz1.isInstance(object2) && clazz2.isInstance(object1)){
            return performWithPair(clazz1.cast(object2), clazz2.cast(object1));
        }
        return Optional.absent();
    }

    public abstract Optional<V> performWithPair(T t, U u);

    public static <V> Optional<V> applySingle(Object object1, Object object2, PairOperation<?,?, V>... pairOperations){
        for (PairOperation<?, ?, V> pairOperation : pairOperations) {
            Optional<V> result = pairOperation.tryPerformOn(object1, object2);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.absent();
    }

}
