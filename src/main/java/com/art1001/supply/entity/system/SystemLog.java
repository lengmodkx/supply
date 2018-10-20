package com.art1001.supply.entity.system;

import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author heshaohua
 * @since 2018-10-19
 */
@Data
public class SystemLog {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;

    /**
     * 请求的url
     */
    private String requestUrl;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 客户端ip
     */
    private String ip;

    /**
     * 请求的方法名称
     */
    private String methodName;

    /**
     * 传递参数
     */
    private String methodArgs;

    /**
     * 方法备注
     */
    private String note;

    /**
     * 方法执行时间
     */
    private String runTime;

    /**
     * 执行结果
     */
    private String runResult;
}
