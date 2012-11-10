package net.fibulwinter.utils;

import android.graphics.Color;

public class ColorUtils {
    public static int createDisabledColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1]=0.5f;
        hsv[2]=0.5f;
        return Color.HSVToColor(hsv);
    }

    public static int getColorOf(int colorIndex, int colorCount){
        float h = (360f*colorIndex) / colorCount;
        return Color.HSVToColor(new float[]{h,1,1});
    }
}
