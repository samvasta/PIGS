package com.samvasta.imageGenerator.common.graphics.textures;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;

public interface ITexture
{
    void fill(Graphics2D g, Polygon bounds, ColorPalette palette, MersenneTwister random);
}
