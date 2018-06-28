package com.samvasta.imagegenerator.debuggenerator.it;

import com.samvasta.imageGenerator.common.graphics.colors.ColorPalette;
import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.graphics.colors.palettes.MonochromePalette;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.interfaces.ISnapshotListener;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.models.PolarVector;
import com.samvasta.imageGenerator.common.noise.fastnoise.FastNoise;
import com.samvasta.imageGenerator.common.particlefield.Particle;
import com.samvasta.imageGenerator.common.particlefield.ParticleField;
import com.samvasta.imageGenerator.common.particlefield.ParticleFieldValue;
import com.samvasta.imageGenerator.common.particlefield.ParticleSimulator;
import org.apache.commons.math3.random.MersenneTwister;

import javax.vecmath.Vector2d;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParticleFieldTest implements IGenerator
{
    @Override
    public boolean isOnByDefault()
    {
        return false;
    }

    @Override
    public List<IniSchemaOption<?>> getIniSettings()
    {
        return new ArrayList<>();
    }

    @Override
    public boolean isMultiThreadEnabled()
    {
        return false;
    }

    @Override
    public void addSnapshotListener(ISnapshotListener listener)
    {
        //don't take snapshots. It's just a test
    }

    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, final MersenneTwister random)
    {
        int pFieldWidth = imageSize.width / 5;
        int pFieldHeight = imageSize.height / 5;

        final double scalarW = (double)imageSize.width / (double)pFieldWidth;
        final double scalarH = (double)imageSize.height / (double)pFieldHeight;
        PolarVector[] pFieldValues = new PolarVector[pFieldHeight * pFieldWidth];

        FastNoise angleNoise = new FastNoise(random.nextInt());
        FastNoise magnitudeNoise = new FastNoise(random.nextInt());

        for(int x = 0; x < pFieldWidth; x++){
            for(int y = 0; y < pFieldHeight; y++){
                pFieldValues[x + y * pFieldWidth] = new PolarVector(angleNoise.GetSimplex(x*0.75f, y*0.75f) * 2.0 * Math.PI, (magnitudeNoise.GetSimplex(x*0.5f, y*0.5f) + 1.0));
            }
        }

        ParticleField field = new ParticleField(pFieldWidth, pFieldHeight);
        field.setEdgeWrap(false);

        field.setFieldValues(pFieldValues);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, imageSize.width, imageSize.height);

//        drawField(field, g, imageSize);
        ParticleSimulator simulator = new ParticleSimulator();
        simulator.setParticleField(field);

        final ColorPalette palette = new MonochromePalette(20, random);

        for(int i = 0; i < 125; i++){

        Particle p = new Particle(new Vector2d(pFieldWidth * random.nextDouble(), pFieldHeight* random.nextDouble()), new Vector2d(0, 0))
        {
            Color c = palette.getColor(random.nextDouble());
            @Override
            public void draw(long timeStep, ParticleField field, Graphics2D g)
            {
                g.setColor(c);
//                g.drawOval((int)(getPositionX() * scalarW)-6, (int)(getPositionY() * scalarH)-6, 11, 11);
                int diameter = 5 + (int)Math.round(Math.sqrt(timeStep * 2));
                g.setColor(ColorUtil.getTransparent(c, 15));
//                g.fillOval((int)(getPositionX() * scalarW)-diameter/2, (int)(getPositionY() * scalarH)-diameter/2, diameter, diameter);
                g.setStroke(new BasicStroke(diameter));
                g.drawLine((int)(getPositionX() * scalarW)-diameter/2, (int)(getPositionY() * scalarH)-diameter/2, (int)(getLastPositionX() * scalarW)-diameter/2, (int)(getLastPositionY() * scalarH)-diameter/2);
            }

            @Override
            public void afterTick(ParticleField field){
                if(acceleration.lengthSquared() > 1){
                    acceleration.normalize();
                }
            }
        };
        simulator.addParticle(p);
        }

        simulator.simulate(500, g);
    }

    private void drawField(ParticleField field, Graphics2D g, Dimension destSize){
        int fieldWidth = field.getWidth();
        int fieldHeight = field.getHeight();

        double scalarW = (double)destSize.width / (double)fieldWidth;
        double scalarH = (double)destSize.height / (double)fieldHeight;

        for(double x = 0; x < destSize.width; x += scalarW){
            for(double y = 0; y < destSize.height; y += scalarH){
                ParticleFieldValue val = field.getValue(x / scalarW, y / scalarH);
                double angle = val.influenceAngle;
                double length = val.influenceMagnitude * Math.sin(angle) * scalarH + val.influenceMagnitude * Math.cos(angle) * scalarW;

                float b = (float)val.influenceMagnitude /2f;
                g.setColor(Color.getHSBColor(0.3f, 0.5f, b));
                g.drawLine((int)x, (int)y, (int)(x + 10 * length * Math.cos(angle)), (int)(y + 10 * length * Math.sin(angle)));
            }
        }
    }
}
