package com.example.demo.api.model;

import java.nio.FloatBuffer;
import ai.onnxruntime.*;
import java.util.*;
import java.util.stream.Collectors;

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

        String modelFolder = AlgorithmONNX.class.getClassLoader().getResource(this.getFilename()).getPath();

        //String file = IOUtils.toString(AlgorithmONNX.class.getClassLoader().getResourceAsStream(this.getFilename()));

        this.session = env.createSession(modelFolder, opts);
        this.loaded = Boolean.TRUE;
    }

    public long predict(Map<String, Object> values) throws OrtException{

        long[] shape = ((TensorInfo) session.getInputInfo().get("transaction_input").getInfo()).getShape();
        for(int i = 0; i < shape.length; i++){
            if(shape[i] == -1){
                shape[i] = 1;
            }
        }

        FloatBuffer buffer = FloatBuffer.allocate(values.size());
        for (String s: values.keySet()) {
            buffer.put(Float.parseFloat(values.get(s).toString()));
        }
        buffer.rewind();

        OnnxTensor tensor = OnnxTensor.createTensor(env, buffer, shape);

        OrtSession.Result result = session.run(Collections.singletonMap((session.getInputNames().stream()).collect(Collectors.toList()).get(0), tensor), Collections.singleton(session.getOutputNames().stream().collect(Collectors.toList()).get(0)));

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
