package com.example.demo.api.controller;

import com.example.demo.api.model.Algorithm;
import com.example.demo.service.AlgorithmService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class AlgorithmController {

    @Autowired
    private AlgorithmService algorithmService;

    public AlgorithmController(AlgorithmService algorithmService){
        this.algorithmService = algorithmService;
    }

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello World";
    }

    @RequestMapping(value = "/predict", method = RequestMethod.POST)
    public HashMap predict(@RequestBody String payload) throws Exception {

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(payload);
        }catch (JSONException err){
            return new HashMap<String, Object>(){
                {
                    put("KEYVALUES", "");
                    put("RQUID","");
                    put("RESULTID","");
                    put("ERRORCODE","");
                    put("ERRORDESC", "");
                    put("MESSAGE", "Failed to load JSON request: " + err.toString());
                    put("ISFRAUD", -1);

                }
            };
        }
        String algoName = jsonObject.getString("algo");

        Optional algo = algorithmService.getAlgorithm(algoName);

        if(algo.isPresent()){
            Algorithm algorithm = (Algorithm) algo.get();
            String algoDescription = algorithm.getDescription();


            if( ! algorithm.isLoaded()){
                return new HashMap<String, Object>(){
                    {
                        put("KEYVALUES", "");
                        put("RQUID","");
                        put("RESULTID","");
                        put("ERRORCODE","");
                        put("ERRORDESC", "");
                        put("MESSAGE", "Failed to load " + algoDescription);
                        put("ISFRAUD", -1);
                    }
                };
            }

            float y = -1.0f;
            try {
                y = algorithm.predict((JSONArray) jsonObject.get("transaction"));
            }
            catch(Exception e) {
                return new HashMap<String, Object>() {
                    {
                        put("KEYVALUES", "");
                        put("RQUID","");
                        put("RESULTID","");
                        put("ERRORCODE","");
                        put("ERRORDESC", "");
                        put("MESSAGE", "Failed to predict with " + algoDescription + ": " + e.toString());
                        put("ISFRAUD", -1);
                    }
                };
            }
            float finalY = y;
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
                put("RESULTID","");
                put("ERRORCODE","");
                put("ERRORDESC", "");
                put("MESSAGE", "Can't find algorithm in Database");
                put("ISFRAUD", -1);
            }
        };
    }
}
