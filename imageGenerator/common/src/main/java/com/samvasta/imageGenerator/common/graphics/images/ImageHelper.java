package com.samvasta.imageGenerator.common.graphics.images;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class ImageHelper
{
    /**
     * @see <a href="https://stackoverflow.com/a/3514297">https://stackoverflow.com/a/3514297</a>
     */
    public static BufferedImage cloneImage(BufferedImage bi){
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
