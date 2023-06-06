package com.example.demo.service;
import java.util.Map;

public abstract class Transformer {

    protected String filename;
    protected String folderName;
    protected Boolean loaded;

    public Transformer(String filename, String folderName){
        this.filename = filename;
        this.folderName = folderName;
        this.loaded = Boolean.FALSE;
    }

    public String getFilename(){ return this.filename; }
    public void setFilename(String filename){ this.filename = filename;}
    public String getFolderName(){ return this.folderName;}
    public void setFolderName(String folderName){ this.folderName = folderName; }

    public abstract void load();
    public void fit(){
        System.out.println("\nFit method is not yet available.\n");
    };
    public abstract Map<String, Object> transform(Map<String, Object> data);
}
