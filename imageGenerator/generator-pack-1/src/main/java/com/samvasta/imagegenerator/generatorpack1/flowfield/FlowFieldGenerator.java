package com.samvasta.imagegenerator.generatorpack1.flowfield;

import com.samvasta.imageGenerator.common.graphics.colors.CeiLchColor;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.models.PolarVector;
import com.samvasta.imageGenerator.common.models.parametricfunctions.IParametricFunction;
import com.samvasta.imageGenerator.common.noise.NoiseHelper;
import com.samvasta.imageGenerator.common.noise.fastnoise.FastNoise;
import com.samvasta.imageGenerator.common.particlefield.Particle;
import com.samvasta.imageGenerator.common.particlefield.ParticleField;
import com.samvasta.imageGenerator.common.particlefield.ParticleSimulator;
import org.apache.commons.math3.random.MersenneTwister;

import javax.vecmath.Vector2d;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlowFieldGenerator implements IGenerator {
    private List<ISnapshotListener> snapshotListeners = new ArrayList<>();

    @Override
    public boolean isOnByDefault() {
        return true;
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
        final int numParticles = imageSize.width/5;
        final int fieldScale = 3;
        final int fieldWidth = imageSize.width/fieldScale;
        final int fieldHeight = imageSize.height/fieldScale;
        final double fieldDirection = random.nextDouble()*Math.PI*2.0;

        ParticleField field = new ParticleField(fieldWidth, fieldHeight);
        FastNoise noise = NoiseHelper.getFractalSimplex(random, 10);

        PolarVector[] vecs = new PolarVector[fieldWidth*fieldHeight];
        for(int w = 0; w < fieldWidth; w++){
            for(int h = 0; h < fieldHeight; h++){
                float percent = (float)h / fieldHeight;
                float dir = (float)(Math.PI * (noise.getSimplexFractal(w, h, percent)) + fieldDirection);
                float scale = (noise.getSimplexFractal(percent,w,h)+1) + 2;

                PolarVector vec = new PolarVector(dir, scale);
                vecs[fieldWidth*h + w] = vec;
            }
        }
        field.setFieldValues(vecs);
        field.setEdgeWrap(false);

        ParticleSimulator sim = new ParticleSimulator();
        sim.setParticleField(field);

        CeiLchColor base = new CeiLchColor(80, 120, random.nextInt()*360);
        IParametricFunction initialPosFunction = ParametricFunctionUtil.getRandomFuction(random, (double)fieldWidth/fieldHeight);  //new EllipseFunction(0.125, 0.125 * ((double)fieldWidth/fieldHeight), 0.5, 0.5);
        for(int i = 0; i < numParticles; i++){
            float percent = (float)i/numParticles;

            Point2D pos = initialPosFunction.get(percent);
            Vector2d initialPosition = new Vector2d(pos.getX() * fieldWidth, pos.getY() * fieldHeight);

            base = base.add(0,0,360.0/numParticles);
            Color particleCol = ColorUtil.getTransparent(base.toColor(), 100);

            Particle p = new FlowFieldParticle(initialPosition, new Vector2d(), new Vector2d(), fieldScale, particleCol);
            sim.addParticle(p);
        }


        g.setColor(Color.BLACK);
        g.fillRect(0,0,imageSize.width, imageSize.height);
        g.setStroke(new BasicStroke(2));

        for(int i = 0; i < numTimeSteps; i++){
            sim.simulate(1, g);
            if(canRenderThisTimestep(i)){
                takeSnapshot();
            }
            if(!sim.areAnyParticlesActive()){
                break;
            }
        }

        g.setColor(Color.BLACK);
        g.fillRect(0,0,imageSize.width, imageSize.height);
        g.setStroke(new BasicStroke(2));

        for(Particle p : sim.getParticles()){
            FlowFieldParticle particle = (FlowFieldParticle)p;
            if(particle.points.size() < 1){
                continue;
            }

            g.setColor(particle.color);

            Path2D path = new Path2D.Double();

            Point2D point = particle.points.get(0);
            path.moveTo(point.getX(), point.getY());

            for(int i = 1; i < particle.points.size(); i++){
                point = particle.points.get(i);
                path.lineTo(point.getX(), point.getY());
            }

            g.draw(path);
        }

    }
    private boolean canRenderThisTimestep(long timestep){
        //less than 100, do at fibonacci intervals
        if(timestep < 15){
            return (timestep % 2) == 0;
        }
        if(timestep < 50){
            return (timestep % 5) == 0;
        }
        else if(timestep > 200){
            //else just to every 25
            return (timestep % 15) == 0;
        }
        //else just to every 25
        return (timestep % 25) == 0;
    }

    private void takeSnapshot(){
        for(ISnapshotListener listener : snapshotListeners){
            listener.takeSnapshot();
        }
    }

    private class FlowFieldParticle extends Particle{
        List<Point2D> points = new ArrayList<>();
        final double fieldScale;
        final Color color;

        private FlowFieldParticle(Vector2d pos, Vector2d vel, Vector2d acc, double fieldScale, Color color)
        {
            super(pos, vel, acc);
            this.fieldScale = fieldScale;
            this.color = color;
        }

        @Override
        public void draw(long timeStep, ParticleField field, Graphics2D g) {
            double pX = (getLastPositionX()*fieldScale);
            double pY = (getLastPositionY()*fieldScale);
            double cX = getPositionX() * fieldScale;
            double cY = getPositionY() * fieldScale;

            g.setColor(color);
            g.drawLine((int)pX, (int)pY, (int)cX, (int)cY);

            points.add(new Point2D.Double(pX, pY));

        }

    }
}
