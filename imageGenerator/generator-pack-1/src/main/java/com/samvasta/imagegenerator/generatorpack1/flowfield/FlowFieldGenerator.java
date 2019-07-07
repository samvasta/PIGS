package com.samvasta.imagegenerator.generatorpack1.flowfield;

import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.models.PolarVector;
import com.samvasta.imageGenerator.common.noise.NoiseHelper;
import com.samvasta.imageGenerator.common.noise.fastnoise.FastNoise;
import com.samvasta.imageGenerator.common.particlefield.Particle;
import com.samvasta.imageGenerator.common.particlefield.ParticleField;
import com.samvasta.imageGenerator.common.particlefield.ParticleSimulator;
import org.apache.commons.math3.random.MersenneTwister;

import javax.vecmath.Vector2d;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlowFieldGenerator implements IGenerator {
    private List<ISnapshotListener> snapshotListeners = new ArrayList<>();

    @Override
    public boolean isOnByDefault() {
        return false;
    }

    @Override
    public List<IniSchemaOption<?>> getIniSettings() {
        return new ArrayList<>();
    }

    @Override
    public boolean isMultiThreadEnabled() {
        return true;
    }

    @Override
    public void addSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.add(listener);
    }

    @Override
    public void removeSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.remove(listener);
    }

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {
        final int numTimeSteps = 500;
        final int numParticles = imageSize.width/10;
        final int fieldScale = 3;
        final int fieldWidth = imageSize.width/fieldScale;
        final int fieldHeight = imageSize.height/fieldScale;
        ParticleField field = new ParticleField(fieldWidth, fieldHeight);
        FastNoise noise = NoiseHelper.getFractalSimplex(random, 10);

        PolarVector[] vecs = new PolarVector[fieldWidth*fieldHeight];
        for(int w = 0; w < fieldWidth; w++){
            for(int h = 0; h < fieldHeight; h++){
                double p = (double)h / fieldHeight;
                float scale = (float)(0.5 + 0.5*Math.cos(p*12*Math.PI) + 4*Math.sin(0.0625*p));// (float)Math.exp(p-1);
                float dir = (float)(Math.PI * (noise.getSimplexFractal(w, h))) * scale;
                dir += (float)(Math.PI / 2.0);
                PolarVector vec = new PolarVector(dir, 3*(scale)+2);
                vecs[fieldWidth*h + w] = vec;
            }
        }
        field.setFieldValues(vecs);
        field.setEdgeWrap(false);

        ParticleSimulator sim = new ParticleSimulator();
        sim.setParticleField(field);
        for(int i = 0; i < numParticles; i++){
            Vector2d initialPosition = new Vector2d(fieldWidth * ((float)i/numParticles), 0);
            Vector2d intialVelocity = new Vector2d(0, 1);
            Particle p = new Particle(initialPosition, intialVelocity, new Vector2d(0,1)){

                @Override
                public void draw(long timeStep, ParticleField field, Graphics2D g) {
                    g.setColor(ColorUtil.blend(Color.WHITE, Color.BLACK, (float)timeStep/numTimeSteps));
                    int pX = (int)(getLastPositionX()*fieldScale);
                    int pY = (int)(getLastPositionY()*fieldScale);
                    int cX = (int)(getPositionX() * fieldScale);
                    int cY = (int)(getPositionY() * fieldScale);
                    g.drawLine(pX, pY, cX, cY);
                }
            };
            sim.addParticle(p);
        }

//        g.setColor(Color.BLACK);
        g.fillRect(0,0,imageSize.width, imageSize.height);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        sim.simulate(numTimeSteps, g);

    }
}
