package com.samvasta.imageGenerator.common.helpers;

import com.samvasta.imageGenerator.common.annotations.IgnoreGenerator;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarHelper
{

    public static void populateGeneratorTypesFromJar(List<Class<? extends IGenerator>> generatorList, String pathToJar) throws IOException {
        JarFile jarFile = new JarFile(pathToJar);
        Enumeration<JarEntry> entries = jarFile.entries();

        URL[] urls = { new URL("jar:file:" + pathToJar + "!/") };
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while(entries.hasMoreElements()){
            JarEntry je = entries.nextElement();
            if(je.isDirectory() || !je.getName().endsWith(".class")){
                continue;
            }

            //6 = length of ".class"
            String className = je.getName().substring(0, je.getName().length()-6).replace('/', '.');

            Class c;
            try{
                c = cl.loadClass(className);

            } catch (ClassNotFoundException e){
                continue;
            }

            if(IGenerator.class.isAssignableFrom(c) && !c.isAnnotationPresent(IgnoreGenerator.class)){
                generatorList.add((Class<? extends IGenerator>)c);
            }
        }
    }
}
