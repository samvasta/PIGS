package com.samvasta.imageGenerator.batchgenerator.app;

import com.samvasta.imageGenerator.common.models.IniSchema;
import com.samvasta.imageGenerator.common.models.IniSchemaOption;
import com.samvasta.imageGenerator.common.models.IniSchemaSection;
import com.samvasta.imageGenerator.common.exceptions.BadSettingsFileException;
import com.samvasta.imageGenerator.common.helpers.ComparisonHelper;
import com.samvasta.imageGenerator.common.helpers.ImageIOHelper;
import com.samvasta.imageGenerator.common.helpers.IniHelper;
import com.samvasta.imageGenerator.common.helpers.JarHelper;
import com.samvasta.imageGenerator.common.interfaces.IGenerator;
import com.samvasta.imageGenerator.common.models.GeneratorContext;
import com.samvasta.imageGenerator.common.models.ImageBundle;
import com.samvasta.imageGenerator.common.models.ImageCreator;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.log4j.Logger;
import org.ini4j.Ini;
import org.ini4j.Profile;


import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class App
{
    private static final Logger LOGGER = Logger.getLogger(App.class);

    private static final String GENERATOR_PLUGINS_DIR_PATH = "GeneratorPlugins";

    private static final String INI_SETTINGS_PATH = "settings.ini";
    private static final String SETTINGS_SECTION_GENERAL = "General";
    private static final String SETTINGS_GENERAL_NUM_IMAGES_OUTPUT = "num_image_output";
    private static final String SETTINGS_GENERAL_MULTI_THREADING = "use_multi_threading";
    private static final String SETTINGS_GENERAL_OUTPUT_PATH = "output_relative_path";

    private static final String SETTINGS_SECTION_TYPES = "Types";

    private static final String SETTINGS_SECTION_ADVANCED = "Advanced";
    private static final String SETTINGS_ADVANCED_IS_OVERRIDE_SIZE = "use_custom_image_size";
    private static final String SETTINGS_ADVANCED_OVERRIDE_WIDTH = "custom_image_width";
    private static final String SETTINGS_ADVANCED_OVERRIDE_HEIGHT = "custom_image_height";

    private static final int DEFAULT_WIDTH = 1920;
    private static final int DEFAULT_HEIGHT = 1080;

    private boolean isInitSuccessful;
    private Map<String, Class<? extends IGenerator>> generatorTypes;

    private Ini settings;

    private void init(){
        try{
            loadGenerators();
            loadSettings();
        } catch(IOException e){
            isInitSuccessful = false;
        }
        isInitSuccessful = true;
    }

    private void loadGenerators() throws IOException{
        File generatorPlugins = new File(GENERATOR_PLUGINS_DIR_PATH);
        if(!generatorPlugins.exists() || !generatorPlugins.isDirectory()){
            generatorPlugins.mkdir();
        }

        File[] plugins = generatorPlugins.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.toLowerCase().endsWith(".jar");
            }
        });

        ArrayList<Class<? extends IGenerator>> pluginGeneratorsList = new ArrayList<>();
        for(File fileName : plugins){
            JarHelper.populateGeneratorTypesFromJar(pluginGeneratorsList, fileName.getAbsolutePath());
        }

        generatorTypes = new HashMap<>();

        for(Class<? extends IGenerator> klass : pluginGeneratorsList){
            generatorTypes.put(klass.getSimpleName(), klass);
        }
    }

    private void loadSettings() throws IOException{
        File iniFile = new File(INI_SETTINGS_PATH);

        if(!iniFile.exists()){
            if(!iniFile.createNewFile()){
                throw new IOException("Could not create the settings.ini file");
            }
            LOGGER.info("No settings.ini file was found. A new settings file will be created. This file must be modified before continuing.");
        }
        IniSchema schema = generateIniSchema();

        settings = IniHelper.loadIni(INI_SETTINGS_PATH);
        validateSettingsFile(schema);
        settings.store(iniFile);
    }

    private IniSchema generateIniSchema() {
        IniSchema schema = new IniSchema();

        IniSchemaSection generalSection = schema.addSection(SETTINGS_SECTION_GENERAL);
        generalSection.addOption(SETTINGS_GENERAL_NUM_IMAGES_OUTPUT, 0);
        generalSection.addOption(SETTINGS_GENERAL_MULTI_THREADING, true);
        generalSection.addOption(SETTINGS_GENERAL_OUTPUT_PATH, "Images/");

        IniSchemaSection typesSection = schema.addSection(SETTINGS_SECTION_TYPES, "Integer values in this section are relative " +
                "weights and their sum does not need to equal the total number of images generated.");

        for(Class<? extends IGenerator> generatorType : generatorTypes.values()){

            try {
                IGenerator generator = generatorType.newInstance();
                Integer initialValue = generator.isOnByDefault() ? 1 : 0;
                typesSection.addOption(generatorType.getSimpleName(), initialValue);

                List<IniSchemaOption<?>> generatorSettings = generator.getIniSettings();

                if(!generatorSettings.isEmpty()){
                    IniSchemaSection generatorTypeSection = schema.addSection(generatorType.getSimpleName());
                    for(IniSchemaOption<?> settingEntry : generatorSettings){
                        generatorTypeSection.addOption(settingEntry.getOptionName(), settingEntry.getDefaultValue());
                    }
                }

            }
            catch (InstantiationException | IllegalAccessException e) {
                LOGGER.info("Could not initialize generator of type " + generatorType.getSimpleName(), e);
            }
        }


        IniSchemaSection advancedSection = schema.addSection(SETTINGS_SECTION_ADVANCED);
        advancedSection.addOption(SETTINGS_ADVANCED_IS_OVERRIDE_SIZE, false);
        advancedSection.addOption(SETTINGS_ADVANCED_OVERRIDE_WIDTH, 0);
        advancedSection.addOption(SETTINGS_ADVANCED_OVERRIDE_HEIGHT, 0);

        return schema;
    }


    private void validateSettingsFile(IniSchema schema){
        List<IniSchemaSection> schemaSections = schema.getSections();

        //remove old sections
        for(String section : settings.keySet()){
            boolean isSectionFound = false;
            for(IniSchemaSection schemaSection : schemaSections){
                if(section.equals(schemaSection.getSectionName())){
                    isSectionFound = true;
                    break;
                }
            }

            if(!isSectionFound){
                settings.remove(section);
                LOGGER.info("The section " + section + " is invalid. This section will be removed.");
            }
        }

        //add/update sections
        for(IniSchemaSection schemaSection : schemaSections){
            validateSection(schemaSection);
        }
    }

    private void validateSection(IniSchemaSection schemaSection){
        String sectionName = schemaSection.getSectionName();

        if(!settings.containsKey(sectionName)){
            IniHelper.addSection(settings, sectionName);
            String comment = schemaSection.getSectionComment();
            if(!comment.isEmpty()){
                IniHelper.addComment(settings, sectionName, comment);
            }
            LOGGER.info("The " + schemaSection.getSectionName() + " section was not found in the settings.ini file. This section will be created.");
        }
        Profile.Section iniSection = settings.get(schemaSection.getSectionName());

        String[] optionNames = iniSection.childrenNames();
        List<IniSchemaOption<?>> schemaOptions = schemaSection.getOptions();

        //Add new options
        for(IniSchemaOption schemaOption : schemaOptions){
            if(!iniSection.containsKey(schemaOption.getOptionName())){
                iniSection.add(schemaOption.getOptionName(), schemaOption.getDefaultValue());
            }
        }

        //Remove old options
        for(String optionName : optionNames){
            boolean isOptionInSchema = false;
            for(IniSchemaOption schemaOption : schemaOptions){
                if(schemaOption.getOptionName().equals(optionName)){
                    isOptionInSchema = true;
                    break;
                }
            }

            if(!isOptionInSchema){
                iniSection.removeChild(optionName);
                LOGGER.info("The " + schemaSection.getSectionName() + " section is invalid. This section will be removed.");
            }
        }
    }

    private void start() throws BadSettingsFileException
    {
        if(!isInitSuccessful){
            throw new BadSettingsFileException("Initialization has failed.");
        }

        boolean isMultiThreadEnabled = IniHelper.getBooleanValue(settings, SETTINGS_SECTION_GENERAL, SETTINGS_GENERAL_MULTI_THREADING);
        MersenneTwister seedGenerator = new MersenneTwister();
        Dimension imageSize = getImageSize();
        String pathRoot = IniHelper.getStringValue(settings, SETTINGS_SECTION_GENERAL, SETTINGS_GENERAL_OUTPUT_PATH);
        GeneratorContext context = new GeneratorContext(imageSize, seedGenerator, pathRoot);


        initOutputDirectory(pathRoot);

        HashMap<Class<? extends IGenerator>, List<IGenerator>> generatorsMap = getGenerators();

        for(Class<? extends IGenerator> generatorType : generatorsMap.keySet()){
            List<IGenerator> generators = generatorsMap.get(generatorType);
            IGenerator generator = generators.get(0);
            boolean isMultiThreadedOverride = generator.isMultiThreadEnabled();
            generateImages(generators, context, isMultiThreadEnabled && isMultiThreadedOverride);
        }
    }

    private Dimension getImageSize(){
        boolean isImageSizeOverride = IniHelper.getBooleanValue(settings, SETTINGS_SECTION_ADVANCED, SETTINGS_ADVANCED_IS_OVERRIDE_SIZE);
        if(isImageSizeOverride){
            int width = IniHelper.getIntValue(settings, SETTINGS_SECTION_ADVANCED, SETTINGS_ADVANCED_OVERRIDE_WIDTH);
            int height = IniHelper.getIntValue(settings, SETTINGS_SECTION_ADVANCED, SETTINGS_ADVANCED_OVERRIDE_HEIGHT);
            return new Dimension(width, height);
        }

        Dimension imageSize = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        int maxPixels = 0;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for(GraphicsDevice device : ge.getScreenDevices()) {
            int numPixels = device.getDisplayMode().getWidth() * device.getDisplayMode().getHeight();
            if(numPixels > maxPixels)
            {
                maxPixels = numPixels;
                imageSize = new Dimension(device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight());
            }
        }

        return imageSize;
    }

    private HashMap<Class<? extends IGenerator>, Integer> getGeneratorWeightMap(){
        HashMap<Class<? extends IGenerator>, Integer> map = new HashMap<>();

        for(Class<? extends IGenerator> generatorType : generatorTypes.values()){
            try{
                int weight = IniHelper.getIntValue(settings, SETTINGS_SECTION_TYPES, generatorType.getSimpleName());
                map.put(generatorType, weight);
            } catch (NumberFormatException e){
                //Don't care
                LOGGER.warn(String.format("Settings file did not contain information for generator type \"%s\"", generatorType.getSimpleName()));
            }
        }
        return map;
    }

    private void initOutputDirectory(String dir){
        File file = new File(dir);

        if(!file.exists() || !file.isDirectory()){
            file.mkdir();
        }
    }

    private HashMap<Class<? extends IGenerator>, List<IGenerator>> getGenerators() throws BadSettingsFileException{
        int numImages = IniHelper.getIntValue(settings, SETTINGS_SECTION_GENERAL, SETTINGS_GENERAL_NUM_IMAGES_OUTPUT);
        if(numImages < 1){
            throw new BadSettingsFileException(String.format("Settings option \"%s > %s\" must be greater than 0.", SETTINGS_SECTION_GENERAL, SETTINGS_GENERAL_NUM_IMAGES_OUTPUT));
        }

        HashMap<Class<? extends IGenerator>, List<IGenerator>> generators = new HashMap<>();


        HashMap<Class<? extends IGenerator>, Integer> generatorWeightMap = getGeneratorWeightMap();

        int totalWeight = 0;
        for(int weight : generatorWeightMap.values()){
            totalWeight += weight;
        }

        double step = (double)totalWeight / (double)numImages;

        Iterator<Class<? extends IGenerator>> generatorIterator = generatorWeightMap.keySet().iterator();

        if(!generatorIterator.hasNext()){
            throw new BadSettingsFileException("No generators found.");
        }


        Class<? extends IGenerator> currentGeneratorType = generatorIterator.next();
        double maxGeneratorWeight = generatorWeightMap.get(currentGeneratorType);

        double totalWeightAcc = 0;
        double generatorWeightAcc = 0;

        List<IGenerator> currentGeneratorList = new ArrayList<>();
        generators.put(currentGeneratorType, currentGeneratorList);

        while(ComparisonHelper.isLessThan(totalWeightAcc, totalWeight)){
            if(generatorWeightAcc >= maxGeneratorWeight){
                if(generatorIterator.hasNext()){
                    generatorWeightAcc = 0;
                    currentGeneratorType = generatorIterator.next();
                    maxGeneratorWeight = generatorWeightMap.get(currentGeneratorType);

                    currentGeneratorList = new ArrayList<>();
                    generators.put(currentGeneratorType, currentGeneratorList);
                }
                else{
                    break;
                }
            }

            try{
                currentGeneratorList.add(currentGeneratorType.newInstance());
            } catch (IllegalAccessException | InstantiationException e){
                //don't care
                LOGGER.warn(String.format("Could not create generator of type \"%s\"", currentGeneratorType.getSimpleName()));
            }

            generatorWeightAcc += step;
            totalWeightAcc += step;
        }

        return generators;
    }

    private void generateImages(final List<IGenerator> generators, final GeneratorContext context, final boolean isMultiThreadEnabled){
        int numThreads = 1;
        if(isMultiThreadEnabled){
            numThreads = Runtime.getRuntime().availableProcessors();
        }

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        LinkedList<Future<ImageBundle>> imagePromises = new LinkedList<>();

        for(IGenerator generator : generators){
            long seed = context.seedGenerator.nextLong();
            ImageCreator imageCreator = new ImageCreator(context.imageSize, generator, seed, settings);
            imagePromises.add(executorService.submit(imageCreator));
        }

        ExecutorService executorServiceSaving = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        while(imagePromises.size() > 0){
            Future<ImageBundle> promise = imagePromises.remove(0);
            if(promise.isDone()){
                try{
                    final ImageBundle bundle = promise.get();
                    executorServiceSaving.execute(new Runnable(){

                        public void run()
                        {
                            try{
                                ImageIOHelper.saveImage(bundle.image, bundle.seed, context.outputDir);
                            } catch(IOException e){
                                //Don't care
                            }
                        }
                    });
                }
                catch(InterruptedException | ExecutionException e){
                    //don't care
                }
            }
            else{
                if(!promise.isCancelled())
                {
                    imagePromises.addLast(promise);

                }
            }
        }

        executorService.shutdown();
        try{
            if(!executorService.awaitTermination(10, TimeUnit.DAYS)){
                executorService.shutdownNow();
                LOGGER.info("Images took too long to generate. Shutting down...");
            }
        } catch (InterruptedException e){
            executorService.shutdownNow();
            LOGGER.error("Image generation service has been interrupted. Shutting down...");
        }

        executorServiceSaving.shutdown();
        try{
            if(!executorServiceSaving.awaitTermination(10, TimeUnit.MINUTES)){
                executorServiceSaving.shutdownNow();
                LOGGER.info("Images took too long to save. Shutting down...");
            }
        }
        catch(InterruptedException e){
            executorServiceSaving.shutdownNow();
            LOGGER.error("Image saving service has been interrupted. Shutting down...");
        }
    }

    public static void main(String...args){
        App app = new App();

        try{
            app.init();
            app.start();

        } catch(BadSettingsFileException e){
            LOGGER.error(e.getMessage());
        }
    }
}
