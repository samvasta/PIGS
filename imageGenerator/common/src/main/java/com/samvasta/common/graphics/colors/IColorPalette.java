package com.samvasta.common.graphics.colors;

import java.awt.*;

public interface IColorPalette
{
    Color getColor(double percent);

    int getNumColors();

    Color[] getAllColors();

    double[] getWeights();
}
