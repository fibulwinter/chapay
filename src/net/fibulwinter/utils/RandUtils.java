package net.fibulwinter.utils;

public class RandUtils {
    public static double rand(double min, double max) {
        return Math.random() * (max-min) + min;
    }

    public static boolean getBoolean() {
        return Math.random()>0.5;
    }

    public static double mix(double a, double b, double p){
        return a*(1-p)+b*p;
    }
}
