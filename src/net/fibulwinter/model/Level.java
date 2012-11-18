package net.fibulwinter.model;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.common.base.Optional;
import net.fibulwinter.geometry.GeometryStack;
import net.fibulwinter.geometry.Rectangle;
import net.fibulwinter.geometry.Region;
import net.fibulwinter.geometry.V;

public class Level {
    private Rectangle borders=new Rectangle(new V(160,205), 300, 410);

    private static final GeometryStack.DataExtractor<FlyType> FLY_OFF_EXTRACTOR = new GeometryStack.DataExtractor<FlyType>() {
        @Override
        public Optional<FlyType> getData(Region region) {
            return region.getFlyType();
        }
    };
    private static final GeometryStack.DataExtractor<Double> FRICTION_EXTRACTOR = new GeometryStack.DataExtractor<Double>() {
        @Override
        public Optional<Double> getData(Region region) {
            return region.getFriction();
        }
    };


    private GeometryStack geometryStack;
    private Bitmap logicalBitmap;

    public Level(Rectangle borders, GeometryStack geometryStack) {
        this.borders = borders;
        this.geometryStack = geometryStack;
        logicalBitmap=generateLogicalBitmap();


    }

    private Bitmap generateLogicalBitmap() {
        int width = (int) borders.getWidth();
        int height = (int) borders.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                V pos = new V(x, y);
                FlyType flyType = getFlyType(pos);
                int color=Color.BLACK;
                switch (flyType) {
                    case PIT:
                        color=Color.BLACK;
                        break;
                    case WATER:
                        color=Color.BLUE;
                        break;
                    case GRASS:
                        color=Color.GREEN;
                        break;
                }
                bitmap.setPixel(x,y, color);
            }
        }
        return bitmap;
    }

    public FlyType getFlyType(V pos){
        return geometryStack.getTopValue(pos, FLY_OFF_EXTRACTOR).or(FlyType.PIT);
    }

    public double getFriction(V pos){
        return geometryStack.getTopValue(pos, FRICTION_EXTRACTOR).or(0.5);
    }

    public Bitmap getLogicalBitmap() {
        return logicalBitmap;
    }

    public Rectangle getBorders() {
        return borders;
    }
}
