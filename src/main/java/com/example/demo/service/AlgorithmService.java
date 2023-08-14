package com.example.demo.service;

import com.example.demo.api.model.*;
import org.springframework.stereotype.Component;
import com.example.demo.utils.Utils;

import java.util.*;

@Component
public class AlgorithmService {
    private List<Algorithm> algorithmList;
    private Pipeline clf;

    public AlgorithmService(){

        this.algorithmList = new ArrayList<>();

        String modelFolder = "model";
        
        Map<String, Transformer> pipeline = new LinkedHashMap<>();
        pipeline.put("Base Tr", new BaseTransformer(Utils.dateList, Utils.toDrop));
        pipeline.put("Ordinal Encoder", new OrdinalEncoder("ordinal_encoder.json", modelFolder));
        pipeline.put("MMScaler", new Scaler("mmscaler.onnx", modelFolder));
        pipeline.put("Cos Transformer", new FunctionTransformer(Math::cos, Utils.cos_cols));
        pipeline.put("Sin Transformer", new FunctionTransformer(Math::sin, Utils.sin_cols));
        pipeline.put("STD Scaler", new Scaler("std.onnx", modelFolder));

        this.clf = new Pipeline(pipeline);

        this.clf.load();
        System.out.println("\nPipeline loaded.");

        

        Algorithm SVM = new AlgorithmPMML(Utils.SVM, "SVM","Support Vector Machine", modelFolder, "SVM.pmml", Utils.PMML);
        Algorithm LR = new AlgorithmPMML(Utils.LR, "LR","Logistic Regression", modelFolder, "LR.pmml", Utils.PMML);
        //Algorithm KNN = new AlgorithmPMML(Utils.KNN, "KNN","K Nearest Neighbour", modelFolder, "KNN.pmml", Utils.PMML);
        //Algorithm RF = new AlgorithmPMML(Utils.RANDOM_FOREST, "RF","Random Forest", modelFolder, "RF.pmml", Utils.PMML);
        Algorithm CNN = new AlgorithmONNX(Utils.CNN, "CNN", "Convolutional Neural Network", modelFolder,"CNN.onnx", Utils.ONNX);
        Algorithm AE = new AlgorithmONNX(Utils.AE, "AE", "Auto Encoder", modelFolder,"AE.onnx", Utils.ONNX);
        Algorithm DT = new AlgorithmPMML(Utils.DECISION_TREE, "DT", "Decision Tree", modelFolder,"DTree.pmml", Utils.PMML);
        Algorithm XGB = new AlgorithmPMML(Utils.XGB, "XGB", "Extreme Gradient Boosting", modelFolder,"XGB.pmml", Utils.PMML);
        Algorithm XT = new AlgorithmPMML(Utils.XT, "XT", "Extra Trees", modelFolder,"XT.pmml", Utils.PMML);
        Algorithm GNB = new AlgorithmPMML(Utils.GNB, "GNB", "Gaussian Naive Bayes", modelFolder,"GNB.pmml", Utils.PMML);
        Algorithm LDA = new AlgorithmPMML(Utils.LDA, "LDA", "Linear Discriminant Analysis", modelFolder,"LDA.pmml", Utils.PMML);
        //Algorithm KMEANS = new AlgorithmPMML(Utils.KMEANS, "KMEANS", "KMEANS", modelFolder, "KMEANS.pmml", Utils.PMML);
        //Algorithm LOF = new AlgorithmONNX(Utils.LOF, "LOF", "Local Outlier Factor", modelFolder,"LOF.onnx", Utils.ONNX);

        this.algorithmList.addAll(Arrays.asList(SVM, LR, DT, XGB, XT, GNB, LDA, CNN, AE));

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
        Optional<Algorithm> optional = Optional.empty();
        for(Algorithm algo: algorithmList){
            if(id == algo.getId()){
                optional = Optional.of(algo);
                return optional;
            }
        }
        return optional;
    }

    public Optional<Algorithm> getAlgorithm(String name){
        Optional<Algorithm> optional = Optional.empty();
        for(Algorithm algo: algorithmList){
            if(name.equals(algo.getName())){
                optional = Optional.of(algo);
                return optional;
            }
        }
        return optional;
    }

    public Pipeline getPipeline(){
        return this.clf;
    }
}

