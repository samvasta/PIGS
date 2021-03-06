package com.samvasta.imagegenerator.generatorpack1.tessellation.filltextures;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.images.ProtoTexture;
import com.samvasta.imageGenerator.common.graphics.textures.ITexture;
import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class SolidColorTexture implements ITexture {

    private Color fill;

    public SolidColorTexture() {
        fill = null;
    }

    public SolidColorTexture(Color fillIn){
        fill = fillIn;
    }

    @Override
    public ProtoTexture getTexture(Dimension textureSize, RandomGenerator random) {
        ProtoTexture tex = new ProtoTexture(textureSize);
        double[] pixels = new double[textureSize.width * textureSize.height];
        tex.setPixels(0, 0, textureSize.width, textureSize.height, pixels);
        return tex;
    }

    @Override
    public BufferedImage colorize(ProtoTexture protoTexture, ColorPalette palette, RandomGenerator random) {
        BufferedImage img = new BufferedImage(protoTexture.getWidth(), protoTexture.getHeight(), BufferedImage.TYPE_INT_ARGB);
        if(fill == null) {
            double percent = random.nextDouble();
            int sampleOption = random.nextInt(3);
            switch(sampleOption){
                case 0:
                default:
                    fill = palette.getColor(percent);
                    break;
                case 1:
                    fill = palette.getColorSmooth(percent);
                    break;
                case 2:
                    fill = palette.getColorInverseSmooth(percent);
                    break;
            }
        }
        int[] pixels = new int[img.getWidth()*img.getHeight()];
        Arrays.fill(pixels, fill.getRGB());
        img.setRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
        return img;
    }
}
