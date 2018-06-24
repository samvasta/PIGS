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
}
