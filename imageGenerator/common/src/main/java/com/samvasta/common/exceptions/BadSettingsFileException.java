//------------------------------------------------------------------------------
// AnalyticsOS
// Copyright (c) 2018. Lone Star Aerospace, Inc
// com.samvasta.common.exceptions.BadSettingsFileException
//
// Unauthorized copying of this file, via any medium, is strictly prohibited.
// Proprietary. All rights reserved.
//------------------------------------------------------------------------------
package com.samvasta.common.exceptions;

public class BadSettingsFileException extends Exception
{
    public BadSettingsFileException(String errorMsgIn){
        super(errorMsgIn);
    }
}
