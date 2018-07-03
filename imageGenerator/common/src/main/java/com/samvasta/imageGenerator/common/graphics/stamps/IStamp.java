package com.samvasta.imageGenerator.common.graphics.stamps;

import java.awt.*;

public interface IStamp
{
    void stamp(Graphics2D g, int x, int y, int width, int height, double rotationAngle);
}
