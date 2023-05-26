package com.example.demo.service;

import com.example.demo.api.model.AlgorithmONNX;
import com.example.demo.utils.Utils;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDate;
import ai.onnxruntime.*;

public class Preprocessing {
    private String folder;
    private Path scalerPath;
    private String encoderPath;
    protected String scalerFile;
    protected String stdScalerFile;
    protected String encoderFile;
    protected Boolean loaded;
    Map<String, Object> encoder_map;

    AlgorithmONNX min_max_scaler;
    AlgorithmONNX std_scaler;

    public Preprocessing(String scalerFile, String encoderFile, String stdScalerFile){
        this.scalerFile = scalerFile;
        this.encoderFile = encoderFile;
        this.stdScalerFile = stdScalerFile;
        this.loaded = Boolean.FALSE;
        this.folder = Utils.preprocessing_folder;
    }

    public void load() {
        try{
            this.loadScaler();
            this.loadEncoder();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void loadScaler()  throws OrtException {
        try{
            this.min_max_scaler = new AlgorithmONNX(Utils.MMSCALER, "MMScaler", "Min Max Scaler", this.getFolder(), this.getScalerFile(), Utils.ONNX);
            this.min_max_scaler.loadAlgorithm();

            this.std_scaler = new AlgorithmONNX(Utils.MMSCALER, "STDScaler", "STD Scaler", this.getFolder(), this.getSTDScalerFile(), Utils.ONNX);
            this.std_scaler.loadAlgorithm();
        }
        catch(OrtException e){
            e.printStackTrace();
        }
    }

    public void loadEncoder()   {

        this.encoderPath = this.folder + this.getEncoderFile();

        try (JsonReader reader = Json.createReader(new FileReader(this.encoderPath))) {
            JsonObject json_encoder = reader.readObject();
            this.encoder_map = Utils.toMap(json_encoder);
            this.loaded = Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> encode(Map<String, Object> transaction){

        for(String c : Utils.categorical_features){
            Map c_map = (HashMap) this.encoder_map.get(c);
            String current_value =  (String) transaction.get(c);
            int encoded_value = Integer.parseInt((String) c_map.get(current_value));
            transaction.replace(c, encoded_value);
        }
        return transaction;
    }

    public Map<String, Object> encode(Map<String, Object> transaction, String[] features){

        for(String c : features){
            Map c_map = (HashMap) this.encoder_map.get(c);
            String current_value =  (String) transaction.get(c);
            int encoded_value = Integer.parseInt((String) c_map.get(current_value));
            transaction.replace(c, encoded_value);
        }
        return transaction;
    }

    public Map<String, Object> baseTransformer(Map<String, Object> transaction)  {

        try{
            // Cleaning
            for(String d: Utils.toDrop){
                transaction.remove(d);
            }

            // cast as float
            transaction.replace("V9", Float.parseFloat( (String) transaction.get("V9")));
            transaction.replace("V10", Float.parseFloat( (String) transaction.get("V10")));
            transaction.replace("V24", Float.parseFloat( (String) transaction.get("V24")));

            // convert date columns to correct format and type
            DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd/MM/yyyy") ;
            DateTimeFormatter f2 = DateTimeFormatter.ofPattern("yyyy-MM-dd") ;

            LocalDate V6 = LocalDate.parse( ((String) transaction.get("V6")).split(" ")[0], f2);
            LocalDate V7 = LocalDate.parse(((String) transaction.get("V7")).split(" ")[0], f2);
            LocalDate V8 = LocalDate.parse(((String) transaction.get("V8")).split(" ")[0], f1);
            LocalDate V23 = LocalDate.parse((String) transaction.get("V23"), f1);

            transaction.replace("V6", V6);
            transaction.replace("V7", V7);
            transaction.replace("V8", V8);
            transaction.replace("V23", V23);

            for(String dl: Utils.dateList){
                transaction.put( "COS_" + dl + "_day_", ( (LocalDate) transaction.get(dl)).getDayOfMonth());
                transaction.put( "COS_" + dl + "_month_", ( (LocalDate) transaction.get(dl)).getMonthValue());
            }

            for(String dl: Utils.dateList){
                transaction.put( "SIN_" + dl + "_day_", ( (LocalDate) transaction.get(dl)).getDayOfMonth());
                transaction.put( "SIN_" + dl + "_month_", ( (LocalDate) transaction.get(dl)).getMonthValue());
            }

            for(String dl: Utils.dateList){
                transaction.put( dl + "_year", ( (LocalDate) transaction.get(dl)).getYear());
                transaction.remove(dl);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return transaction;
    }
    public Map<String, Object> baseTransformer(Map<String, Object> transaction, String[] dateList, String[] toDrop) throws ParseException {

        try{
            // Cleaning
            for(String d: toDrop){
                transaction.remove(d);
            }

            // cast as float
            transaction.replace("V9", Float.parseFloat( (String) transaction.get("V9")));
            transaction.replace("V10", Float.parseFloat( (String) transaction.get("V10")));
            transaction.replace("V24", Float.parseFloat( (String) transaction.get("V24")));

            // convert date columns to correct format and type
            DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd/MM/yyyy") ;
            DateTimeFormatter f2 = DateTimeFormatter.ofPattern("yyyy-MM-dd") ;
            LocalDate V6 = LocalDate.parse( ((String) transaction.get("V6")).split(" ")[0], f2);
            LocalDate V7 = LocalDate.parse(((String) transaction.get("V7")).split(" ")[0], f2);
            LocalDate V8 = LocalDate.parse(((String) transaction.get("V8")).split(" ")[0], f1);
            LocalDate V23 = LocalDate.parse((String) transaction.get("V23"), f1);

            transaction.replace("V6", V6);
            transaction.replace("V7", V7);
            transaction.replace("V8", V8);
            transaction.replace("V23", V23);

            for(String dl: dateList){
                transaction.put( "COS_" + dl + "_day_", ( (LocalDate) transaction.get(dl)).getDayOfMonth());
                transaction.put( "COS_" + dl + "_month_", ( (LocalDate) transaction.get(dl)).getMonthValue());
                transaction.put( "SIN_" + dl + "_day_", ( (LocalDate) transaction.get(dl)).getDayOfMonth());
                transaction.put( "SIN_" + dl + "_month_", ( (LocalDate) transaction.get(dl)).getMonthValue());
                transaction.put( dl + "_year", ( (LocalDate) transaction.get(dl)).getYear());
                transaction.remove(dl);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return transaction;
    }

    public Map<String, Object> scale(Map<String, Object> transaction) throws OrtException{

        try{
            transaction = this.min_max_scaler.transform(transaction);
        }
        catch(OrtException e){
            e.printStackTrace();
        }
        return transaction;
    }

    public Map<String, Object> stdScale(Map<String, Object> transaction) throws OrtException{

        try{
            transaction = this.std_scaler.transform(transaction);
        }
        catch(OrtException e){
            e.printStackTrace();
        }
        return transaction;
    }

    public Map<String, Object> transform(Map<String, Object> transaction) {

        try{
            transaction = this.baseTransformer(transaction);
            transaction = this.encode(transaction);
            transaction = this.scale(transaction);
            transaction = Utils.applyMap(transaction, Utils.cos_cols, Math::cos);
            transaction = Utils.applyMap(transaction, Utils.sin_cols, Math::sin);
            System.out.println(transaction);
            transaction = this.stdScale(transaction);
            System.out.println(transaction);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return transaction;
    }
    public String getFolder() {
        return this.folder;
    }
    public String getScalerFile() {
        return this.scalerFile;
    }
    public String getEncoderFile() {
        return this.encoderFile;
    }

    public String getSTDScalerFile(){
        return this.stdScalerFile;
    }
}
