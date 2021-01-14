package com.samvasta.imagegenerator.microservice;

import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imagegenerator.generatorpack1.amoebas.AmoebaGenerator;
import com.samvasta.imagegenerator.generatorpack1.bezier.BezierGenerator;
import com.samvasta.imagegenerator.generatorpack1.circlewave.CircleWaveGenerator;
import com.samvasta.imagegenerator.generatorpack1.clippedhatching.ClippedHatchingGenerator;
import com.samvasta.imagegenerator.generatorpack1.flowfield.FlowFieldGenerator;
import com.samvasta.imagegenerator.generatorpack1.fog.FogGenerator;
import com.samvasta.imagegenerator.generatorpack1.landscape.LandscapeGenerator;
import com.samvasta.imagegenerator.generatorpack1.legacylandscape.LegacyLandscapeGenerator;
import com.samvasta.imagegenerator.generatorpack1.minspanningtree.MinSpanningTreeGenerator;
import com.samvasta.imagegenerator.generatorpack1.radialpolygons.RadialPolygonsGenerator;
import com.samvasta.imagegenerator.generatorpack1.tangles.TangleGenerator;
import com.samvasta.imagegenerator.generatorpack1.tessellation.TessellationGenerator;
import com.samvasta.imagegenerator.generatorpack1.triangulation.TriangulationGenerator;
import com.samvasta.imagegenerator.generatorpack1.wovengrid.WovenGridGenerator;
import org.apache.commons.math3.random.RandomGenerator;

public class GeneratorFactory {

    private static final String ALL_COLORS = "allcolors";
    private static final String AMOEBAS = "amoebas";
    private static final String BEZIER = "bezier";
    private static final String CIRCLE_WAVE = "circlewave";
    private static final String CLIPPED_HATCHING = "clippedhatching";
    private static final String FLOW_FIELD = "flowfield";
    private static final String FOG = "fog";
    private static final String LANDSCAPE = "landscape";
    private static final String LANDSCAPE_LEGACY = "landscapelegacy";
    private static final String MIN_SPANNING_TREE = "minspanningtree";
    private static final String RADIAL_POLYGONS = "radialpolygons";
    private static final String TANGLES = "tangles";
    private static final String TESSELLATION = "tessellation";
    private static final String TRIANGULATION = "triangulation";
    private static final String WOVEN_GRID = "wovengrid";

    public static final String RANDOM_GENERATOR_NAME = "random";

    public static final String[] GENERATOR_STRING_OPTIONS = {
//            ALL_COLORS,       // too expensive on CPU
            AMOEBAS,
            BEZIER,
            CIRCLE_WAVE,
            CLIPPED_HATCHING,
            FLOW_FIELD,
            FOG,
            LANDSCAPE,
            LANDSCAPE_LEGACY,
            MIN_SPANNING_TREE,
            RADIAL_POLYGONS,
            TANGLES,
            TESSELLATION,
            TRIANGULATION,
            WOVEN_GRID,
    };

    public static IGenerator getGenerator(String name, RandomGenerator random) {
        switch(name.toLowerCase()) {
//            case ALL_COLORS: return new AllColorsGenerator();
            case AMOEBAS: return new AmoebaGenerator();
            case BEZIER: return new BezierGenerator();
            case CIRCLE_WAVE: return new CircleWaveGenerator();
            case CLIPPED_HATCHING: return new ClippedHatchingGenerator();
            case FLOW_FIELD: return new FlowFieldGenerator();
            case FOG: return new FogGenerator();
            case LANDSCAPE: return new LandscapeGenerator();
            case LANDSCAPE_LEGACY: return new LegacyLandscapeGenerator();
            case MIN_SPANNING_TREE: return new MinSpanningTreeGenerator();
            case RADIAL_POLYGONS: return new RadialPolygonsGenerator();
            case TANGLES: return new TangleGenerator();
            case TESSELLATION: return new TessellationGenerator();
            case TRIANGULATION: return new TriangulationGenerator();
            case WOVEN_GRID: return new WovenGridGenerator();

            case RANDOM_GENERATOR_NAME: {
                return getGenerator(GENERATOR_STRING_OPTIONS[random.nextInt(GENERATOR_STRING_OPTIONS.length)], random);
            }

            //yay antipatterns!
            default: return null;
        }
    }

}
