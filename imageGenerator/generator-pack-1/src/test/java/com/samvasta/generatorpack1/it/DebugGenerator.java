package com.samvasta.generatorpack1.it;

import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.debuggenerator.GeneratorWindow;
import com.samvasta.imagegenerator.generatorpack1.samplegenerator.SimpleGenerator;

import javax.swing.*;
import java.awt.*;

public class DebugGenerator
{

    public static void main(String...args){
        IGenerator generator = new SimpleGenerator();

        GeneratorWindow window = new GeneratorWindow(generator, new Dimension(1920, 1080));

        window.setSize(1600, 900);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
