package com.mcustom.library.utils;

/**
 * @WYU-WIN
 * @date 2021/12/13
 * @Description:
 */

import android.graphics.Color;
import android.util.Log;

/**
 * @WYU-WIN
 * @date 2021/12/13
 * @Description: 多色组件选取颜色工具类
 */
public class ColorPickerUtils {
    private static String TAG = "ColorPickerUtils";

    /**
     * 获取某个百分比位置的颜色
     *
     * @param radio 取值[0,1]
     * @return
     */
    public static int getColor(int[] mColorArr, float radio) {
        int startColor;
        int endColor;
        if (radio >= 1) {
            return mColorArr[mColorArr.length - 1];
        }
        radio = radio * ((float) (mColorArr.length - 1) / mColorArr.length);
        for (int i = 0; i < mColorArr.length; i++) {
            float radios = getRadios(i, mColorArr.length);
            if (radio <= radios) {
                if (i == 0) {
                    return mColorArr[0];
                } else if (i == mColorArr.length) {
                    return mColorArr[i - 1];
                }

                startColor = mColorArr[i - 1];
                endColor = mColorArr[i];
                float areaRadio = getAreaRadio(radio, getRadios(i - 1, mColorArr.length), radios);
                return getColorFrom(startColor, endColor, areaRadio);
            }
        }
        return -1;
    }

    private static float getAreaRadio(float radio, float startPosition, float endPosition) {
        return (radio - startPosition) / (endPosition - startPosition);
    }

    private static float getRadios(float startIndex, float endIndex) {
        return startIndex / endIndex;
    }

    /**
     * 取两个颜色间的渐变区间 中的某一点的颜色
     *
     * @param startColor
     * @param endColor
     * @param radio
     * @return
     */
    public static int getColorFrom(int startColor, int endColor, float radio) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);

        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.argb(255, red, greed, blue);
    }
}
