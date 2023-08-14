package com.example.demo.service;

import com.example.demo.api.model.AlgorithmPMML;
import com.example.demo.utils.Utils;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class OrdinalEncoder extends Transformer{

    Map<String, Object> dictionary;
    String encoderPath;

    public OrdinalEncoder(String filename, String folderName){
        super(filename, folderName);
    }

    public void load(){

        String modelFolder = OrdinalEncoder.class.getClassLoader().getResource(this.getFilename()).getPath();
        this.encoderPath = modelFolder;

        try {
            JsonReader reader = Json.createReader(new FileReader(this.encoderPath));
            JsonObject json_encoder = reader.readObject();
            this.dictionary = Utils.toMap(json_encoder);
            this.loaded = Boolean.TRUE;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Map<String, Object> transform(Map<String, Object> data){
        if(! this.loaded){
            System.out.println("\nCould not load encoder.\n");
            return null;
        }
        for(String c : Utils.categorical_features){
            Map c_map = (HashMap) this.dictionary.get(c);
            String current_value =  (String) data.get(c);
            int encoded_value = Integer.parseInt((String) c_map.get(current_value));
            data.replace(c, encoded_value);
        }
        return data;
    }
}
