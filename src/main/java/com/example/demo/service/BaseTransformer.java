package com.example.demo.service;

import ai.onnxruntime.OnnxTensor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class BaseTransformer extends Transformer {

    String[] dateList;
    String[] toDrop;

    public BaseTransformer(String[] dateList, String[] toDrop){
        super("", "");
        this.dateList = dateList;
        this.toDrop =toDrop;
    }

    public void load(){
        this.loaded = Boolean.TRUE;
    }

    public Map<String, Object> transform(Map<String, Object> data){
        try{
            // Cleaning
            for(String d: this.toDrop){
                data.remove(d);
            }

            // cast as float
            data.replace("V9", Float.parseFloat( (String) data.get("V9")));
            data.replace("V10", Float.parseFloat( (String) data.get("V10")));
            data.replace("V24", Float.parseFloat( (String) data.get("V24")));

            // convert date columns to correct format and type
            DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd/MM/yyyy") ;
            DateTimeFormatter f2 = DateTimeFormatter.ofPattern("yyyy-MM-dd") ;
            LocalDate V6 = LocalDate.parse( ((String) data.get("V6")).split(" ")[0], f2);
            LocalDate V7 = LocalDate.parse(((String) data.get("V7")).split(" ")[0], f2);
            LocalDate V8 = LocalDate.parse(((String) data.get("V8")).split(" ")[0], f1);
            LocalDate V23 = LocalDate.parse((String) data.get("V23"), f1);

            data.replace("V6", V6);
            data.replace("V7", V7);
            data.replace("V8", V8);
            data.replace("V23", V23);

            for(String dl: this.dateList){
                data.put( "COS_" + dl + "_day_", ( (LocalDate) data.get(dl)).getDayOfMonth());
                data.put( "COS_" + dl + "_month_", ( (LocalDate) data.get(dl)).getMonthValue());
                data.put( "SIN_" + dl + "_day_", ( (LocalDate) data.get(dl)).getDayOfMonth());
                data.put( "SIN_" + dl + "_month_", ( (LocalDate) data.get(dl)).getMonthValue());
                data.put( dl + "_year", ( (LocalDate) data.get(dl)).getYear());
                data.remove(dl);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return data;
    }
}
