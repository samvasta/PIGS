package com.samvasta.generatorpack1.it;

import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.debuggenerator.GeneratorWindow;
import com.samvasta.imagegenerator.generatorpack1.bezier.BezierGenerator;

import javax.swing.*;
import java.awt.*;

public class DebugGenerator
{

    public static void main(String...args){
        IGenerator generator = new BezierGenerator();

        GeneratorWindow window = new GeneratorWindow(generator, new Dimension(1920, 1080));

        window.setSize(1920, 1080);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
