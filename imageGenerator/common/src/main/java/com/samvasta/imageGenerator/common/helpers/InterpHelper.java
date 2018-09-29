//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.imageGenerator.common.helpers.InterpHelper
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.imageGenerator.common.helpers;

public class InterpHelper {

    public static double lerp(double v1, double v2, double percent){
        return (percent * v1) + ((1-percent) * v2);
    }

    public static float lerp(float v1, float v2, float percent){
        return (percent * v1) + ((1-percent) * v2);
    }

    public static double lerp2d(double tl, double tr, double bl, double br, double xPercent, double yPercent){
        double top = lerp(tl, tr, xPercent);
        double bottom = lerp(bl, br, xPercent);
        return lerp(top, bottom, yPercent);
    }


}
