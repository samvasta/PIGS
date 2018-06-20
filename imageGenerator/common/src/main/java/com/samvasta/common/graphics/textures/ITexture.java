package com.samvasta.common.graphics.textures;

import com.samvasta.common.graphics.colors.IColorPalette;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;

public interface ITexture
{
    void fill(Graphics2D g, Polygon bounds, IColorPalette palette, MersenneTwister random);
}
