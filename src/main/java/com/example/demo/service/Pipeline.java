package com.example.demo.service;

import com.example.demo.api.model.Algorithm;
import com.example.demo.api.model.AlgorithmONNX;
import com.example.demo.utils.Utils;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.tree.TreeNode;
import java.io.FileReader;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDate;
import ai.onnxruntime.*;

public class Pipeline {
    protected Map<String, Transformer> transformers;
    protected Algorithm classifier;
    protected int nbTransformers;
    protected boolean loaded;
    protected Boolean isClassifier;
    protected Boolean isTransformer;

    public Pipeline(Map<String, Transformer> transformers){
        this.transformers = transformers;
        this.nbTransformers = transformers.size();
        this.loaded = Boolean.FALSE;
        this.isTransformer = Boolean.TRUE;
        this.isClassifier = Boolean.FALSE;
        this.load();
    }

    public Pipeline(Map<String, Transformer> transformers, Algorithm classifier){
        this.transformers = transformers;
        this.nbTransformers = transformers.size();
        this.classifier = classifier;
        this.loaded = Boolean.FALSE;
        this.isTransformer = Boolean.TRUE;
        this.isClassifier = Boolean.TRUE;
        this.load();
    }

    public Pipeline(Algorithm classifier){
        this.classifier = classifier;
        this.loaded = Boolean.FALSE;
        this.isTransformer = Boolean.FALSE;
        this.isClassifier = Boolean.TRUE;
        this.load();
    }

    public void load() {
        if(this.isTransformer){
            for(Transformer t: transformers.values()){
                t.load();
            }
        }
        if(this.isClassifier){
            try{
                this.classifier.loadAlgorithm();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        this.loaded = Boolean.TRUE;
    }

    public Map<String, Object> transform(Map<String, Object> data) {
        if(! this.isTransformer){
            System.out.println("No transformer in Pipeline.");
            System.exit(1);
        }
        for(Transformer t: this.transformers.values()){
            data = t.transform(data);
        }
        return data;
    }

    public long predict(Map<String, Object> data){

        if(! this.isClassifier){
            System.out.println("No Classifier in Pipeline.");
            System.exit(1);
        }

        long pred = -1;

        if(this.isTransformer)
            data = this.transform(data);

        try {
            pred = this.classifier.predict(data);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return pred;
    }

    public void addClassifier(Algorithm classifier){
        this.classifier = classifier;
        this.isClassifier = Boolean.TRUE;
    }
}
