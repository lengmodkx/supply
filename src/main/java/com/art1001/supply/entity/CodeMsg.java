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
    public static CodeMsg ACCOUNT_OR_PASSWORD_ERROR = new CodeMsg(0,"账号或密码错误");

    public static CodeMsg CAPTCHA_ERROR = new CodeMsg(0,"验证码错误");
    public static CodeMsg REGISTER_FAIL = new CodeMsg(0,"注册失败");
    public static CodeMsg USER_NO = new CodeMsg(0,"用户不存在，请检查");
    public static CodeMsg CAPTCHA_NO_USE = new CodeMsg(0,"验证码已经失效");


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
