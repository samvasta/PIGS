package com.samvasta.imageGenerator.common.helpers;

public class MathHelper
{
    public static double clamp(double value, double min, double max){
        if(max < min){
            throw new IllegalArgumentException("Max must be less than or equal to min");
        }
        return Math.min(max, Math.max(min, value));
    }

    public static int clamp(int value, int min, int max){
        if(max < min){
            throw new IllegalArgumentException("Max must be less than or equal to min");
        }
        return Math.min(max, Math.max(min, value));
    }

    public static double clamp01(double value){
        return clamp(value, 0, 1);
    }

    public static double clamp0255(double value){
        return clamp(value, 0, 255);
    }

    public static double wrap(double value, double min, double max){
        while(value > max){
            value -= (max - min);
        }
        while(value < min){
            value += (max - min);
        }
        return value;
    }

    public static double lerp(double v1, double v2, double percent){
        return (percent * v1) + ((1-percent) * v2);
    }

    public static double lerp2d(double tl, double tr, double bl, double br, double xPercent, double yPercent){
        double top = lerp(tl, tr, xPercent);
        double bottom = lerp(bl, br, xPercent);
        return lerp(top, bottom, yPercent);
    }

    public static double min(double...values){
        double min = Double.MAX_VALUE;
        for(double d : values){
            if(d < min){
                min = d;
            }
        }
        return min;
    }

    public static double max(double...values){
        double max = -Double.MAX_VALUE;
        for(double d : values){
            if(d > max){
                max = d;
            }
        }
        return max;
    }
}
