package net.fibulwinter.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    private static ImageCache instance;
    private Resources resources;
    private Map<String, Bitmap> originals = new HashMap<String, Bitmap>();
    private Map<String, Bitmap> scaleds = new HashMap<String, Bitmap>();
    private Map<String, Integer> scaleSizes = new HashMap<String, Integer>();

    public static ImageCache get() {
        return instance;
    }

    public static void init(Resources resources) {
        instance = new ImageCache(resources);
    }

    private ImageCache(Resources resources) {
        this.resources = resources;
    }

    public Bitmap get(String name, int sizePixels) {
        Bitmap original = originals.get(name);
        if (original == null) {
            int id = resources.getIdentifier(name, "drawable", "net.fibulwinter");
            original = BitmapFactory.decodeResource(resources, id);
            originals.put(name, original);
        }
        if (sizePixels == original.getWidth()) {
            return original;
        } else {
            Bitmap scaled = scaleds.get(name);
            Integer size = scaleSizes.get(name);
            if (scaled == null || size == null || size != sizePixels) {
                scaled = Bitmap.createScaledBitmap(original, sizePixels, sizePixels, true);
                scaleds.put(name, scaled);
                scaleSizes.put(name, sizePixels);
            }
            return scaled;
        }
    }

    public Bitmap get(int id, int sizePixels) {
        String name=String.valueOf(id);
        Bitmap original = originals.get(name);
        if (original == null) {
            original = BitmapFactory.decodeResource(resources, id);
            originals.put(name, original);
        }
        if (sizePixels == original.getWidth()) {
            return original;
        } else {
            Bitmap scaled = scaleds.get(name);
            Integer size = scaleSizes.get(name);
            if (scaled == null || size == null || size != sizePixels) {
                scaled = Bitmap.createScaledBitmap(original, sizePixels, sizePixels, true);
                scaleds.put(name, scaled);
                scaleSizes.put(name, sizePixels);
            }
            return scaled;
        }
    }

    public Bitmap get(int id, int sizeX, int sizeY) {
        String name=String.valueOf(id);
        Bitmap original = originals.get(name);
        if (original == null) {
            original = BitmapFactory.decodeResource(resources, id);
            originals.put(name, original);
        }
        if (sizeX == original.getWidth()) {
            return original;
        } else {
            Bitmap scaled = scaleds.get(name);
            Integer size = scaleSizes.get(name);
            if (scaled == null || size == null || size != sizeX) {
                scaled = Bitmap.createScaledBitmap(original, sizeX, sizeY, true);
                scaleds.put(name, scaled);
                scaleSizes.put(name, sizeX);
            }
            return scaled;
        }
    }
}