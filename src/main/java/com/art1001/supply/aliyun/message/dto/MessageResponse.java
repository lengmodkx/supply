package com.art1001.supply.aliyun.message.dto;
import lombok.Data;

/**
 * @author heshaohua
 * @date 2019/11/21 14:22
 **/
@Data
public class MessageResponse {

    private String Message;

    private String RequestId;

    private String BizId;

    private String Code;

}
