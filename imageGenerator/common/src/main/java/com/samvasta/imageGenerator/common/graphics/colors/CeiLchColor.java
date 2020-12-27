package com.samvasta.imageGenerator.common.graphics.colors;

import com.samvasta.imageGenerator.common.helpers.MathHelper;

import java.awt.*;

/**
 * an attempt at modeling a color in the HCL Color space
 * @see <a href="https://en.wikipedia.org/wiki/HCL_color_space">https://en.wikipedia.org/wiki/HCL_color_space</a>
 */
public class CeiLchColor
{
    public final double luminance;
    public final double chroma;
    public final double hue;

    /**
     * Creates a color in the CEI-Lch color space
     * @param luminance in range [0,100]
     * @param chroma in range [0,200]
     * @param hue in range [0,360]
     */
    public CeiLchColor(double luminance, double chroma, double hue){
        this.luminance = luminance;
        this.chroma = chroma;
        this.hue = hue;
    }

    public CeiLchColor add(double deltaLum, double deltaChroma, double deltaHue){
        double l = MathHelper.clamp(luminance + deltaLum, 0, 100);
        double c = MathHelper.clamp(chroma + deltaChroma, 0, 200);
        double h = MathHelper.wrap(hue + deltaHue, 0, 360);

        return new CeiLchColor(l, c, h);
    }

    public Color toColor(){
        double[] ceiLch = new double[]{luminance, chroma, hue};
        double[] ceiLab = ColorUtil.ceiLchToCeiLab(ceiLch);
        double[] xyz = ColorUtil.ceiLabToXyz(ceiLab, ColorIlluminant.DEFAULT);
        return ColorUtil.xyzToRgb(xyz);
    }

    public Color toColor(int alpha){
        double[] ceiLch = new double[]{luminance, chroma, hue};
        double[] ceiLab = ColorUtil.ceiLchToCeiLab(ceiLch);
        double[] xyz = ColorUtil.ceiLabToXyz(ceiLab, ColorIlluminant.DEFAULT);
        return ColorUtil.xyzToRgb(xyz, alpha);
    }

    public static CeiLchColor fromColor(Color c){
        double[] xyz = ColorUtil.rgbToXyz(c);
        double[] ceiLab = ColorUtil.xyzToCeiLab(xyz, ColorIlluminant.DEFAULT);
        double[] ceiLch = ColorUtil.ceiLabToCeiLch(ceiLab);
        return new CeiLchColor(ceiLch[0], ceiLch[1], ceiLch[2]);
    }

}
