package com.samvasta.imagegenerator.microservice;

import com.google.gson.Gson;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    public static final Logger logger = Logger.getLogger(Server.class);

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        Spark.port(port);

        final Gson gson = new Gson();
        final MersenneTwister random = new MersenneTwister(System.currentTimeMillis());

        Spark.get(
                "/list-generators",
                (Request req, Response res) -> {
                    String json = gson.toJson(GeneratorFactory.GENERATOR_STRING_OPTIONS);
                    res.type("application/json");
                    return json;
                }
        );

        Spark.get(
                "/options/:generatorname",
                (Request req, Response res) -> {
                    String generatorName = req.params(":generatorname");
                    IGenerator generator = GeneratorFactory.getGenerator(generatorName);
                    if (generator == null) {
                        res.status(409);
                        res.type("application/json");
                        return gson.toJson(new Object(){public String[] validOptions = GeneratorFactory.GENERATOR_STRING_OPTIONS;});
                    }

                    return "";
                }
        );

        // [START cloudrun_system_package_handler]
        // [START run_system_package_handler]
        Spark.get(
                "/generate/:generatorname/:dimension",
                (Request req, Response res) -> {
                    String generatorName = req.params(":generatorname");
                    IGenerator generator = GeneratorFactory.getGenerator(generatorName);
                    if (generator == null) {
                        res.status(409);
                        res.type("application/json");
                        return gson.toJson(new Object(){public String[] validOptions = GeneratorFactory.GENERATOR_STRING_OPTIONS;});
                    }

                    String dimension = req.params(":dimension");
                    Dimension imageSize = parseDimension(dimension);
                    if(imageSize == null) {
                        res.status(400);
                        res.type("application/json");
                        return gson.toJson("Invalid dimension. Expected string like \"123x456\"");
                    }

                    Map<String, Object> settings = new HashMap<>();

                    Image img;
                    try {
                        img = createImage(generator, imageSize, settings, random);
                        res.type("image/png");
                    } catch (Exception e) {
                        res.status(500);
                        return "Internal Server Error";
                    }
                    return img;
                });
        // [END run_system_package_handler]
        // [END cloudrun_system_package_handler]
    }

    // [START cloudrun_system_package_exec]
    // [START run_system_package_exec]
    // Generate a diagram based on a graphviz DOT diagram description.
    public static BufferedImage createImage(IGenerator generator, Dimension imageSize, Map<String, Object> settings, MersenneTwister random) {
        Graphics2D g = null;
        try{
            BufferedImage mainImage = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_ARGB);
            g = (Graphics2D)mainImage.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            generator.generateImage(settings, g, imageSize, random);

            return mainImage;
        }
        catch(Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
        finally{
            if(g != null){
                g.dispose();
            }
        }
    }
    // [END run_system_package_exec]
    // [END cloudrun_system_package_exec]

    private static final Pattern dimensionPattern = Pattern.compile("(\\d+)[x|X](\\d+)");
    private static final Dimension parseDimension(String dimensionStr) {
        try {
            Matcher matcher = dimensionPattern.matcher(dimensionStr);
            String widthStr = matcher.group(0);
            String heightStr = matcher.group(1);

            int width = Integer.parseInt(widthStr);
            int height = Integer.parseInt(heightStr);
            return new Dimension(width, height);
        } catch (Exception e) {
            return null;
        }
    }
}
