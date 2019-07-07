package com.samvasta.imageGenerator.common.models.parametricfunctions;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Takes multiple functions and adds them together
 */
public class CompositeFunction implements IParametricFunction {

    private final List<IParametricFunction> functionsList;

    public CompositeFunction(IParametricFunction...functions){
        functionsList = new ArrayList<>();
        functionsList.addAll(Arrays.asList(functions));
    }

    public void addFunction(IParametricFunction func){
        functionsList.add(func);
    }

    @Override
    public Point2D get(double time) {
        double x = 0;
        double y = 0;

        for(IParametricFunction func : functionsList){
            Point2D point = func.get(time);
            x += point.getX();
            y += point.getY();
        }

        return new Point2D.Double(x, y);
    }
}
