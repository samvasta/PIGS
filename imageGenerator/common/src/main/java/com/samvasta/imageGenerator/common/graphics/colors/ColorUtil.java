package com.samvasta.imageGenerator.common.graphics.colors;

import com.samvasta.imageGenerator.common.helpers.MathHelper;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.awt.color.ColorSpace;

public class ColorUtil
{

    public static Color getRandomColor(MersenneTwister random){
        int colorInt = random.nextInt(0xffffff);
        return new Color(colorInt);
    }

    public static Color getRandomColor(MersenneTwister random, int alpha){
        if(alpha > 255 || alpha < 0){
            throw new IllegalArgumentException("alpha must be in range [0 - 255]");
        }
        int colorInt = alpha << 24 | random.nextInt(0xffffff);
        return new Color(colorInt, true);
    }

    /**
     * Copies the RGB components of th ecolor and uses the alpha parameter for the new alpha channel
     */
    public static Color getTransparent(Color c, int alpha){
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    /**
     * Creates a new color by shifting the HSV components of a color without wrapping the Saturation and Value components. (The Hue component can wrap)
     */
    public static Color shift(Color c, float deltaHue, float deltaSaturation, float deltaBrightness){
        return shift(c, deltaHue, deltaSaturation, deltaBrightness, false, false);
    }

    /**
     * Creates a new color by shifting the HSV components of a color
     */
    public static Color shift(Color c, float deltaHue, float deltaSaturation, float deltaBrightness, boolean wrapSaturation, boolean wrapBrightness){
        float[] hsb = new float[3];
        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);

        hsb[0] += deltaHue;
        hsb[1] += deltaSaturation;
        hsb[2] += deltaBrightness;

        if(hsb[0] < 0){
            hsb[0] += 1f;
        }
        else if(hsb[0] > 1){
            hsb[0] += -1f;
        }

        if(wrapSaturation){
            if(hsb[1] < 0){
                hsb[1] += 1f;
            }
            else if(hsb[1] > 1){
                hsb[1] += -1f;
            }
        }

        if(wrapBrightness){
            if(hsb[2] < 0){
                hsb[2] += 1f;
            }
            else if(hsb[2] > 1){
                hsb[2] += -1f;
            }
        }

        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    public static int getRComponent(int color){
        return (color & 0x00ff0000) >> 16;
    }

    public static int getGComponent(int color){
        return (color & 0x0000ff00) >> 8;
    }

    public static int getBComponent(int color){
        return (color & 0x000000ff);
    }

    public static int getAComponent(int color){
        return (color & 0xff000000) >> 24;
    }


    /**
     * @see <a href="http://www.easyrgb.com/en/math.php">http://www.easyrgb.com/en/math.php</a>
     * @return double array with index 0 = X, 1 = Y, 2 = Z
     */
    public static double[] rgbToXyz(Color c){
        double r = c.getRed() / 255d;
        double g = c.getGreen() / 255d;
        double b = c.getBlue() / 255d;

        double[] xyz = new double[3];

        if(r > 0.04045){
            r = Math.pow(((r + 0.055) / 1.055), 2.4);
        }
        else{
            r = r / 12.92;
        }
        if(g > 0.04045){
            g = Math.pow(((g + 0.055) / 1.055), 2.4);
        }
        else{
            g = g / 12.92;
        }
        if(b > 0.04045){
            b = Math.pow(((b + 0.055) / 1.055), 2.4);
        }
        else{
            b = b / 12.92;
        }

        r *= 100;
        g *= 100;
        b *= 100;

        //x
        xyz[0] = r * 0.4124 + g * 0.3576 + b * 0.1805;

        //y
        xyz[1] = r * 0.2126 + g * 0.7152 + b * 0.0722;

        //z
        xyz[2] = r * 0.0193 + g * 0.1192 + b * 0.9505;

        return xyz;
    }

    /**
     * @param xyz double array with index 0 = X, 1 = Y, 2 = Z
     * @see <a href="http://www.easyrgb.com/en/math.php">http://www.easyrgb.com/en/math.php</a>
     */
    public static Color xyzToRgb(double[] xyz){
        double x = xyz[0] / 100;
        double y = xyz[1] / 100;
        double z = xyz[2] / 100;

        double r = x * 3.2406 + y * -1.5372 + z * -0.4968;
        double g = x * -0.9689 + y * 1.8758 + z * 0.0415;
        double b = x * 0.0557 + y * -0.2040 + z * 1.0570;

        if(r > 0.0031308){
            r = 1.055 * Math.pow(r, (1.0 / 2.4)) - 0.055;
        }
        else{
            r *= 12.92;
        }

        if(g > 0.0031308){
            g = 1.055 * Math.pow(g, (1.0 / 2.4)) - 0.055;
        }
        else{
            g *= 12.92;
        }
        if(b > 0.0031308){
            b = 1.055 * Math.pow(b, (1.0 / 2.4)) - 0.055;
        }
        else{
            b *= 12.92;
        }

        r = MathHelper.clamp01(r);
        g = MathHelper.clamp01(g);
        b = MathHelper.clamp01(b);

        return new Color((int)(r * 255), (int)(g * 255), (int)(b * 255));
    }

    public static double[] xyzToCeiLab(double[] xyz, ColorIlluminant illuminant){

        double x = xyz[0] / illuminant.X;
        double y = xyz[1] / illuminant.Y;
        double z = xyz[2] / illuminant.Z;

        if(x > 0.008856){
            x = Math.pow(x, 1.0/3.0);
        }
        else{
            x = (7.787 * x) + (16.0 / 116.0);
        }

        if(y > 0.008856){
            y = Math.pow(y, 1.0/3.0);
        }
        else{
            y = (7.787 * y) + (16.0 / 116.0);
        }

        if(z > 0.008856){
            z = Math.pow(z, 1.0/3.0);
        }
        else{
            z = (7.787 * z) + (16.0 / 116.0);
        }

        double L = (116.0 * y) - 16.0;
        double a = 500.0 * (x - y);
        double b = 200.0 * (y - z);

        return new double[] { L, a, b};
    }

    public static double[] ceiLabToXyz(double[] ceiLab, ColorIlluminant illuminant){
        double y = (ceiLab[0] + 16.0) / 116.0;
        double x = (ceiLab[1] / 500.0) + y;
        double z = y - (ceiLab[2] / 200.0);

        double yCubed = y*y*y;
        if(yCubed > 0.008856){
            y = yCubed;
        }
        else{
            y = (y - 16.0 / 116.0) / 7.787;
        }

        double xCubed = x*x*x;
        if(xCubed > 0.008856){
            x = xCubed;
        }
        else{
            x = (x - 16.0 / 116.0) / 7.787;
        }

        double zCubed = z*z*z;
        if(zCubed > 0.008856){
            z = zCubed;
        }
        else{
            z = (z - 16.0 / 116.0) / 7.787;
        }

        return new double[] { x * illuminant.X, y * illuminant.Y, z * illuminant.Z};
    }

    public static double[] ceiLabToCeiLch(double[] ceiLab){
        double L = ceiLab[0];
        double a = ceiLab[1];
        double b = ceiLab[2];

        double h = Math.atan2(b, a);

        if(h > 0){
            h = (h / Math.PI) * 180.0;
        }
        else{
            h = 360.0 - (Math.abs(h) / Math.PI) * 180.0;
        }

        double c = Math.sqrt(a * a + b * b);

        return new double[]{ L, c, h };
    }

    public static double[] ceiLchToCeiLab(double[] ceiLch){
        double L = ceiLch[0];
        double c = ceiLch[1];
        double h = ceiLch[2];

        double a = Math.cos(h * Math.PI / 180.0) * c;
        double b = Math.sin(h * Math.PI / 180.0) * c;

        return new double[] { L, a, b };
    }
}
