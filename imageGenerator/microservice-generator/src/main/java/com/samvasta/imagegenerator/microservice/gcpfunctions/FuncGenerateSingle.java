package com.samvasta.imagegenerator.microservice.gcpfunctions;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.samvasta.imagegenerator.microservice.actions.ActionResponse;
import com.samvasta.imagegenerator.microservice.actions.Actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FuncGenerateSingle implements HttpFunction {

    private static final Logger logger = Logger.getLogger(FuncGenerateSingle.class.getName());

    @Override
    public void service(HttpRequest request, HttpResponse response)
            throws IOException {

        String generatorName = request.getFirstQueryParameter("generator").orElse(null);
        String width = request.getFirstQueryParameter("width").orElse(null);
        String height = request.getFirstQueryParameter("height").orElse(null);

        Map<String, String> queryParams = new HashMap<>();
        for(String key : request.getQueryParameters().keySet()){
            queryParams.put(key, request.getQueryParameters().get(key).get(0));
        }

        ActionResponse actionResponse = Actions.generateOne(generatorName, Actions.parseDimension(width, height), queryParams);
        response.setContentType("application/json");
        response.setStatusCode(actionResponse.httpResponseStatus);

        PrintWriter writer = new PrintWriter(response.getWriter());
        writer.print(actionResponse.resultJson);
        writer.flush();
    }
}
