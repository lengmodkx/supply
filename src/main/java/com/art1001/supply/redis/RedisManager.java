package com.art1001.supply.redis;

import com.art1001.supply.util.SerializeUtil;
import org.apache.shiro.session.Session;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.*;

/**
 * @author wangyafeng
 * 2016年12月13日 下午1:10:16
 */

public class RedisManager {

	private JedisPool getJedisPool() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(600);
		config.setMaxIdle(300);
		config.setMaxWaitMillis(1000);
		config.setTimeBetweenEvictionRunsMillis(30000);
		config.setMinEvictableIdleTimeMillis(30000);
		config.setSoftMinEvictableIdleTimeMillis(30000);
		config.setTestOnBorrow(true);
//        JedisPool jedisPool = new JedisPool(config, "supply.redis.cache.chinacloudapi.cn",6379,10000,"fcupAgxfZTjWbeZ9ZJHM1Vv6e24+72keQswYpvDwikQ=");
		JedisPool jedisPool = new JedisPool(config, "localhost");
		return jedisPool;
	}

	public Jedis getJedis() throws JedisConnectionException {
        Jedis jedis = null;
        try {
            jedis = getJedisPool().getResource();
        } catch (Exception e) {
            throw new JedisConnectionException(e);
        }
        return jedis;
    }


	/**
	 *
	 * @param dbIndex	db index
	 * @param key	key
	 * @return	byte[] byte[]
	 * @throws Exception	异常信息
	 */
    public byte[] getValueByKey(int dbIndex, byte[] key) {
        Jedis jedis = null;
        byte[] result = null;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            result = jedis.get(key);
        } catch (Exception e) {
            throw e;
        } finally {
        	if(null != jedis)
        	{
        		jedis.close();
        	}
        }
        return result;
    }

    /**
     *
     * @param dbIndex db index
     * @param key	key
     * @return	影响个数
     * @throws Exception	异常信息
     */
    public long deleteByKey(int dbIndex, byte[] key){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            Long result = jedis.del(key);
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
        	if(null != jedis)
        	{
        		jedis.close();
        	}
        }
    }

    /**
     * set值
     * @param dbIndex	db index
     * @param key	key
     * @param value	value
     * @param expireTime	过期时间
     * @throws Exception	异常信息
     */
    public void saveValueByKey(int dbIndex, byte[] key, byte[] value, int expireTime) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            jedis.set(key, value);
            if (expireTime > 0)
			{
				jedis.expire(key, expireTime);
			}
        } catch (Exception e) {
            throw e;
        } finally {
        	if(null != jedis)
        	{
        		jedis.close();
        	}
        }
    }


	/**
	 * 获取所有Session
	 * @param dbIndex	db index
	 * @param redisShiroSession	key
	 * @return	session集合
	 * @throws Exception	异常信息
	 */
	@SuppressWarnings("unchecked")
	public Collection<Session> allSession(int dbIndex, String redisShiroSession)  {
		Jedis jedis = null;
        Set<Session> sessions = new HashSet<Session>();
		try {
            jedis = getJedis();
            jedis.select(dbIndex);
            Set<byte[]> byteKeys = jedis.keys(redisShiroSession.getBytes());
            if (byteKeys != null && byteKeys.size() > 0) {
                for (byte[] bs : byteKeys) {
                	Session obj = SerializeUtil.deserialize(jedis.get(bs),
                    		 Session.class);
                     if(obj instanceof Session){
                    	 sessions.add(obj);
                     }
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
        	if(null != jedis)
        	{
        		jedis.close();
        	}
        }
        return sessions;
	}


	/**
	 * 简单的Get
	 * @param key	key
	 * @param requiredType
	 * @return	T required Type Object
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key , Class<T>...requiredType)  {
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			return SerializeUtil.deserialize(jds.get(key.getBytes()),requiredType);
        } catch (Exception e) {
            throw e;
        } finally {
        	if(jds != null)
			{
				jds.close();
			}
        }
	}
	/**
	 * 简单的set
	 * @param key	key
	 * @param value	value
	 */
	public void set(String key ,Object value){
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			byte[] svalue = SerializeUtil.serialize(value);
			jds.set(key.getBytes(), svalue);
        } catch (Exception e) {
            throw e;
        } finally {
        	if(jds != null)
			{
				jds.close();
			}
        }
	}

	/**
	 * 设置有过期时间的存取
	 * @param key	key
	 * @param value	value
	 * @param timer （秒）	过期时间
	 */
	public void setex(String key, Object value, int timer)  {
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			byte[] svalue = SerializeUtil.serialize(value);
			jds.setex(key.getBytes(), timer, svalue);
        } catch (Exception e) {
            throw e;
        } finally {
        	if(jds != null)
			{
				jds.close();
			}
        }

	}

	/**
	 *
	 * @param mapkey map
	 * @param key	 map里的key
	 * @param requiredType value的泛型类型
	 * @return	T 对象
	 */
	@SuppressWarnings("unchecked")
	public  <T> T getVByMap(String mapkey,String key , Class<T> requiredType) {
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			List<byte[]> result = jds.hmget(mapkey.getBytes(), key.getBytes());
			if(null != result && result.size() > 0 ){
				byte[] x = result.get(0);
				T resultObj = SerializeUtil.deserialize(x, requiredType);
				return resultObj;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if(jds != null)
			{
				jds.close();
			}
		}
		return null;
	}
	/**
	 *
	 * @param mapkey map
	 * @param key	 map里的key
	 * @param value   map里的value
	 */
	public  void setVByMap(String mapkey,String key ,Object value) {
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			byte[] svalue = SerializeUtil.serialize(value);
			jds.hset(mapkey.getBytes(), key.getBytes(),svalue);
        } catch (Exception e) {
        	throw e;
        } finally {
        	if(jds != null)
			{
				jds.close();
			}
        }

	}
	/**
	 * 删除Map里的值
	 * @param mapKey	key
	 * @param dkey	dkey
	 * @return	影响个数
	 */
	public Object delByMapKey(String mapKey ,String...dkey) {
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			byte[][] dx = new byte[dkey.length][];
			for (int i = 0; i < dkey.length; i++) {
				dx[i] = dkey[i].getBytes();
			}
			Long result = jds.hdel(mapKey.getBytes(), dx);
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			if(jds != null)
			{
				jds.close();
			}
		}
	}

	/**
	 * 从redis里取set整个集合
	 * @param setKey	key
	 * @param requiredType requiredType
	 * @return	set set集合
	 * @throws Exception	异常信息
	 */
	@SuppressWarnings("unchecked")
	public <T> Set<T> getVByList(String setKey,Class<T> requiredType) {
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			Set<T> set = new TreeSet<T>();
			Set<byte[]> xx = jds.smembers(setKey.getBytes());
			for (byte[] bs : xx) {
				T t = SerializeUtil.deserialize(bs, requiredType);
				set.add(t);
			}
			return set;
		} catch (Exception e) {
			throw e;
		} finally {
			if(jds != null)
			{
				jds.close();
			}
		}
	}
	/**
	 * 获取Set长度
	 * @param setKey	key
	 * @return	set长度
	 */
	public Long getLenBySet(String setKey){
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			Long result = jds.scard(setKey);
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			if(jds != null)
			{
				jds.close();
			}
		}
	}
	/**
	 * 删除Set
	 * @param dkey	key
	 * @return	影响的行个数
	 */
	public Long delSetByKey(String key,String...dkey){
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			Long result = 0L;
			if(null == dkey){
				result = jds.srem(key);
			}else{
				result = jds.del(key);
			}
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			if(jds != null)
			{
				jds.close();
			}
		}
	}
	/**
	 * 随机 Set 中的一个值
	 * @param key	key
	 * @return	value
	 */
	public String srandmember(String key) {
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			String result = jds.srandmember(key);
			return result;
		} catch (Exception e){
			throw e;
		} finally {
			if(jds != null)
			{
				jds.close();
			}
		}
	}
	/**
	 * 往redis里存Set
	 * @param setKey	key
	 * @param value	value
	 */
	public void setVBySet(String setKey,String value){
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			jds.sadd(setKey, value);
		} catch (Exception e) {
			throw e;
        } finally {
        	if(jds != null)
			{
				jds.close();
			}
        }
	}
	/**
	 * 取set
	 * @param key	leu
	 * @return set	set集合
	 */
	public Set<String> getSetByKey(String key) {
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			Set<String> result = jds.smembers(key);
			return result;
		} catch (Exception e) {
			throw e;
        } finally {
        	if(jds != null)
			{
				jds.close();
			}
        }

	}


	/**
	 * 往redis里存List
	 * @param listKey	key
	 * @param value	value
	 */
	public void setVByList(String listKey,Object value){
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			byte[] svalue = SerializeUtil.serialize(value);
			jds.rpush(listKey.getBytes(), svalue);
		} catch (Exception e) {
			throw e;
		} finally {
			if(jds != null)
			{
				jds.close();
			}
		}
	}
	/**
	 * 从redis里取list
	 * @param listKey	listkey
	 * @param start	start
	 * @param end	end
	 * @param requiredType requiredType
	 * @return	list
	 * @throws Exception	异常信息
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getVByList(String listKey,int start,int end,Class<T> requiredType) {
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			List<T> list = new ArrayList<T>();
			List<byte[]> xx = jds.lrange(listKey.getBytes(),start,end);
			for (byte[] bs : xx) {
				T t = SerializeUtil.deserialize(bs, requiredType);
				list.add(t);
			}
			return list;
		} catch (Exception e) {
			throw e;
		} finally {
			if(jds != null)
			{
				jds.close();
			}
		}
	}
	/**
	 * 获取list长度
	 * @param listKey	key
	 * @return	listKey长度
	 */
	public Long getLenByList(String listKey) {
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			Long result = jds.llen(listKey.getBytes());
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			if(jds != null)
			{
				jds.close();
			}
		}
	}
	/**
	 * 删除
	 * @param dkey	待删除的key数组
	 * @return	返回影响个数
	 */
	public Long delByKey(String...dkey) {
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			byte[][] dx = new byte[dkey.length][];
			for (int i = 0; i < dkey.length; i++) {
				dx[i] = dkey[i].getBytes();
			}
			Long result = jds.del(dx);
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			if(jds != null)
			{
				jds.close();
			}
		}
	}
	/**
	 * 判断是否存在
	 * @param existskey	判断existskey是否存在
	 * @return	return true if existskey exists,else false;
	 */
	public boolean exists(String existskey)  {
		Jedis jds = null;
		try {
			jds = getJedis();
			jds.select(0);
			return jds.exists(existskey.getBytes());
		} catch (Exception e) {
			throw e;
		} finally {
			if(jds != null)
			{
				jds.close();
			}
		}
	}
	
}
