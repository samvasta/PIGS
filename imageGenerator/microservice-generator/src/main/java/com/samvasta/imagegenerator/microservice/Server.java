package com.samvasta.imagegenerator.microservice;

import com.samvasta.imagegenerator.microservice.actions.ActionResponse;
import com.samvasta.imagegenerator.microservice.actions.Actions;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.awt.*;
import java.util.*;

public class Server {

    public static final Logger logger = Logger.getLogger(Server.class);

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        Spark.port(port);

        Spark.get("/", (req, res) -> "Hello world");
        System.out.println("Server listening on port " + Spark.port());

        Spark.get(
                "/list-generators",
                (Request req, Response res) -> {
                    ActionResponse response = Actions.listGenerators();
                    res.status(response.httpResponseStatus);
                    res.type("application/json");
                    return response.resultJson;
                }
        );

        Spark.get(
                "/options/:generatorname",
                (Request req, Response res) -> {
                    String generatorName = req.params(":generatorname");
                    ActionResponse response = Actions.getGeneratorOptions(generatorName);
                    res.status(response.httpResponseStatus);
                    res.type("application/json");
                    return response.resultJson;
                }
        );

        Spark.get(
                "/generate/:generatorname",
                (Request req, Response res) -> {
                    String generatorName = req.params(":generatorname");

                    Map<String, String> queryParams = new HashMap<>();
                    for(String key : req.queryParams()) {
                        queryParams.put(key.toLowerCase(), req.queryParams(key));
                    }
                    ActionResponse response = Actions.generateOne(generatorName, null, queryParams);
                    res.status(response.httpResponseStatus);
                    res.type("application/json");
                    return response.resultJson;
                }
        );

        Spark.get(
                "/generate/:generatorname/:dimension",
                (Request req, Response res) -> {
                    String generatorName = req.params(":generatorname");

                    String dimension = req.params(":dimension");
                    System.out.println(dimension);
                    Dimension imageSize = Actions.parseDimension(dimension);
                    System.out.println(imageSize);

                    Map<String, String> queryParams = new HashMap<>();
                    for(String key : req.queryParams()) {
                        queryParams.put(key.toLowerCase(), req.queryParams(key));
                    }
                    ActionResponse response = Actions.generateOne(generatorName, imageSize, queryParams);
                    res.status(response.httpResponseStatus);
                    res.type("application/json");
                    return response.resultJson;
                });


        Spark.post(
                "/generate-all",
                (Request req, Response res) -> {

                    Map<String, String> queryParams = new HashMap<>();
                    for(String key : req.queryParams()) {
                        queryParams.put(key.toLowerCase(), req.queryParams(key));
                    }

                    ActionResponse response = Actions.generateAll(queryParams);

                    res.status(response.httpResponseStatus);
                    res.type("application/json");
                    return response.resultJson;
                });
    }

}
