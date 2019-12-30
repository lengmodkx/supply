package com.art1001.supply.common;

public class Constants {

    /**
     * 会员头像上传路径
     */
    public final static String MEMBER_IMAGE_URL = "upload/avatar/";

    public final static String PROJECT_IMG = "upload/project/";
    // TODO: 2018/6/20 开发环境
    public static final String OSS_URL = "https://art1001-bim-5d.oss-cn-beijing.aliyuncs.com/";

    public static final String TASK = "任务";
    public static final String FILE = "文件";
    public static final String SHARE = "分享";
    public static final String SCHEDULE  = "日程";
    public static final String GROUP_CHAT = "群聊";

    public static final String EXECUTOR = "执行者";
    public static final String PARTICIPANT = "参与者";



    public static final String TAG_NAME = "标签";
    public static final String TASK_GROUP = "任务分组";
    public static final String FOLDER = "文件夹";

    public static final String EXECUTE = "execute";
    public static final String JOIN = "join";

    //用于筛选 " 我的" 模块排序规则
    public static final String PROJECT = "project";
    public static final String DUE_DATE = "dueDate";
    public static final String PRIORITY = "priority";
    public static final String CREATED = "created";

    public static final String STAR ="star";

    //标识 0,1
    public static final String ZERO ="0";
    public static final String ONE ="1";

    //模块小写英文
    public static final String TASK_EN = "task";
    public static final String FILE_EN = "file";
    public static final String SCHEDULE_EN = "schedule";
    public static final String SHARE_EN = "share";
    public static final String TAG_EN ="tag";
    public static final String GROUP_EN ="group";

    public static final String MEMBER_EN = "member";

    //角色中文名称
    public static final String OWNER_CN = "拥有者";
    public static final String ADMIN_CN = "管理员";
    public static final String MEMBER_CN = "成员";

    //角色key
    public static final String OWNER_KEY = "administrator";
    public static final String ADMIN_KEY = "admin";
    public static final String MEMBER_KEY = "member";

    //redis资源前缀
    public static final String PRO_SOURCES_PREFIX = "proResources::";

    //字符串分隔符
    public static final String SPLIST_COMMA = ",";

    //文件素材库id
    public static final String MATERIAL_BASE = "ef6ba5f0e3584e58a8cc0b2d28286c93";

    public static final String WE_CHAT_SESSION_KEY_PRE = "weChatAppLogin:";

    //===================================================== Redis prefix ==============================================

    public static final String USER_INFO = "userInfo:";

}
