package com.samvasta.imageGenerator.common.graphics.stamps;

import org.apache.commons.math3.random.RandomGenerator;

import java.awt.*;

public interface IStamp
{
    void stamp(Graphics2D g, StampInfo stampInfo, RandomGenerator random);
}
