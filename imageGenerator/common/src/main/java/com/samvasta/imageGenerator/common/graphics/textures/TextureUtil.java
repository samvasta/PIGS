//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.imageGenerator.common.graphics.textures.TextureUtil
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.imageGenerator.common.graphics.textures;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.SingleColorPalette;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TextureUtil
{
    private static final ColorPalette MASK_PALETTE = new SingleColorPalette(Color.BLUE);

    public static BufferedImage getMaskedTexture(ITexture textureGenerator, ITexture maskGenerator, Dimension textureSize, ColorPalette palette, MersenneTwister random){
        BufferedImage texture = textureGenerator.getTexture(textureSize, palette, random);
        BufferedImage mask = maskGenerator.getTexture(textureSize, MASK_PALETTE, random);

        BufferedImage maskedTexture = new BufferedImage(texture.getWidth(), texture.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int[] pixels = maskedTexture.getRGB(0, 0, texture.getWidth(), texture.getHeight(), null, 0, texture.getWidth());


        int[] colorPixels = texture.getRGB(0, 0, texture.getWidth(), texture.getHeight(), null, 0, texture.getWidth());
        int[] maskPixels = mask.getRGB(0, 0, texture.getWidth(), texture.getHeight(), null, 0, texture.getWidth());

        for(int i = 0; i < pixels.length; i++){
            pixels[i] = colorPixels[i] | (maskPixels[i] << 24);
        }

        maskedTexture.setRGB(0, 0, maskedTexture.getWidth(), maskedTexture.getHeight(), pixels, 0, maskedTexture.getWidth());

        return maskedTexture;
    }
}
