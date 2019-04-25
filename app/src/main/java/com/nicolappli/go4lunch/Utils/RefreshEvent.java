package com.nicolappli.go4lunch.Utils;

public class RefreshEvent {

    private String query;

    public RefreshEvent(String query){
        this.query = query;
    }

    public String getQuery(){
        return query;
    }
}
