package com.samvasta.imagegenerator.microservice;

import com.google.gson.Gson;
import com.samvasta.imageGenerator.common.helpers.IniHelper;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Spark;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    public static final Logger logger = Logger.getLogger(Server.class);

    public static final Gson gson = new Gson();
    public static final String INVALID_GENERATOR_NAME_RESPONSE = "{\"validNames\":" + gson.toJson(GeneratorFactory.GENERATOR_STRING_OPTIONS) + "}";
    public static final String INVALID_DIMENSION_FORMAT_RESPONSE = gson.toJson("Invalid dimension. Expected string like \"123x456\"");
    public static final String MISSING_DIMENSIONS_RESPONSE = "Missing dimensions in query. Expected something like \"/generate/<generator-name>/500x500\"";

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        Spark.port(port);

        final MersenneTwister random = new MersenneTwister(System.currentTimeMillis());

        Spark.get("/", (req, res) -> "Hello world");
        System.out.println("Server listening on port " + Spark.port());

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
                        return INVALID_GENERATOR_NAME_RESPONSE;
                    }

                    List<GeneratorOptionDetails> optionsList = new ArrayList<>();
                    List<IniSchemaOption<?>> options = generator.getIniSettings();
                    for(IniSchemaOption<?> option : options){
                        optionsList.add(new GeneratorOptionDetails(option));
                    }
                    return gson.toJson(optionsList);
                }
        );

        Spark.get(
                "/generate/:generatorname",
                (Request req, Response res) -> {
                    String generatorName = req.params(":generatorname");
                    IGenerator generator = GeneratorFactory.getGenerator(generatorName);
                    if (generator == null) {
                        res.status(409);
                        res.type("application/json");
                        return INVALID_GENERATOR_NAME_RESPONSE;
                    }

                    res.status(409);
                    return MISSING_DIMENSIONS_RESPONSE;
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
                        return INVALID_GENERATOR_NAME_RESPONSE;
                    }

                    String dimension = req.params(":dimension");
                    Dimension imageSize = parseDimension(dimension);
                    if(imageSize == null) {
                        res.status(400);
                        res.type("application/json");
                        return INVALID_DIMENSION_FORMAT_RESPONSE;
                    }

                    //Build options
                    Map<String, Object> settings = new HashMap<>();
                    List<IniSchemaOption<?>> options = generator.getIniSettings();

                    Map<String, String> queryParams = new HashMap<>();
                    for(String key : req.queryParams()) {
                        queryParams.put(key.toLowerCase(), req.queryParams(key));
                    }

                    for(IniSchemaOption<?> option : options){
                        String safeOptionName = GeneratorOptionDetails.getUrlSafeOptionName(option);
                        if(queryParams.containsKey(safeOptionName)) {
                            String paramValue = queryParams.get(safeOptionName);
                            settings.put(option.getOptionName(), toObject(option.getValueType(), paramValue));
                        }
                        else {
                            settings.put(option.getOptionName(), option.getDefaultValue());
                        }
                    }

                    //Generate image
                    byte[] imgBytes;
                    try {
                        BufferedImage img = createImage(generator, imageSize, settings, random);
                        try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                            ImageIO.write(img, "png", stream );

                            stream.flush();
                            imgBytes = stream.toByteArray();
                        }
                        res.type("image/png");
                    } catch (Exception e) {
                        res.status(500);
                        return "Internal Server Error";
                    }

                    return imgBytes;
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

    private static final Pattern dimensionPattern = Pattern.compile("(\\d+)[xX](\\d+)");
    private static final Dimension parseDimension(String dimensionStr) {
        try {
            Matcher matcher = dimensionPattern.matcher(dimensionStr);
            if(matcher.find()){
                String widthStr = matcher.group(1);
                String heightStr = matcher.group(2);

                int width = Integer.parseInt(widthStr);
                int height = Integer.parseInt(heightStr);
                return new Dimension(width, height);
            }
            //yay antipatterns!
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            //yay antipatterns!
            return null;
        }
    }

    //Shameless rip
    public static Object toObject( Class clazz, String value ) {
        if( Boolean.class == clazz ) return Boolean.parseBoolean( value );
        if( Byte.class == clazz ) return Byte.parseByte( value );
        if( Short.class == clazz ) return Short.parseShort( value );
        if( Integer.class == clazz ) return Integer.parseInt( value );
        if( Long.class == clazz ) return Long.parseLong( value );
        if( Float.class == clazz ) return Float.parseFloat( value );
        if( Double.class == clazz ) return Double.parseDouble( value );
        return value;
    }
}
