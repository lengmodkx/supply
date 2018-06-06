package com.art1001.supply.entity.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wangyafeng
 * @ClassName: UserOnline
 * @Description: 用户在线信息
 * @date 2016年7月12日 下午2:29:40
 */
@Data
@Accessors(chain = true)
public class UserSessionEntity extends UserEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /*
     * Session Id
     */
    private String sessionId;
    /*
     * Session Host
     */
    private String host;
    /*
     * Session创建时间
     */
    private Date startTime;
    /*
     * Session最后交互时间
     */
    private Date lastAccess;
    /*
     * Session timeout
     */
    private long timeout;
    /*
     * session 是否踢出
     */
    private boolean sessionStatus = Boolean.TRUE;


    public UserSessionEntity() {
    }

    public UserSessionEntity(UserEntity user) {
        super(user);
    }


}
