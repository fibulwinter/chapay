package net.fibulwinter.utils;

import static com.google.common.base.Preconditions.checkArgument;

public class Rectangle {
    private final double minX;
    private final double minY;
    private final double maxX;
    private final double maxY;

    public Rectangle(double minX, double minY, double maxX, double maxY) {
        checkArgument(minX<=maxX);
        checkArgument(minY<=maxY);
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getWidth() {
        return maxX-minX;
    }

    public double getHeight() {
        return maxY-minY;
    }
}
