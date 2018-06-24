package com.samvasta.imageGenerator.common.graphics.textures;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.images.ProtoTexture;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface ITexture
{
    ProtoTexture getTexture(Dimension textureSize, MersenneTwister random);

    BufferedImage colorize(ProtoTexture protoTexture, ColorPalette palette, MersenneTwister random);
}
