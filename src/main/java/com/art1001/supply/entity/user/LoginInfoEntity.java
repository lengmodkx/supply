package com.art1001.supply.entity.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LoginInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /*
     * 用户id
     */
    private String userId;
    /*
     * 用户账户
     */
    private String accountName;
    /*
     * 用户登录机器ip
     */
    private String loginIp;
    /*
     * 用户省份
     */
    private String province;
    /*
     * 用户城市
     */
    private String city;
    /*
     * 用户地域信息
     */
    private String region;
    /*
     * 用户登录时间
     */
    private Date loginTime;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

}
