package com.art1001.supply.entity;

public class CodeMsg {
    private int result;
    private String msg;

    /**
     * 通用结果
     * @return
     */
    public static CodeMsg succss=new CodeMsg(1,"success");

    public static CodeMsg SERVER_ERROR=new CodeMsg(0,"服务端异常");
    public static CodeMsg PASSWORD_EMPTY=new CodeMsg(0,"密码不能为空");
    public static CodeMsg MOBILE_EMPTY=new CodeMsg(0,"手机号不能为空");
    public static CodeMsg MOBILE_ERROR=new CodeMsg(0,"手机号格式错误");
    public static CodeMsg NO_USER = new CodeMsg(0,"用户不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(0,"密码错误");


    public CodeMsg fillArgs(Object...args){
        int result = this.result;
        String msg = String.format(this.msg,args);
        return new CodeMsg(result,msg);
    }

    private CodeMsg(int result, String msg){
        this.result = result;
        this.msg = msg;
    }



    public int getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "CodeMsg{" +
                "result=" + result +
                ",msg=" + msg +
                "}";
    }
}
