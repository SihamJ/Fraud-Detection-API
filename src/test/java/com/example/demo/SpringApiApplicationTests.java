package com.example.demo;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.nio.FloatBuffer;
import java.util.Collections;
import ai.onnxruntime.*;
import org.json.JSONArray;

@SpringBootTest
class SpringApiApplicationTests {

	@Test
	void contextLoads() throws OrtException {
		OrtEnvironment env;
		OrtSession.SessionOptions opts;
		OrtSession session;
		env = OrtEnvironment.getEnvironment();
		opts = new OrtSession.SessionOptions();
		try {
			session = env.createSession("/home/sihartist/Desktop/fraud-detection/models/cnn.onnx", opts);
		}
		catch(Exception e){
			System.out.println("\nCan't load model CNN\n" + e.toString());
		}
	}

}
