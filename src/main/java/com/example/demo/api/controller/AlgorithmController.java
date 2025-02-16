package com.example.demo.api.controller;

import com.example.demo.api.model.Algorithm;
import com.example.demo.service.*;
import com.example.demo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.json.*;
import java.io.StringReader;
import java.util.*;


@RestController
public class AlgorithmController {

    @GetMapping("/welcome")
    public String welcome(){
        return "Welcome to spring boot heroku demo";
    }

    @Autowired
    private AlgorithmService algorithmService;

    public AlgorithmController(AlgorithmService algorithmService){
        this.algorithmService = algorithmService;
    }



    @RequestMapping(value = "/predict", method = RequestMethod.POST)
    public Map predict(@RequestBody String payload) throws Exception {

        JsonObject data;
        try {
            JsonReader jsonReader = Json.createReader(new StringReader(payload));
            data = jsonReader.readObject();
            jsonReader.close();

        } catch (final JsonException err) {
            return new HashMap<String, Object>() {
                {
                    put("KEYVALUES", "");
                    put("RQUID", "");
                    put("RESULTID", "SystemError");
                    put("ERRORCODE", "00001");
                    put("ERRORDESC", "");
                    put("MESSAGE", "Failed to load JSON request: " + err.toString());
                    put("ISFRAUD", -1);
                }
            };
        }
        final String algoName = data.getString("algo");

        Optional algo = algorithmService.getAlgorithm(algoName);

        if (algo.isPresent()) {
            Algorithm algorithm = (Algorithm) algo.get();
            final String algoDescription = algorithm.getDescription();

//            if (!algorithm.isLoaded()) {
//                return new HashMap<String, Object>() {
//                    {
//                        put("KEYVALUES", "");
//                        put("RQUID", "");
//                        put("RESULTID", "SystemError");
//                        put("ERRORCODE", "00001");
//                        put("ERRORDESC", "SYSTEM_ERROR");
//                        put("MESSAGE", "Failed to load " + algoDescription);
//                        put("ISFRAUD", -1);
//                    }
//                };
//            }

            // Add Preprocessing Here
             


            Utils utils = new Utils();

            JsonObject tr_object = (JsonObject) data.get("transaction");

            Map<String, Object> transaction = Utils.toMap(tr_object);

            Pipeline clf = algorithmService.getPipeline();
            clf.addClassifier(algorithm);

            long y = -1;
            try {
                y = clf.predict(transaction);
            }
            catch(final Exception e) {
                return new HashMap<String, Object>() {
                    {
                        put("KEYVALUES", "");
                        put("RQUID", "");
                        put("RESULTID", "SystemError");
                        put("ERRORCODE", "00001");
                        put("ERRORDESC", "SYSTEM_ERROR");
                        put("MESSAGE", "Failed to predict with " + algoDescription + ": " + e.toString());
                        put("ISFRAUD", -1);
                    }
                };
            }
            final long finalY = y;
            return new HashMap<String, Object>(){
                    {
                        put("KEYVALUES", "");
                        put("RQUID","");
                        put("RESULTID","");
                        put("ERRORCODE","");
                        put("ERRORDESC", "");
                        put("MESSAGE", "Predicted with " + algoDescription);
                        put("ISFRAUD", finalY);
                    }
            };
        }


        return new HashMap<String, Object>(){
            {
                put("KEYVALUES", "");
                put("RQUID","");
                put("RESULTID","SystemError");
                put("ERRORCODE","00001");
                put("ERRORDESC", "SYSTEM_ERROR");
                put("MESSAGE", "Can't find algorithm " + algoName + " in Database");
                put("ISFRAUD", -1);
            }
        };
        }
    }


