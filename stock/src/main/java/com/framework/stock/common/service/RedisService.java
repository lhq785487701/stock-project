package com.framework.stock.common.service;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import com.framework.stock.utils.StringUtils;

@ConditionalOnClass({ RedisTemplate.class })
@Component
@SuppressWarnings({ "rawtypes", "unchecked" })
public class RedisService {

	@Autowired
	private RedisTemplate redisTemplate;

	//获取锁尝试时间
    private final long EXPIRE_TIME = 5 * 10000;

	public boolean set(String key, Object value) {
		boolean result = false;
		try {
			ValueOperations<Serializable, Object> operations = this.redisTemplate.opsForValue();
			operations.set(key, value);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean set(String key, Object value, Long expireTime) {
		boolean result = false;
		try {
			ValueOperations<Serializable, Object> operations = this.redisTemplate.opsForValue();
			operations.set(key, value);
			this.redisTemplate.expire(key, expireTime.longValue(), TimeUnit.SECONDS);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void remove(String... keys) {
		for (String key : keys) {
			remove(key);
		}
	}

	public void removePattern(String pattern) {
		Set<Serializable> keys = this.redisTemplate.keys(pattern);
		if (keys.size() > 0) {
			this.redisTemplate.delete(keys);
		}
	}

	public void remove(String key) {
		if (exists(key)) {
			this.redisTemplate.delete(key);
		}
	}

	public boolean exists(String key) {
		return this.redisTemplate.hasKey(key).booleanValue();
	}

	public Object get(String key) {
		Object result = null;
		ValueOperations<Serializable, Object> operations = this.redisTemplate.opsForValue();
		result = operations.get(key);
		return result;
	}

	public void hmSet(String key, Object hashKey, Object value) {
		HashOperations<String, Object, Object> hash = this.redisTemplate.opsForHash();
		hash.put(key, hashKey, value);
	}

	public Object hmGet(String key, Object hashKey) {
		HashOperations<String, Object, Object> hash = this.redisTemplate.opsForHash();
		return hash.get(key, hashKey);
	}

	public void lPush(String k, Object v) {
		ListOperations<String, Object> list = this.redisTemplate.opsForList();
		list.rightPush(k, v);
	}

	public Object leftPop(String k) {
		ListOperations<String, Object> list = this.redisTemplate.opsForList();
		return list.leftPop(k);
	}

	public Object rightPop(String k) {
		ListOperations<String, Object> list = this.redisTemplate.opsForList();
		return list.rightPop(k);
	}

	public Object leftPop(String k, long timeout, TimeUnit unit) {
		ListOperations<String, Object> list = this.redisTemplate.opsForList();
		return list.leftPop(k, timeout, unit);
	}

	public Object rightPop(String k, long timeout, TimeUnit unit) {
		ListOperations<String, Object> list = this.redisTemplate.opsForList();
		return list.rightPop(k, timeout, unit);
	}

	public List<Object> lRange(String k, long l, long l1) {
		ListOperations<String, Object> list = this.redisTemplate.opsForList();
		return list.range(k, l, l1);
	}

	public void add(String key, Object value) {
		SetOperations<String, Object> set = this.redisTemplate.opsForSet();
		set.add(key, new Object[] { value });
	}

	public Set<Object> setMembers(String key) {
		SetOperations<String, Object> set = this.redisTemplate.opsForSet();
		return set.members(key);
	}

	public void zAdd(String key, Object value, double scoure) {
		ZSetOperations<String, Object> zset = this.redisTemplate.opsForZSet();
		zset.add(key, value, scoure);
	}

	public Set<Object> rangeByScore(String key, double scoure, double scoure1) {
		ZSetOperations<String, Object> zset = this.redisTemplate.opsForZSet();
		return zset.rangeByScore(key, scoure, scoure1);
	}

	public void expire(Object key, long timeout, TimeUnit unit) {
		this.redisTemplate.expire(key, timeout, unit);
	}

	public RedisTemplate getRedisTemplate() {
		return this.redisTemplate;
	}

	 /**
     * Redis加锁的操作
     *
     * @param key
     * @param timeout 获取锁的等待时间
     * @return
     */
    public Boolean tryLock(String key, long timeout) {
    	String value = System.currentTimeMillis() + timeout + "";
        if (this.redisTemplate.opsForValue().setIfAbsent(key, value, EXPIRE_TIME, TimeUnit.SECONDS)) {
            return true;
        }
        String currentValue = (String) this.redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotEmpty(currentValue) && Long.valueOf(currentValue) < System.currentTimeMillis()) {
            String oldValue = (String) this.redisTemplate.opsForValue().getAndSet(key, value);
            if (StringUtils.isNotEmpty(oldValue) && oldValue.equals(currentValue)) {
                return true;
            }
        }
        return false;
    }
 
 
    /**
     * Redis解锁的操作
     *
     * @param key
     * @param value
     */
    public void unlock(String key) {
        String currentValue = (String) this.redisTemplate.opsForValue().get(key);
        try {
            if (StringUtils.isNotEmpty(currentValue)) {
            	this.redisTemplate.opsForValue().getOperations().delete(key);
            }
        } catch (Exception e) {
        }
    }

}
