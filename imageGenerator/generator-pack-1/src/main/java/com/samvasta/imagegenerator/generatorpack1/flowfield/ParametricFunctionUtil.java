package com.samvasta.imagegenerator.generatorpack1.flowfield;

import com.samvasta.imageGenerator.common.models.parametricfunctions.*;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.Random;

public class ParametricFunctionUtil {

    public static IParametricFunction getRandomFuction(RandomGenerator random, double widthToHeightRatio){
        int type = random.nextInt(7);
        switch (type){
            case 0:
                return getRandomCosFunction(random);
            case 1:
                return getRandomSinFunction(random);
            case 2:
                return getRandomEllipseFunction(random);
            case 3:
                return getRandomCircleFunction(random, widthToHeightRatio);
            case 4:
                return getRandomLinearFunction(random);
            case 5:
                return getRandomWavyCircleFunction(random, widthToHeightRatio);
            case 6:
                return new LinearFunction(0, 1, 0.5, 0);
        }
        return new EllipseFunction(0.185, 0.185*widthToHeightRatio, 0.5, 0.5);
    }

    public static CosFunction getRandomCosFunction(RandomGenerator random){
        double numPeriods = (int)(10 * random.nextDouble() * random.nextDouble()) + 1;
        if(random.nextFloat() < 0.35){
            numPeriods = 1.0/numPeriods;
        }
        double amplitude = Math.min(0.9, 1/(5*numPeriods));

        double offset = random.nextDouble();

        return new CosFunction(amplitude, numPeriods, offset, 0.5);
    }

    public static SinFunction getRandomSinFunction(RandomGenerator random){
        double numPeriods = (int)(10 * random.nextDouble() * random.nextDouble()) + 1;
        if(random.nextFloat() < 0.35){
            numPeriods = 1.0/numPeriods;
        }
        double amplitude = Math.min(0.9, 1/(5*numPeriods));

        double offset = random.nextDouble();

        return new SinFunction(amplitude, numPeriods, offset, 0.5);
    }

    public static EllipseFunction getRandomEllipseFunction(RandomGenerator random){
        double width = random.nextDouble() * 0.125 + 0.0625;
        double height = random.nextDouble() * 0.125 + 0.0625;
        double centerX = random.nextGaussian()*0.15 + 0.5;
        double centerY = random.nextGaussian()*0.15 + 0.5;
        return new EllipseFunction(width, height, centerX, centerY);
    }

    public static EllipseFunction getRandomCircleFunction(RandomGenerator random, double widthToHeightRatio){
        double width = random.nextDouble() * 0.125 + 0.0625;
        double height = width * widthToHeightRatio;
        double centerX = random.nextGaussian()*0.15 + 0.5;
        double centerY = random.nextGaussian()*0.15 + 0.5;
        return new EllipseFunction(width, height, centerX, centerY);
    }

    public static LinearFunction getRandomLinearFunction(RandomGenerator random){
        double slope = random.nextGaussian();
        double intercept = -slope/2.0 + 0.5;
        if(random.nextBoolean()){
            return new LinearFunction(0,1, intercept, slope);
        }
        else{
            return new LinearFunction(intercept, slope,0,1);
        }
    }
    public static WavyCircleFunction getRandomWavyCircleFunction(RandomGenerator random, double widthToHeightRatio){
        double width = random.nextDouble() * 0.125 + 0.125;
        double centerX = 0.5;
        double centerY = 0.5;

        return new WavyCircleFunction(width, widthToHeightRatio, centerX, centerY, random.nextInt(20), random.nextGaussian() * 0.00625 + 0.0125);
    }

    public static CompositeFunction getRandomCompositeFunction(RandomGenerator random, double widthToHeightRatio){

        CompositeFunction func = new CompositeFunction();

        double slope = random.nextGaussian();
        double intercept = -slope/2.0 + 0.5;

        func.addFunction(new LinearFunction(intercept, slope,0,1));

        func.addFunction(getRandomSinFunction(random));

        return func;
    }
}
