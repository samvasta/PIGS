package com.samvasta.imageGenerator.common.particlefield;

import javax.vecmath.Vector2d;
import java.awt.*;

public abstract class Particle
{
    private Vector2d lastPosition;
    private Vector2d lastVelocity;
    private Vector2d lastAcceleration;

    protected Vector2d position;
    protected Vector2d velocity;
    protected Vector2d acceleration;

    private boolean isActive = true;

    public Particle(){
        this(new Vector2d(0,0), new Vector2d(0,0), new Vector2d(0,0));
    }

    public Particle(Vector2d initialPosition){
        this(initialPosition, new Vector2d(0,0), new Vector2d(0,0));
    }

    public Particle(Vector2d initialPosition, Vector2d initialVelocity){
        this(initialPosition, initialVelocity, new Vector2d(0,0));
    }

    public Particle(Vector2d initialPosition, Vector2d initialVelocity, Vector2d initialAcceleration){
        position = initialPosition;
        velocity = initialVelocity;
        acceleration = initialAcceleration;
    }

    public abstract void draw(long timeStep, ParticleField field, Graphics2D g);

    public final void tick(ParticleField field){
        if(!isActive){
            return;
        }

        lastAcceleration = new Vector2d(acceleration);
        lastVelocity = new Vector2d(velocity);
        lastPosition = new Vector2d(position);


        ParticleFieldValue fieldValue = field.getValue(position.x, position.y);
        velocity.setX(fieldValue.influenceMagnitude * Math.cos(fieldValue.influenceAngle));
        velocity.setY(fieldValue.influenceMagnitude * Math.sin(fieldValue.influenceAngle));

        //velocity.add(acceleration);
        position.add(velocity);

        int fieldWidth = field.getWidth();
        int fieldHeight = field.getHeight();

        if(position.x > fieldWidth){
            if(field.isEdgeWrap())
            {
                position.x -= fieldWidth;
            }
            else{
                isActive = false;
            }
        }
        else if(position.x < 0){
            if(field.isEdgeWrap()){
                position.x += fieldWidth;
            }
            else{
                isActive = false;
            }
        }
        if(position.y > fieldHeight){
            if(field.isEdgeWrap()){
                position.y -= fieldHeight;
            }
            else{
                isActive = false;
            }
        }
        else if(position.y < 0){
            if(field.isEdgeWrap()){
                position.y += fieldHeight;
            }
            else{
                isActive = false;
            }
        }

        afterTick(field);
    }

    public void afterTick(ParticleField field){
        //no logic by default
    }

    public void updateIsActive(ParticleField field){
        if(getPositionX() < 0 || getPositionX() > field.getWidth() ||
                getPositionY() < 0 || getPositionY() > field.getHeight()){
            isActive = false;
        }
    }

    public double getLastPositionX()
    {
        return lastPosition.x;
    }

    public double getLastPositionY()
    {
        return lastPosition.y;
    }

    public double getLastVelocityX()
    {
        return lastVelocity.x;
    }

    public double getLastVelocityY()
    {
        return lastVelocity.y;
    }

    public double getLastAccelerationX()
    {
        return lastAcceleration.x;
    }

    public double getLastAccelerationY()
    {
        return lastAcceleration.y;
    }

    public double getPositionX()
    {
        return position.x;
    }

    public double getPositionY()
    {
        return position.y;
    }

    public double getVelocityX()
    {
        return velocity.x;
    }

    public double getVelocityY()
    {
        return velocity.y;
    }

    public double getAccelerationX()
    {
        return acceleration.x;
    }

    public double getAccelerationY()
    {
        return acceleration.y;
    }

    public boolean isActive(){
        return isActive;
    }
}
