package com.samvasta.generatorpack1.it;

import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.debuggenerator.GeneratorWindow;
import com.samvasta.imagegenerator.generatorpack1.landscape.LandscapeGenerator;
import com.samvasta.imagegenerator.generatorpack1.samplegenerator.SimpleGenerator;

import javax.swing.*;
import java.awt.*;

public class DebugGenerator
{

    public static void main(String...args){
        IGenerator generator = new LandscapeGenerator();

        GeneratorWindow window = new GeneratorWindow(generator, new Dimension(2560, 1440));

        window.setSize(1920, 1080);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
