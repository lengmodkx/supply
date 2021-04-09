package com.art1001.supply.service.recycle;

/**
 * @author heshaohua
 * @date 2020年03月12日 11:14:00
 * 移入回收站/恢复信息的业务类名称枚举
 */
public enum RecycleBinImplNameEnum {

    /**
     * 任务回收站的业务实例名称
     */
    TaskRecycleBin("task", "taskRecycleBinImpl"),

    /**
     * 文件回收站的业务实例名称
     */
    FileRecycleBin("file", "fileRecycleBinImpl"),

    FileRecycleBin1("folder", "fileRecycleBinImpl"),
    /**
     * 分享回收站的业务实例名称
     */
    ShareRecycleBin("share", "shareRecycleBinImp"),

    /**
     * 日程回收站的业务实例名称
     */
    ScheduleRecycleBin("schedule", "scheduleRecycleBinImpl"),

    /**
     * 标签回收站的业务实例名称
     */
    TagRecycleBin("tag", "tagRecycleBinImpl"),;

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
        for (RecycleBinImplNameEnum value : values()) {
            if(value.getCode().equals(code)){
                return value.getName();
            }
        }
        return null;
    }


    RecycleBinImplNameEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
