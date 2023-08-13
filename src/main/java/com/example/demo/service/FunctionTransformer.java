package com.example.demo.service;

import com.example.demo.utils.Utils;

import java.util.Map;
import java.util.function.Function;

public class FunctionTransformer extends Transformer {

    protected Function<Double, Double> func;
    protected String[] columns;

    public FunctionTransformer(Function<Double, Double> func){
        super("", "");
        this.func = func;
        this.columns = Utils.final_order;
    }
    public FunctionTransformer(Function<Double, Double> func, String[] columns){
        super("", "");
        this.func = func;
        this.columns = columns;
    }

    public void load(){
        this.loaded = Boolean.TRUE;
    }

    public Map<String, Object> transform(Map<String, Object> data){
        return Utils.applyMap(data, this.columns, this.func);
    }

}

