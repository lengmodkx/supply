package com.art1001.supply.entity;

import lombok.Data;

/**
 * 全部结果返回类
 * 2019/12/25
 * wangyafeng
 * @param <T>
 */
@Data
public class Result<T> {
    private int result;
    private String msg;

    private T data;

    private Result(T data){
        this.result = 1;
        this.msg = "success";
        this.data = data;
    }

    private Result(){
        this.result = 1;
        this.msg = "success";
    }

    private Result(CodeMsg msg){
        if(msg==null) {
            return;
        }
        this.result = msg.getResult();
        this.msg = msg.getMsg();
    }
    private Result(String msg){
        if(msg==null) {
            return;
        }
        this.result = 0;
        this.msg = msg;
    }

    /**
     * 成功是调用
     */
    public static <T> Result<T> success(){
        return new Result<>();
    }


    /**
     * 成功是调用
     */
    public static <T> Result<T> success(T data){
        return new Result<>(data);
    }

    /**
     * 失败时调用
     */
    public static <T> Result<T> fail(CodeMsg msg){
        return new Result<>(msg);
    }

    /**
     * 失败时调用
     */
    public static <T> Result<T> fail(String msg){
        return new Result<>(msg);
    }

    public int getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
