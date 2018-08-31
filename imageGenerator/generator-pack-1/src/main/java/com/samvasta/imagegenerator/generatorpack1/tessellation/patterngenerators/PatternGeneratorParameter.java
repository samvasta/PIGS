package com.samvasta.imagegenerator.generatorpack1.tessellation.patterngenerators;

import org.apache.commons.math3.random.RandomGenerator;

public class PatternGeneratorParameter
{
    public final double min;
    public final double max;
    public final double defaultValue;

    public PatternGeneratorParameter(double minIn, double maxIn, double defaultValueIn){
        min = minIn;
        max = maxIn;
        defaultValue = defaultValueIn;
    }

    public double getRandValue(RandomGenerator rand){
        //50% chance to always return default value
        if(rand.nextBoolean()){
            double value = rand.nextGaussian() * (defaultValue);
            if(value >= min && value <= max){
                return value;
            }

            return rand.nextDouble() * (max - min) + min;
        }

        return defaultValue;
    }
}
