package com.example.demo.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import com.example.demo.api.model.AlgorithmPMML;
import com.example.demo.utils.Utils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Scaler extends Transformer {

    private OrtEnvironment env;
    private OrtSession.SessionOptions opts;
    private OrtSession session;

    public Scaler(String filename, String folderName){
        super(filename, folderName);
    }

    public void load(){
        this.env = OrtEnvironment.getEnvironment();
        this.opts = new OrtSession.SessionOptions();
        try{
            String modelFolder = Scaler.class.getClassLoader().getResource(this.getFilename()).getPath();
            this.session = env.createSession(modelFolder, opts);
        }
        catch(OrtException e){
            System.out.println(e + "\n");
        }
        this.loaded = Boolean.TRUE;
    }

    public Map<String, Object> transform(Map<String, Object> data){

        ArrayList<String> inputs = new ArrayList<>(data.keySet());
        ArrayList<OnnxTensor> tensors = new ArrayList<>(data.size());

        for(Object v : data.values()){
            FloatBuffer val =  FloatBuffer.allocate(1);
            val.put(Float.parseFloat(v.toString()));
            val.rewind();
            long[] s = {1, 1};
            try{
                tensors.add(OnnxTensor.createTensor(env, val, s));
            }
            catch(OrtException e){
                e.printStackTrace();
            }
        }

        Map<String, OnnxTensor> input_map = new LinkedHashMap<>();
        for (int i=0; i < tensors.size(); i++) {
            input_map.put(inputs.get(i), tensors.get(i));
        }

        OrtSession.Result result = null;

        try{
            result = this.session.run(input_map);
        }
        catch(OrtException e){
            e.printStackTrace();
        }

        OnnxTensor resultTensor = (OnnxTensor) result.get(0);
        float[][] outputValues = null;

        try{
            outputValues = (float[][]) resultTensor.getValue();
        }
        catch(OrtException e)
        {
            e.printStackTrace();
        }

        result.close();

        Map<String, Object> new_data = new LinkedHashMap<>();

        for(int i=0; i < inputs.size(); i++){
            new_data.put(Utils.final_order[i], outputValues[0][i]);
        }

        return new_data;
    }
}
