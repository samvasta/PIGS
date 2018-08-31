package com.samvasta.imageGenerator.common.models;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.geom.Point2D;
import java.math.BigDecimal;

public class Transform2D
{
    private static final Logger LOGGER = Logger.getLogger(Transform2D.class);
    /*
     * Linear Transformations
     */
    private BigDecimal translateX;
    private BigDecimal translateY;

    private BigDecimal shearX;
    private BigDecimal shearY;

    private BigDecimal scaleX;
    private BigDecimal scaleY;

    private BigDecimal rotationAngle;


    private BigDecimal m00, m10, m01, m11;
    private boolean isLinearTransformFinalized;

    public Transform2D(){
        translateX = new BigDecimal(0.0);
        translateY = new BigDecimal(0.0);

        shearX = new BigDecimal(0.0);
        shearY = new BigDecimal(0.0);
        scaleX = new BigDecimal(1.0);
        scaleY = new BigDecimal(1.0);

        rotationAngle = new BigDecimal(0.0);
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
        BigDecimal cosRotation = new BigDecimal(Math.cos(rotationAngle.doubleValue()));
        BigDecimal sinRotation = new BigDecimal(Math.sin(rotationAngle.doubleValue()));
        m00 = cosRotation.subtract(shearY.multiply(sinRotation));
        m01 = shearX.multiply(cosRotation).subtract(sinRotation);
        m10 = sinRotation.add(shearY.multiply(cosRotation));
        m11 = shearX.multiply(sinRotation).add(cosRotation);

        //(rotate X shear) X scale
        m00 = scaleX.multiply(m00);
        m01 = scaleY.multiply(m01);
        m10 = scaleX.multiply(m10);
        m11 = scaleY.multiply(m11);

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
    public final PrecisePoint2D transform(PrecisePoint2D src){
        BigDecimal x = src.getPreciseX();
        BigDecimal y = src.getPreciseY();


        if(!isLinearTransformFinalized){
            LOGGER.warn("For best performance, finalizeLinearTransform() should be called on all Transform2D objects" +
                    "before applying the transformation");
            BigDecimal origX;
            BigDecimal origY;

            //scale
            origX = x;
            origY = y;
            x = origX.multiply(scaleX);
            y = origY.multiply(scaleY);

            //shear
            origX = x;
            origY = y;
            x = x.add(shearX.multiply(origY));
            y = y.add(shearY.multiply(origX));

            //rotate
            origX = x;
            origY = y;
            BigDecimal cosRotation = new BigDecimal(Math.cos(rotationAngle.doubleValue()));
            BigDecimal sinRotation = new BigDecimal(Math.sin(rotationAngle.doubleValue()));
            x = origX.multiply(cosRotation).subtract(origY.multiply(sinRotation));
            y = origX.multiply(sinRotation).add(origY.multiply(cosRotation));

            //translate
            origX = x;
            origY = y;
            x = origX.add(translateX);
            y = origY.add(translateY);
        }
        else{
            BigDecimal origX = x;
            BigDecimal origY = y;
            x = m00.multiply(origX).add(m01.multiply(origY)).add(translateX);
            y = m10.multiply(origX).add(m11.multiply(origY)).add(translateY);
        }


        return transformNonLinear(x, y);
    }

    /**
     * Transforms multiple points. See {@link #transform(PrecisePoint2D)}
     */
    public final PrecisePoint2D[] transform(PrecisePoint2D[] src){
        PrecisePoint2D[] dest = new PrecisePoint2D[src.length];
        for(int i = 0; i < src.length; i++){
            dest[i] = transform(src[i]);
        }
        return dest;
    }

    protected PrecisePoint2D transformNonLinear(BigDecimal x, BigDecimal y){
        //Base class implements a strictly linear transformation. Derivative classes may not be linear.
        return new PrecisePoint2D(x, y);
    }

    public void setTranslation(double tx, double ty){
        translateX = new BigDecimal(tx);
        translateY = new BigDecimal(ty);
        isLinearTransformFinalized = false;
    }

    public void setTranslation(BigDecimal tx, BigDecimal ty){
        translateX = tx;
        translateY = ty;
        isLinearTransformFinalized = false;
    }

    public void setScale(double sx, double sy){
        scaleX = new BigDecimal(sx);
        scaleY = new BigDecimal(sy);
        isLinearTransformFinalized = false;
    }

    public void setShear(double sx, double sy){
        shearX = new BigDecimal(sx);
        shearY = new BigDecimal(sy);
        isLinearTransformFinalized = false;
    }

    public double getTranslateX()
    {
        return translateX.doubleValue();
    }

    public void setTranslateX(double translateX)
    {
        this.translateX = new BigDecimal(translateX);
        isLinearTransformFinalized = false;
    }

    public double getTranslateY()
    {
        return translateY.doubleValue();
    }

    public void setTranslateY(double translateY)
    {
        this.translateY = new BigDecimal(translateY);
        isLinearTransformFinalized = false;
    }

    public double getShearX()
    {
        return shearX.doubleValue();
    }

    public void setShearX(double shearX)
    {
        this.shearX = new BigDecimal(shearX);
        isLinearTransformFinalized = false;
    }

    public double getShearY()
    {
        return shearY.doubleValue();
    }

    public void setShearY(double shearY)
    {
        this.shearY = new BigDecimal(shearY);
        isLinearTransformFinalized = false;
    }

    public double getScaleX()
    {
        return scaleX.doubleValue();
    }

    public void setScaleX(double scaleX)
    {
        this.scaleX = new BigDecimal(scaleX);
        isLinearTransformFinalized = false;
    }

    public double getScaleY()
    {
        return scaleY.doubleValue();
    }

    public void setScaleY(double scaleY)
    {
        this.scaleY = new BigDecimal(scaleY);
        isLinearTransformFinalized = false;
    }

    public double getRotationAngle()
    {
        return rotationAngle.doubleValue();
    }

    public void setRotationAngle(double rotationAngle)
    {
        this.rotationAngle = new BigDecimal(rotationAngle);
        isLinearTransformFinalized = false;
    }
}
