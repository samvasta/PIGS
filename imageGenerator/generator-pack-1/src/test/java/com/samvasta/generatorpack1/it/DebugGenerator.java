package com.samvasta.generatorpack1.it;

import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.debuggenerator.GeneratorWindow;
import com.samvasta.imagegenerator.generatorpack1.amoebas.AmoebaGenerator;
import com.samvasta.imagegenerator.generatorpack1.bezier.BezierGenerator;
import com.samvasta.imagegenerator.generatorpack1.circlewave.CircleWaveGenerator;
import com.samvasta.imagegenerator.generatorpack1.flowfield.FlowFieldGenerator;
import com.samvasta.imagegenerator.generatorpack1.flowfield.WavyCircleFunction;
import com.samvasta.imagegenerator.generatorpack1.landscape.LandscapeGenerator;
import com.samvasta.imagegenerator.generatorpack1.samplegenerator.SimpleGenerator;
import com.samvasta.imagegenerator.generatorpack1.spacefiller.SpaceFillerGenerator;
import com.samvasta.imagegenerator.generatorpack1.tangles.TangleGenerator;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TessellationGenerator;
import com.samvasta.imagegenerator.generatorpack1.triangulation.TriangulationGenerator;
import com.samvasta.imagegenerator.generatorpack1.wovengrid.WovenGridGenerator;
import org.intellij.lang.annotations.Flow;

import javax.swing.*;
import java.awt.*;

public class DebugGenerator
{

    public static void main(String...args){
        IGenerator generator = new AmoebaGenerator();

        GeneratorWindow window = new GeneratorWindow(generator, new Dimension(2560, 1440));

        window.setSize(1920, 1080);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
