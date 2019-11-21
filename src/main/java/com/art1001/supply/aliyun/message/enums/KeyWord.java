package com.art1001.supply.aliyun.message.enums;

public enum KeyWord {

    PREFIX("phoneCode:");

    private String codePrefix;


    KeyWord(String codePrefix) {
        this.codePrefix = codePrefix;
    }

    KeyWord() {
    }
}
