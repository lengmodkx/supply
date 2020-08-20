package com.art1001.supply.enums;

public enum TaskRepeat {

    NO_REPEAT("不重复"),
    DAY_REPEAT ("每天重复"),
    WEEK_REPEAT("每周重复"),
    MONTH_REPEAT("每月重复"),
    YEAR_REPEAT("每年重复"),
    WORKING_DAY_REPEAT("工作日重复");

    private String name;

    TaskRepeat(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
