package com.samvasta.imageGenerator.common.graphics.colors;

import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ColorPalette
{
    private List<Color> colors;
    private List<Double> relativeWeights;
    private double totalRelativeWeight;

    private Color biggestColor;
    private double biggestColorWeight;
    private Color smallestColor;
    private double smallestColorWeight;

    public ColorPalette(Color[] colorsArr, double[] weights){
        colors = new ArrayList<>();
        relativeWeights = new ArrayList<>();
        totalRelativeWeight = 0;
        smallestColorWeight = Double.MAX_VALUE;
        biggestColorWeight = -1;

        for(int i = 0; i < colorsArr.length; i++){
            addColor(colorsArr[i], weights[i]);
        }
    }

    public ColorPalette(MersenneTwister random){
        colors = new ArrayList<>();
        relativeWeights = new ArrayList<>();
        totalRelativeWeight = 0;
        smallestColorWeight = Double.MAX_VALUE;
        biggestColorWeight = -1;
        initColorsAndWeights(random);

        if(colors.size() == 0 || relativeWeights.size() == 0){
            throw new ArrayIndexOutOfBoundsException("Colors and Weights must have length greater than 0");
        }
    }

    protected abstract void initColorsAndWeights(MersenneTwister random);

    protected void addColor(Color col, double weight){
        colors.add(col);
        relativeWeights.add(weight);
        totalRelativeWeight += weight;

        if(biggestColorWeight < weight){
            biggestColor = col;
            biggestColorWeight = weight;
        }
        if(smallestColorWeight > weight){
            smallestColor = col;
            smallestColorWeight = weight;
        }
    }

    public Color getColor(double percent) {
        double weightAcc = 0;
        int weightIdx = 0;

        while(weightAcc < percent && weightIdx < relativeWeights.size()-1){
            weightAcc += getNormalizedWeightByIndex(weightIdx);
            weightIdx++;
        }

        return colors.get(weightIdx);
    }

    public Color getColorByIndex(int index){
        return colors.get(index);
    }

    public int getNumColors(){
        return colors.size();
    }

    public Color[] getAllColors(){
        return colors.toArray(new Color[0]);
    }

    public double[] getRelativeWeights(){
        final double[] doubleArr = new double[relativeWeights.size()];
        for(int i = 0; i < relativeWeights.size(); i++){
            doubleArr[i] = relativeWeights.get(i);
        }
        return doubleArr;
    }

    public double getRelativeWeight(double percent){
        double weightAcc = 0;
        int weightIdx = 0;

        while(weightAcc < percent && weightIdx < relativeWeights.size()){
            weightAcc += relativeWeights.get(weightIdx);
            weightIdx++;
        }

        return relativeWeights.get(weightIdx);
    }

    /**
     * Finds the weight of the color
     * @return weight of the color, or -1 if the color does not exist in this palette
     */
    public double getRelativeWeight(Color color){
        for(int i = 0; i < colors.size(); i++){
            if(colors.get(i).equals(color)){
                return relativeWeights.get(i);
            }
        }
        return -1;
    }

    public double getRelativeWeightByIndex(int index){
        return relativeWeights.get(index);
    }

    /**
     * Computes the normalized weight of the color at the given percent. Weights are normalized so that all color relativeWeights add to 1.
     */
    public double getNormalizedWeight(double percent){
        return getRelativeWeight(percent) / totalRelativeWeight;
    }

    /**
     * Computes the normalized weight of the color. Weights are normalized so that all color relativeWeights add to 1.
     */
    public double getNormalizedWeight(Color color){
        return getRelativeWeight(color) / totalRelativeWeight;
    }

    /**
     * Computes the normalized weight at the index. Weights are normalized so that all color relativeWeights add to 1.
     */
    public double getNormalizedWeightByIndex(int index){
        return relativeWeights.get(index) / totalRelativeWeight;
    }

    /**
     * Sum of the relative weight of all colors in this palette
     */
    public double getTotalRelativeWeight(){
        return totalRelativeWeight;
    }

    /**
     * @return the color with the largest weight
     */
    public Color getBiggestColor(){
        return biggestColor;
    }

    /**
     * @return the color with the smallest weight
     */
    public Color getSmallestColor(){
        return smallestColor;
    }
}
