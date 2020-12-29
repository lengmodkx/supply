package com.art1001.supply.util;

import com.art1001.supply.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Component
public class FollowUtil {

    @Resource
    RedisUtil redisUtil;

    private static final String FOLLOWING = "FOLLOWING_";
    private static final String FANS = "FANS_";
    private static final String COMMON_KEY = "COMMON_FOLLOWING";

    // 关注或者取消关注
    public int addOrRelease(String userId, String followingId) {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(followingId)) {
            return -1;
        }
        // 0 = 取消关注 1 = 关注
        int isFollow = 0;

        String followingKey = FOLLOWING + userId;
        String fansKey = FANS + followingId;

        Long rank = redisUtil.zRank(followingKey, followingId);
        // 说明userId没有关注过followingId
        if (rank == null) {
            redisUtil.zSet(followingKey, followingId, System.currentTimeMillis());
            redisUtil.zSet(fansKey, userId, System.currentTimeMillis());
            isFollow = 1;
        } else {//取消关注
            redisUtil.zRem(followingKey, followingId);
            redisUtil.zRem(fansKey, userId);
        }
        return isFollow;
    }

    /**
     * 验证两个用户之间的关系
     *
     * @param userId
     * @param otherUserId
     * @return 0=没关系 1=自己 2=userId关注了otherUserId 3=otherUserId是userId的粉丝 4=互相关注
     */
    public int checkRelations(String userId, String otherUserId) {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(otherUserId)) {
            return 0;
        }

        if (userId.equals(otherUserId)) {
            return 1;
        }

        int relation = 0;
        String followingKey = FOLLOWING + userId;
        String fansKey = FANS + otherUserId;
        // userId是否关注otherUserId
        if (redisUtil.zRank(followingKey, otherUserId) != null) {
            relation = 2;
        }
        // otherUserId是userId的粉丝
        if (redisUtil.zRank(fansKey, userId) != null) {
            relation = 3;
        }

        if (redisUtil.zRank(followingKey, otherUserId) != null && redisUtil.zRank(fansKey, userId) != null) {
            relation = 4;
        }

        return relation;
    }


    /**
     * 获取用户所有关注的人的id
     *
     * @param userId
     * @return
     */
    public Set<String> findFollwings(String userId) {
        return findSet(FOLLOWING + userId);
    }

    /**
     * 获取用户所有的粉丝
     *
     * @param userId
     * @return
     */
    public Set<String> findFans(String userId) {
        return findSet(FANS + userId);
    }

    /**
     * 获取两个共同关注的人
     *
     * @param userId
     * @param otherUserId
     * @return
     */
    public Set<String> findCommonFollowing(String userId, String otherUserId) {
        if (userId == null || otherUserId == null) {
            return new HashSet<>();
        }
        String commonKey = Constants.COMMON_KEY + userId + "_" + otherUserId;
        // 取交集
        redisUtil.zinterstore(commonKey + userId + "_" + otherUserId, FOLLOWING + userId, FOLLOWING + otherUserId);
        Set<String> result = redisUtil.zrange(commonKey, 0, -1);
        redisUtil.del(commonKey);
        return result;
    }

    /**
     * 根据key获取set
     *
     * @param key
     * @return
     */
    private Set<String> findSet(String key) {
        if (key == null) {
            return new HashSet<>();
        }
        // 按照score从大到小排序;
        return redisUtil.zrevrange(key, 0, -1);
    }

}
