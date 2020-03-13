package com.art1001.supply.service.recycle;

/**
 * @author shaohua
 * @date 2020/3/12 23:43
 */
public enum RecycleBinParamCheckEnum {

    /**
     * 检查任务参数的实例名称
     */
    CheckTaskParam("task", "checkTaskParam"),

    /**
     * 检查任务参数的实例名称
     */
    CheckFileParam("file", "checkFileParam"),

    /**
     * 检查分享参数的实例名称
     */
    CheckShareParam("share", "checkShareParam"),

    /**
     * 检查日程参数的实例名称
     */
    CheckScheduleParam("schedule", "checkScheduleParam"),

    /**
     * 检查标签参数的实例名称
     */
    CheckTagParam("tag", "checkTagParam"),

    ;

    /**
     * 业务类型编码
     */
    private String code;

    /**
     * 业务实例名称
     */
    private String name;

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 根据code获取实例Bean名称
     * @param code 实例编码
     * @return 实例名称
     */
    public static String getBeanName(String code){
        for (RecycleBinParamCheckEnum value : values()) {
            if(value.getCode().equals(code)){
                return value.getName();
            }
        }
        return null;
    }


    RecycleBinParamCheckEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
