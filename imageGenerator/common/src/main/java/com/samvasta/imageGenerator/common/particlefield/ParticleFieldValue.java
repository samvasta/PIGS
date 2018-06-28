package com.samvasta.imageGenerator.common.particlefield;

public class ParticleFieldValue
{
    public final double fieldXPos;
    public final double fieldYPos;
    public final double influenceAngle;
    public final double influenceMagnitude;

    public ParticleFieldValue(final double x, final double y, final double angle, final double magnitude){
        fieldXPos = x;
        fieldYPos = y;
        influenceAngle = angle;
        influenceMagnitude = magnitude;
    }
}
