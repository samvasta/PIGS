//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.common.helpers.IniHelper
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.common.helpers;

import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class IniHelper
{

    public static Ini loadIni(String path) throws IOException {
        return new Ini(new FileReader(path));
    }

    public static void addSection(Ini ini, String sectionName){
        Profile.Section section = ini.add(sectionName);
    }

    public static void addOption(Ini ini, String sectionName, String optionName, String value){
        Profile.Section section = ini.get(sectionName);
        section.add(optionName, value);
    }

    public static void addComment(Ini ini, String sectionName, String comment){
        ini.putComment(sectionName, comment);
    }

    public static int getIntValue(Ini ini, String sectionName, String optionName) throws NumberFormatException{
        Profile.Section section = ini.get(sectionName);
        return Integer.parseInt(section.get(optionName));
    }

    public static boolean getBooleanValue(Ini ini, String sectionName, String optionName) {
        Profile.Section section = ini.get(sectionName);
        return Boolean.parseBoolean(section.get(optionName));
    }

    public static String getStringValue(Ini ini, String sectionName, String optionName){
        Profile.Section section = ini.get(sectionName);
        return section.get(optionName);
    }

    public static <T> T getValue(Ini ini, String sectionName, String optionName, Class<T> klass){
        Profile.Section section = ini.get(sectionName);
        return section.get(optionName, klass);
    }

    public static void commitIni(Ini ini, File file) throws IOException{
        ini.store(file);
    }
}
