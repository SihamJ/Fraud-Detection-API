package com.example.demo.service;

import com.example.demo.api.model.*;
import org.springframework.stereotype.Component;
import com.example.demo.utils.Utils;
import com.example.demo.service.Pipeline;
import java.util.Optional;
import java.util.*;

@Component
public class AlgorithmService {
    private List<Algorithm> algorithmList;
    private Pipeline clf;

    public AlgorithmService(){

        this.algorithmList = new ArrayList<>();

        Map<String, Transformer> pipeline = new LinkedHashMap<>();
        pipeline.put("Base Tr", new BaseTransformer(Utils.dateList, Utils.toDrop));
        pipeline.put("Ordinal Encoder", new OrdinalEncoder("ordinal_encoder.json", Utils.preprocessing_folder));
        pipeline.put("MMScaler", new Scaler("mmscaler.onnx", Utils.preprocessing_folder));
        pipeline.put("Cos Transformer", new FunctionTransformer(Math::cos, Utils.cos_cols));
        pipeline.put("Sin Transformer", new FunctionTransformer(Math::sin, Utils.sin_cols));
        pipeline.put("STD Scaler", new Scaler("std.onnx", Utils.preprocessing_folder));

        this.clf = new Pipeline(pipeline);

        this.clf.load();
        System.out.println("\nPipeline loaded.");

        Algorithm SVM = new AlgorithmPMML(Utils.SVM, "SVM","Support Vector Machine", Utils.models_folder, "SVM.pmml", Utils.PMML);
        Algorithm LR = new AlgorithmPMML(Utils.LR, "LR","Logistic Regression", Utils.models_folder, "LR.pmml", Utils.PMML);
        //Algorithm KNN = new AlgorithmPMML(Utils.KNN, "KNN","K Nearest Neighbour", Utils.models_folder, "KNN.pmml", Utils.PMML);
        Algorithm RF = new AlgorithmPMML(Utils.RANDOM_FOREST, "RF","Random Forest", Utils.models_folder, "RF.pmml", Utils.PMML);
        Algorithm CNN = new AlgorithmONNX(Utils.CNN, "CNN", "Convolutional Neural Network", Utils.models_folder,"CNN.onnx", Utils.ONNX);
        Algorithm AE = new AlgorithmONNX(Utils.AE, "AE", "Auto Encoder", Utils.models_folder,"AE.onnx", Utils.ONNX);
        Algorithm DT = new AlgorithmPMML(Utils.DECISION_TREE, "DT", "Decision Tree", Utils.models_folder,"DTree.pmml", Utils.PMML);
        Algorithm XGB = new AlgorithmPMML(Utils.XGB, "XGB", "Extreme Gradient Boosting", Utils.models_folder,"XGB.pmml", Utils.PMML);
        Algorithm XT = new AlgorithmPMML(Utils.XT, "XT", "Extra Trees", Utils.models_folder,"XT.pmml", Utils.PMML);
        Algorithm GNB = new AlgorithmPMML(Utils.GNB, "GNB", "Gaussian Naive Bayes", Utils.models_folder,"GNB.pmml", Utils.PMML);
        Algorithm LDA = new AlgorithmPMML(Utils.LDA, "LDA", "Linear Discriminant Analysis", Utils.models_folder,"LDA.pmml", Utils.PMML);
        //Algorithm KMEANS = new AlgorithmPMML(Utils.KMEANS, "KMEANS", "KMEANS", Utils.models_folder, "KMEANS.pmml", Utils.PMML);
        //Algorithm LOF = new AlgorithmONNX(Utils.LOF, "LOF", "Local Outlier Factor", Utils.models_folder,"LOF.onnx", Utils.ONNX);

        this.algorithmList.addAll(Arrays.asList(SVM, LR, DT, XGB, RF, XT, GNB, LDA, CNN, AE));

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

    public Pipeline getPipeline(){
        return this.clf;
    }
}
