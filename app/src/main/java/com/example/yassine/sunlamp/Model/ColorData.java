package com.example.yassine.sunlamp.Model;

import com.example.yassine.sunlamp.Utils;

import java.io.Serializable;

/**
 * Created by YassIne on 09/08/2015.
 */
public class ColorData implements Serializable {
    protected String hexData ;
    protected int id;
    protected boolean mFavorite = false;
    protected String description = null;
    protected String name = null;
    protected String creation_time;
    protected String position = null;
    protected String imagePath = null;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHexData(){
        return this.hexData;
    }

    /**
     * Costruttore vuoto
     */
    public ColorData(){
}

    // in caso si vogia usare un array di BYTEsS
    public ColorData (String data){
        this.hexData = data;
        //this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setFavorite(boolean favorite){
        this.mFavorite = favorite;
    }

    public boolean isFavorite(){
        return mFavorite;
    }

    public void setData(String data){
        this.hexData = data;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String date) {
        this.creation_time = date;
    }
}
