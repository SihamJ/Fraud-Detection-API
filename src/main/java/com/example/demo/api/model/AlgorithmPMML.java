package com.example.demo.api.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.SAXException;
import javax.xml.bind.JAXBException;

import java.io.File;
import java.io.IOException;

import com.example.demo.utils.Utils;


public class AlgorithmPMML extends Algorithm {

    private Evaluator evaluator;
    private String modelFolder;
    private Path modelPath;



    public AlgorithmPMML(int id, String name, String description, String folder, String filename, int type){
        super(id, name, description, folder, filename, type);
    }

    public void loadAlgorithm()  throws JAXBException, IOException, SAXException {
        

        this.modelFolder = AlgorithmPMML.class.getClassLoader().getResource(this.getFilename()).getPath();
        // this.modelFolder = Utils.models_folder;

        //File file = new File(getClass().getClassLoader().getResource(this.getFilename()).getFile());

        this.modelPath = Paths.get(modelFolder);
        
        this.evaluator = new LoadingModelEvaluatorBuilder()
                .load(modelPath.toFile())
                .build();

        // this.evaluator = new LoadingModelEvaluatorBuilder()
        //         .load(resource.getFile())
        //         .build();

        this.evaluator.verify();
        this.loaded = Boolean.TRUE;
    }

    public long predict(Map<String, Object> values){

        FieldName targetName = this.evaluator.getTargetFields().get(0).getName();
        List<InputField> inputFields = this.evaluator.getInputFields();

        Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();

        for(int i=0; i < inputFields.size(); i++){
            InputField inputField = inputFields.get(i);
            arguments.put(inputField.getName(), inputField.prepare(Float.parseFloat(values.get(inputField.getName().toString()).toString())));
        }

        Map<FieldName, ?> results = this.evaluator.evaluate(arguments);// Extracting prediction
        Map<String, ?> resultRecord = EvaluatorUtil.decodeAll(results);

        long y = ((Integer)resultRecord.get(targetName.toString())).intValue();

        return y;
    }

    public Map<String, Object> transform(Map<String, Object> transaction){
        return transaction;
    }


}