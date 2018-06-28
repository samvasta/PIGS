package com.samvasta.imageGenerator.common.particlefield;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ParticleSimulator
{
    private ParticleField field;
    private List<Particle> particles;

    public ParticleSimulator(){
        particles = new ArrayList<>();
    }

    public void simulate(long numTicks, Graphics2D g){
        if(field == null){
            throw new NullPointerException("field has not been initialized");
        }
        if(numTicks == 0){
            return;
        }

        for(long currentTick = 0; currentTick < numTicks; currentTick++){
            for(Particle particle : particles){
                particle.tick(field);
                particle.draw(currentTick, field, g);
            }
        }
    }

    public void addParticle(Particle particle){
        particles.add(particle);
    }

    public List<Particle> getParticles(){
        return particles;
    }

    public void setParticleField(ParticleField field){
        this.field = field;
    }

    public ParticleField getParticleField(){
        return field;
    }
}
