package com.example.restaurantes;

import org.json.JSONObject;

public class DatosConsulta {
    private String URL;
    private JSONObject json;

    public DatosConsulta(String url, JSONObject json){
        this.URL = url;
        this.json = json;
    }

    public String getURL(){
        return this.URL;
    }

    public JSONObject getJson(){
        return this.json;
    }
}
