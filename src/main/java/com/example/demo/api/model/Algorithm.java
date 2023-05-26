package com.example.demo.api.model;


import ai.onnxruntime.OnnxTensor;
import org.json.JSONArray;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class Algorithm {
    protected int id;
    protected String name;
    protected String description;
    protected String filename;
    protected int type;
    protected Boolean loaded;

    public Algorithm(int id, String name, String description, String filename, int type){
        this.id = id;
        this.name = name;
        this.description = description;
        this.filename = filename;
        this.type = type;
        this.loaded = Boolean.FALSE;
    }
    public int getId(){
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getFilename() {
        return this.filename;
    }
    public void setFilename(String filename){
        this.filename = filename;
    }

    public int getType() {
        return this.type;
    }
    public void setType(int type){
        this.type = type;
    }

    public Boolean isLoaded(){
        return this.loaded;
    }

    public abstract float predict(Map<String, Object> values) throws Exception;
    public abstract Map transform(Map<String, Object> values) throws Exception;
    public abstract void loadAlgorithm() throws Exception;


}
