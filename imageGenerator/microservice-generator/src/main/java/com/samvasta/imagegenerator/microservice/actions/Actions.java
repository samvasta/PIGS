package com.samvasta.imagegenerator.microservice.actions;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imagegenerator.microservice.GeneratorFactory;
import com.samvasta.imagegenerator.microservice.GeneratorOptionDetails;
import com.samvasta.imagegenerator.microservice.Server;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;
import spark.Request;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Actions {
    private Actions(){}


    public static final Logger logger = Logger.getLogger(Server.class);

    public static final Gson gson = new Gson();

    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss.SSS");

    public static final MersenneTwister random = new MersenneTwister(System.currentTimeMillis());

    public static final ActionResponse ERROR_500 = new ActionResponse(500, "Internal Server Error");
    public static final ActionResponse INVALID_GENERATOR_NAME_RESPONSE = new ActionResponse(409, "{\"validNames\":" + gson.toJson(GeneratorFactory.GENERATOR_STRING_OPTIONS) + "}");
    public static final ActionResponse INVALID_DIMENSION_FORMAT_RESPONSE = new ActionResponse(400, gson.toJson("Invalid dimension. Expected string like \"123x456\""));
    public static final ActionResponse MISSING_DIMENSIONS_RESPONSE = new ActionResponse(400, "Missing dimensions in query. Expected something like \"/generate/<generator-name>/500x500\"");
    public static final String NO_OPTIONS_FOR_RANDOM = "Can only provide options for a specific generator.";
    public static final ActionResponse LIST_GENERATORS_RESPONSE = new ActionResponse(200, gson.toJson(GeneratorFactory.GENERATOR_STRING_OPTIONS));

    public static final Dimension[] GENERATE_ALL_DIMENSIONS = new Dimension[] {
            new Dimension(1920, 1080),
            new Dimension(1080, 1920),
            new Dimension(1024, 1024)
    };

    public static final Storage STORAGE = StorageOptions.getDefaultInstance().getService();
    public static final String GCS_BUCKET = System.getenv("GCS_BUCKET");

    public static ActionResponse listGenerators(){
        return LIST_GENERATORS_RESPONSE;
    }

    public static ActionResponse getGeneratorOptions(String generatorName){
        if(generatorName.equals(GeneratorFactory.RANDOM_GENERATOR_NAME)){
            return new ActionResponse(400, NO_OPTIONS_FOR_RANDOM);
        }
        IGenerator generator = GeneratorFactory.getGenerator(generatorName, random);
        if (generator == null) {
            return INVALID_GENERATOR_NAME_RESPONSE;
        }

        List<GeneratorOptionDetails> optionsList = new ArrayList<>();
        List<IniSchemaOption<?>> options = generator.getIniSettings();
        for(IniSchemaOption<?> option : options){
            optionsList.add(new GeneratorOptionDetails(option));
        }
        return new ActionResponse(200, gson.toJson(optionsList));
    }

    public static ActionResponse generateOne(String generatorName, Dimension imageSize, Map<String, String> params){
        IGenerator generator = GeneratorFactory.getGenerator(generatorName, random);
        if (generator == null) {
            return INVALID_GENERATOR_NAME_RESPONSE;
        }

        if(imageSize == null) {
            return INVALID_DIMENSION_FORMAT_RESPONSE;
        }

        //Build options
        Map<String, Object> settings = buildSettings(generator, params);

        //Generate image
        try {
            byte[] imgBytes = createImage(generator, imageSize, settings);
            String url = saveToCloud(imgBytes, generatorName, imageSize, random.nextLong());
            return new ActionResponse(200, String.format("{\"url\":\"%s\"}", url));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR_500;
        }
    }

    public static ActionResponse generateAll(Map<String, String> params){
        List<String> urls = new ArrayList<>();
        for(Dimension imageSize : GENERATE_ALL_DIMENSIONS) {
            for(String generatorName : GeneratorFactory.GENERATOR_STRING_OPTIONS) {

                IGenerator generator = GeneratorFactory.getGenerator(generatorName, random);
                if (generator == null) {
                    return INVALID_GENERATOR_NAME_RESPONSE;
                }


                //Build options
                Map<String, Object> settings = buildSettings(generator, params);

                //Generate image
                try {
                    final byte[] imgBytes = createImage(generator, imageSize, settings);
                    String url = saveToCloud(imgBytes, generatorName, imageSize, random.nextLong());
                    urls.add(url);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return ERROR_500;
                }

            }
        }
        return new ActionResponse(200, gson.toJson(urls));
    }

    public static Map<String, Object> buildSettings(IGenerator generator, Map<String,String> params) {
        Map<String, Object> settings = new HashMap<>();
        List<IniSchemaOption<?>> options = generator.getIniSettings();

        for(IniSchemaOption<?> option : options){
            String safeOptionName = GeneratorOptionDetails.getUrlSafeOptionName(option);
            if(params.containsKey(safeOptionName)) {
                String paramValue = params.get(safeOptionName);
                settings.put(option.getOptionName(), toObject(option.getValueType(), paramValue));
            }
            else {
                Object[] defaultValues = option.getDefaultValues();
                settings.put(option.getOptionName(), defaultValues[random.nextInt(defaultValues.length)]);
            }
        }
        return settings;
    }

    public static byte[] createImage(IGenerator generator, Dimension imageSize, Map<String, Object> settings) throws Exception {
        Graphics2D g = null;
        try{
            BufferedImage mainImage = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_ARGB);
            g = (Graphics2D)mainImage.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            generator.generateImage(settings, g, imageSize, random);

            try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                ImageIO.write(mainImage, "png", stream );

                stream.flush();
                return stream.toByteArray();
            }
        }
        finally{
            if(g != null){
                g.dispose();
            }
        }
    }

    private static final Pattern dimensionPattern = Pattern.compile("(\\d+)[xX](\\d+)");
    public static final Dimension parseDimension(String dimensionStr) {
        try {
            Matcher matcher = dimensionPattern.matcher(dimensionStr);
            if(matcher.find()){
                String widthStr = matcher.group(1);
                String heightStr = matcher.group(2);
                return parseDimension(widthStr, heightStr);
            }
            //yay antipatterns!
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            //yay antipatterns!
            return null;
        }
    }
    public static final Dimension parseDimension(String widthStr, String heightStr) {
        try {

            int width = Integer.parseInt(widthStr);
            int height = Integer.parseInt(heightStr);
            return new Dimension(width, height);
        } catch (Exception e) {
            e.printStackTrace();
            //yay antipatterns!
            return null;
        }
    }

    private static final String saveToCloud(byte[] imgBytes, String generatorName, Dimension size, long seed) {

        String rootFolderName = generatorName.replace(' ', '_').toLowerCase(Locale.ROOT);
        String aspectRatioFolderName = getAspectRatioName(size);
        String timestamp = dateFormatter.format(new Date());
        String name = String.format("%s/%s/%s.png", rootFolderName, aspectRatioFolderName, Long.toString(seed, 16));

        Map<String, String> metadata = new HashMap<String, String>(){
            {
                put("timestamp", timestamp);
                put("width", Integer.toString(size.width));
                put("height", Integer.toString(size.height));
            }
        };

        final BlobId blobId = BlobId.of(GCS_BUCKET, name);
        final BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setMetadata(metadata).build();

        try {
            STORAGE.create(blobInfo, imgBytes, Storage.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));
            return String.format("https://storage.googleapis.com/%s/%s", GCS_BUCKET, name);
        } catch (Exception e) {
            //oh well - not a critical error so hiding the exception is ok
            e.printStackTrace();
            return null;
        }
    }

    private static final double SQUARENESS_THRESHOLD = 0.1;
    private static final String getAspectRatioName(Dimension dim) {
        double ratio = dim.getWidth() / dim.getHeight();

        if(Math.abs(1 - ratio) <= SQUARENESS_THRESHOLD) {
            return "square";
        }
        if(ratio > 1) {
            return "landscape";
        }
        return "portrait";
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
