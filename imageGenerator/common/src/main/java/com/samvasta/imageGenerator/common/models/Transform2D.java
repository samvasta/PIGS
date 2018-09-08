package com.samvasta.imageGenerator.common.models;

import org.apache.log4j.Logger;

import java.awt.geom.Point2D;
import java.math.BigDecimal;

public class Transform2D
{
    private static final Logger LOGGER = Logger.getLogger(Transform2D.class);
    /*
     * Linear Transformations
     */
    private double translateX;
    private double translateY;

    private double shearX;
    private double shearY;

    private double scaleX;
    private double scaleY;

    private double rotationAngle;


    private double m00, m10, m01, m11;
    private boolean isLinearTransformFinalized;

    public Transform2D(){
        translateX = 0.0;
        translateY = 0.0;

        shearX = 0.0;
        shearY = 0.0;
        scaleX = 1.0;
        scaleY = 1.0;

        rotationAngle = 0.0;
        isLinearTransformFinalized = false;
    }

    public Transform2D(Transform2D transformToCopy){
        this.translateX = transformToCopy.translateX;
        this.translateY = transformToCopy.translateY;

        this.shearX = transformToCopy.shearX;
        this.shearY = transformToCopy.shearY;

        this.scaleX = transformToCopy.scaleX;
        this.scaleY = transformToCopy.scaleY;

        this.rotationAngle = transformToCopy.rotationAngle;
        isLinearTransformFinalized = false;
    }

    public final void finalizeLinearTransform(){
        //rotate X shear X scale

        //rotate X shear
        double cosRotation = Math.cos(rotationAngle);
        double sinRotation = Math.sin(rotationAngle);
        m00 = cosRotation - (shearY * sinRotation);
        m01 = (shearX * cosRotation) - sinRotation;
        m10 = sinRotation + (shearY * cosRotation);
        m11 = (shearX * sinRotation) + cosRotation;

        //(rotate X shear) X scale
        m00 = scaleX * m00;
        m01 = scaleY * m01;
        m10 = scaleX * m10;
        m11 = scaleY * m11;

        isLinearTransformFinalized = true;
    }

    /**
     * Transforms a point using linear and non-linear transformation variables. The order of transformations is
     * as follows:<br>
     *     <ol>
     *         <li>Affine Transformations
     *              <ol>
     *                  <li>Scale</li>
     *                  <li>Shear</li>
     *                  <li>Rotate</li>
     *                  <li>Translate</li>
     *              </ol>
     *         </li>
     *         <li>Any Non-linear Transformation</li>
     *     </ol>
     */
    public final Point2D.Double transform(Point2D.Double src){
        double x = src.getX();
        double y = src.getY();


        if(!isLinearTransformFinalized){
            LOGGER.warn("For best performance, finalizeLinearTransform() should be called on all Transform2D objects" +
                    "before applying the transformation");
            double origX;
            double origY;

            //scale
            origX = x;
            origY = y;
            x = origX * scaleX;
            y = origY * scaleY;

            //shear
            origX = x;
            origY = y;
            x = x + (shearX * origY);
            y = y + (shearY * origX);

            //rotate
            origX = x;
            origY = y;
            double cosRotation = Math.cos(rotationAngle);
            double sinRotation = Math.sin(rotationAngle);
            x = origX * (cosRotation) - (origY * sinRotation);
            y = origX * (sinRotation) + (origY * cosRotation);

            //translate
            origX = x;
            origY = y;
            x = origX + translateX;
            y = origY + translateY;
        }
        else{
            double origX = x;
            double origY = y;
            x = m00 * (origX) + (m01 * (origY)) + (translateX);
            y = m10 * (origX) + (m11 * (origY)) + (translateY);
        }


        return transformNonLinear(x, y);
    }

    /**
     * Transforms multiple points. See {@link #transform(Point2D.Double)}
     */
    public final Point2D.Double[] transform(Point2D.Double[] src){
        Point2D.Double[] dest = new Point2D.Double[src.length];
        for(int i = 0; i < src.length; i++){
            dest[i] = transform(src[i]);
        }
        return dest;
    }

    protected Point2D.Double transformNonLinear(double x, double y){
        //Base class implements a strictly linear transformation. Derivative classes may not be linear.
        return new Point2D.Double(x, y);
    }

    public void setTranslation(double tx, double ty){
        translateX = tx;
        translateY = ty;
        isLinearTransformFinalized = false;
    }

    public void setTranslation(BigDecimal tx, BigDecimal ty){
        translateX = tx.doubleValue();
        translateY = ty.doubleValue();
        isLinearTransformFinalized = false;
    }

    public void setScale(double sx, double sy){
        scaleX = sx;
        scaleY = sy;
        isLinearTransformFinalized = false;
    }

    public void setShear(double sx, double sy){
        shearX = sx;
        shearY = sy;
        isLinearTransformFinalized = false;
    }

    public double getTranslateX()
    {
        return translateX;
    }

    public void setTranslateX(double translateX)
    {
        this.translateX = translateX;
        isLinearTransformFinalized = false;
    }

    public double getTranslateY()
    {
        return translateY;
    }

    public void setTranslateY(double translateY)
    {
        this.translateY = translateY;
        isLinearTransformFinalized = false;
    }

    public double getShearX()
    {
        return shearX;
    }

    public void setShearX(double shearX)
    {
        this.shearX = shearX;
        isLinearTransformFinalized = false;
    }

    public double getShearY()
    {
        return shearY;
    }

    public void setShearY(double shearY)
    {
        this.shearY = shearY;
        isLinearTransformFinalized = false;
    }

    public double getScaleX()
    {
        return scaleX;
    }

    public void setScaleX(double scaleX)
    {
        this.scaleX = scaleX;
        isLinearTransformFinalized = false;
    }

    public double getScaleY()
    {
        return scaleY;
    }

    public void setScaleY(double scaleY)
    {
        this.scaleY = scaleY;
        isLinearTransformFinalized = false;
    }

    public double getRotationAngle()
    {
        return rotationAngle;
    }

    public void setRotationAngle(double rotationAngle)
    {
        this.rotationAngle = rotationAngle;
        isLinearTransformFinalized = false;
    }
}
