package com.example.demo.service;

import com.example.demo.api.model.*;
import org.springframework.stereotype.Component;
import com.example.demo.utils.Utils;

import java.util.Optional;
import java.util.*;

@Component
public class AlgorithmService {
    private List<Algorithm> algorithmList;

    public AlgorithmService(){

        this.algorithmList = new ArrayList<>();

        Algorithm SVM = new AlgorithmPMML(Utils.SVM, "SVM","Support Vector Machine", Utils.models_folder, "svm.pmml", Utils.PMML);
        Algorithm RF = new AlgorithmPMML(Utils.RANDOM_FOREST, "RF","Random Forest", Utils.models_folder, "rf.pmml", Utils.PMML);
        Algorithm CNN = new AlgorithmONNX(Utils.CNN, "CNN", "Convolutional Neural Network", Utils.models_folder,"cnn.onnx", Utils.ONNX);
        Algorithm DT = new AlgorithmPMML(Utils.DECISION_TREE, "DT", "Decision Tree", Utils.models_folder,"dtree.pmml", Utils.PMML);

        this.algorithmList.addAll(Arrays.asList(CNN, SVM, RF, DT));
        for( Algorithm algo : this.algorithmList){
            try {
                algo.loadAlgorithm();
            }
            catch (Exception e){
                System.out.println("\nCan't load model " + algo.getName() + "\n" + e.toString());
            }
        }

        System.out.println("\nModels loaded.\n");
    }

    public Optional<Algorithm> getAlgorithm(Integer id){
        Optional optional = Optional.empty();
        for(Algorithm algo: algorithmList){
            if(id == algo.getId()){
                optional = Optional.of(algo);
                return optional;
            }
        }
        return optional;
    }

    public Optional<Algorithm> getAlgorithm(String name){
        Optional optional = Optional.empty();
        for(Algorithm algo: algorithmList){
            if(name.equals(algo.getName())){
                optional = Optional.of(algo);
                return optional;
            }
        }
        return optional;
    }
}
