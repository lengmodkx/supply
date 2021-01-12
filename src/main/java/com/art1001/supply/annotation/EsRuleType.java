package com.art1001.supply.annotation;

public enum EsRuleType {
    ARTICLE("article"),COMMEMT("comment"),DEFAULT("");
    private String name;

    EsRuleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
