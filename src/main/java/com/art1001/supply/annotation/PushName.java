package com.art1001.supply.annotation;

public enum PushName {
    TASK("task"),SHARE("share"),SCHEDULE("schedule"),FILE("file"),NEWS("news"),DEFAULT("");
    private String name;

    PushName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }
}
