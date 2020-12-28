package com.samvasta.imageGenerator.common.helpers;

public class MathHelper
{
    public static final double DEG01 = Math.PI / 180.0;
    public static final double DEG30 = Math.PI / 6.0;
    public static final double DEG60 = Math.PI / 3.0;
    public static final double DEG90 = Math.PI / 2.0;
    public static final double DEG120 = Math.PI * 2.0 / 3.0;
    public static final double DEG150 = Math.PI * 5.0 / 12.0;
    public static final double SQRT_3 = Math.sqrt(3.0);

    public static final double PHI = 1.618033988749894848204586834;

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
