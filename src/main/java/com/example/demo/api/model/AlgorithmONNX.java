package com.example.demo.api.model;

import java.lang.reflect.Array;
import java.nio.FloatBuffer;
import java.util.Collections;
import ai.onnxruntime.*;
import com.example.demo.utils.Utils;
import javax.lang.model.type.NullType;
import java.util.*;

public class AlgorithmONNX extends Algorithm {

    private OrtEnvironment env;
    private OrtSession.SessionOptions opts;
    private OrtSession session;

    public AlgorithmONNX(int id, String name, String description, String folder, String filename, int type){
        super(id, name, description, folder, filename, type);
    }

    public void loadAlgorithm() throws OrtException {
        this.env = OrtEnvironment.getEnvironment();
        this.opts = new OrtSession.SessionOptions();
        this.session = env.createSession(this.getModelFolder() + this.getFilename(), opts);
        this.loaded = Boolean.TRUE;
    }

    public long predict(Map<String, Object> values) throws OrtException{

        long[] shape = new long[] {1, 36, 1};

        FloatBuffer buffer = FloatBuffer.allocate(values.size());
        for (String s: values.keySet()) {
            buffer.put(Float.parseFloat(values.get(s).toString()));
        }
        buffer.rewind();

        OnnxTensor tensor = OnnxTensor.createTensor(env, buffer, shape);
        System.out.println(tensor.getInfo());

        OrtSession.Result result = session.run(Collections.singletonMap("transaction_input", tensor), Collections.singleton("isFraud"));

        OnnxTensor resultTensor = (OnnxTensor) result.get(0);
        float[][] outputValues = (float[][]) resultTensor.getValue();

        result.close();

        return (long) outputValues[0][0];
    }

    /*public long predict(Map<String, Object> values) throws OrtException{

        ArrayList<String> inputs = new ArrayList<>(values.keySet());

        ArrayList<OnnxTensor> tensors = new ArrayList<>(values.size());

        for(Object v : values.values()){
            FloatBuffer val =  FloatBuffer.allocate(1);
            val.put(Float.parseFloat(v.toString()));
            val.rewind();
            long[] s = {1, 1};
            tensors.add(OnnxTensor.createTensor(env, val, s));
        }

        Map<String, OnnxTensor> input_map = new LinkedHashMap<String, OnnxTensor>();
        for (int i=0; i < tensors.size(); i++) {
            input_map.put(inputs.get(i), tensors.get(i));
        }

        OrtSession.Result result = session.run(input_map);

        OnnxTensor resultTensor = (OnnxTensor) result.get(0);

        Object a = resultTensor.getValue();

        long[] outputValues = (long[]) resultTensor.getValue();

        result.close();

        return outputValues[0];
    }
*/
}
