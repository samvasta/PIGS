package com.samvasta.imageGenerator.common.graphics.textures;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface ITexture
{
    BufferedImage getTexture(Dimension textureSize, ColorPalette palette, MersenneTwister random);
}
