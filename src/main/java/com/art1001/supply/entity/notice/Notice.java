package com.art1001.supply.entity.notice;

import lombok.Data;

import java.io.Serializable;

@Data
public class Notice implements Serializable {

    private String type;

    private Object object;
}
