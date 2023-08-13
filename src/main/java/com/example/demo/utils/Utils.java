package com.example.demo.utils;


import javax.json.*;
import java.util.*;
import java.util.function.Function;

public class Utils {

    public static final int     NB_ATTRIBUTES   = 36;

    public static final int 	SVM 			= 1;
    public static final int 	RANDOM_FOREST 		= 2;
    public static final int 	CNN 	= 3;
    public static final int 	DECISION_TREE 				= 4;
    public static final int     LR                          = 5;
    public static final int     KNN                         = 6;
    public static final int     XGB                         = 7;
    public static final int     XT                          = 8;
    public static final int     LDA                         = 9;
    public static final int     GNB                         = 10;
    public static final int     AE                          = 11;
    public static final int     KMEANS                      = 12;
    public static final int     LOF                         = 13;

    public static final int     MMSCALER                    = 50;

    public static final int     ONNX                        = 100;
    public static final int     PMML                        = 101;

    public static final String models_folder                = "/home/sihartist/Desktop/FraudDetection/src/main/resources/models/";
    public static final String preprocessing_folder                = "/home/sihartist/Desktop/FraudDetection/src/main/resources/models/";

    public static final String[] categorical_features       = {"V1", "V2", "V3", "V4", "V12", "V13", "V16", "V19", "V21", "V22", "V25", "V26", "V27"};
    public static final String[] toDrop                     = {"V5", "V11","V14","V15","V17","V18","V20", "CLASS"};
    public static final String[] dateList                   = {"V6", "V7", "V8", "V23"};

    public static final String[] order                     = {"V1", "V2", "V3", "V4", "V5", "V6", "V7", "V8", "V9", "V10", "V11", "V12", "V13", "V14", "V15",
                                                                "V16", "V17", "V18", "V19", "V20", "V21", "V22", "V23", "V24", "V25", "V26", "V27"};

    public static final String[] final_order               = {"COS_V6_day_", "COS_V6_month_",
                                                                "COS_V7_day_", "COS_V7_month_", "COS_V8_day_", "COS_V8_month_",
                                                                "COS_V23_day_", "COS_V23_month_", "SIN_V6_day_", "SIN_V6_month_",
                                                                "SIN_V7_day_", "SIN_V7_month_", "SIN_V8_day_", "SIN_V8_month_",
                                                                "SIN_V23_day_", "SIN_V23_month_", "V1", "V2", "V3", "V4", "V9",
                                                                "V10", "V12", "V13", "V16", "V19", "V21", "V22", "V24",
                                                                "V25", "V26", "V27", "V6_year", "V7_year", "V8_year", "V23_year"};

    public static final String[] cos_cols               = {"COS_V6_day_", "COS_V6_month_",
                                                            "COS_V7_day_", "COS_V7_month_", "COS_V8_day_", "COS_V8_month_",
                                                            "COS_V23_day_", "COS_V23_month_"};

    public static final String[] sin_cols               = {"SIN_V6_day_", "SIN_V6_month_",
                                                            "SIN_V7_day_", "SIN_V7_month_", "SIN_V8_day_", "SIN_V8_month_",
                                                            "SIN_V23_day_", "SIN_V23_month_"};

    public Utils(){

    }

    public static List<Object> toList(JsonArray array) throws JsonException {

        List<Object> list = new ArrayList<Object>();

        for (int i = 0; i < array.size(); i++) {
            Object value = array.get(i);
            if (value instanceof JsonArray) {
                value = toList((JsonArray) value);
            } else if (value instanceof JsonObject) {
                value = toMap((JsonObject) value);
            }
            else{
                value = Json.createValue(value.toString()).getString().replace('"',' ');
            }
            list.add(value);
        }
        return list;
    }

    public static Map<String, Object> toMap(JsonObject jsonobj)  throws JsonException {

        Map<String, Object> map = new LinkedHashMap<String, Object>();
        Iterator<String> keys = jsonobj.keySet().iterator();

        while(keys.hasNext()) {
            String key = keys.next();
            Object value = jsonobj.get(key);
            if (value instanceof JsonArray) {
                value = toList((JsonArray) value);
            } else if (value instanceof JsonObject) {
                value = toMap((JsonObject) value);
            }
            else{
                value = Json.createValue(value.toString()).getString().replace(String.valueOf('"'), "");
            }
            map.put(key, value);
        }

        return map;
    }

    public static Map<String, Object> sort(Map<String, Object> map, String[] order){
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        for(String s: order){
            result.put(s, map.get(s));
        }
        return result;
    }

    public static Map<String, Object> applyMap(Map<String, Object> map, String[] keys, Function<Double, Double> func){
        for(String k: keys){
            map.replace(k, func.apply( Double.valueOf(map.get(k).toString())));
        }
        return map;
    }

}

