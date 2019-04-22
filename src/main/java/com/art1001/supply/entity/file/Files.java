package com.art1001.supply.entity.file;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description
 * @Date:2019/4/20 18:48
 * @Author heshaohua
 **/
@Data
public class Files implements Serializable {

    private List<File> files;
}
