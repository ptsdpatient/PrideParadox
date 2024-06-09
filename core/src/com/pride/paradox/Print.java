package com.pride.paradox;

import com.badlogic.gdx.Gdx;

public class Print {
    static int index=0;
    public static void print(String value){
        Gdx.app.log(index+"",value);
        index++;
    }
    public static void print(Integer value){
        Gdx.app.log(index+"",value+"");
        index++;
    }
    public static void print(Float value){
        Gdx.app.log(index+"",value+"");
        index++;
    }
    public static void print(Character value){
        Gdx.app.log(index+"",value+"");
        index++;
    }
    public static void print(Double value){
        Gdx.app.log(index+"",value+"");
        index++;
    }
}
