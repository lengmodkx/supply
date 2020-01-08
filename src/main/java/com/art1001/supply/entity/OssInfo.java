package com.art1001.supply.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@ToString
@Component
public class OssInfo {

    private String accessId="LTAIP4MyTAbONGJx";

    private String policy;

    private String signature;

    private String dir;

    private String expire;

    private String callback;

    @JsonIgnore
    private String accessKey = "coCyCStZwTPbfu93a3Ax0WiVg3D4EW";

    @JsonIgnore
    private String endpoint="oss-cn-beijing.aliyuncs.com";

    @JsonIgnore
    private String bucket="art1001-bim-5d";

    private String host="https://" + bucket + "." + endpoint;

    @Value("${callback_url}")
    @JsonIgnore
    private String callbackUrl;
}
