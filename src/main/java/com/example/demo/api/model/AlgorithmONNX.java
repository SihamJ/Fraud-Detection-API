package com.example.demo.api.model;

import java.nio.FloatBuffer;
import java.util.Collections;
import ai.onnxruntime.*;
import org.json.JSONArray;

public class AlgorithmONNX extends Algorithm {

    private OrtEnvironment env;
    private OrtSession.SessionOptions opts;
    private OrtSession session;

    public AlgorithmONNX(int id, String name, String description, String filename, int type){
        super(id, name, description, filename, type);
    }

    public void loadAlgorithm() throws OrtException {
        this.env = OrtEnvironment.getEnvironment();
        this.opts = new OrtSession.SessionOptions();
        this.session = env.createSession("/home/sihartist/Desktop/JavaAPI/src/main/resources/models/" + this.getFilename(), opts);
        this.loaded = Boolean.TRUE;
    }

    public float predict(JSONArray values) throws OrtException{

        long[] shape = new long[] {1, 36, 1};

        FloatBuffer buffer = FloatBuffer.allocate(values.length());
        for (int i = 0; i < values.length(); i++) {
            buffer.put(values.getFloat(i));
        }
        buffer.rewind();

        OnnxTensor tensor = OnnxTensor.createTensor(env, buffer, shape);
        System.out.println(tensor.getInfo());

        OrtSession.Result result = session.run(Collections.singletonMap("conv1d_input", tensor), Collections.singleton("dense_1"));

        OnnxTensor resultTensor = (OnnxTensor) result.get(0);
        float[][] outputValues = (float[][]) resultTensor.getValue();

        result.close();

        return outputValues[0][0];
    }


}
