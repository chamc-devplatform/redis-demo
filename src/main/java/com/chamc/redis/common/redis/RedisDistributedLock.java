package com.chamc.redis.common.redis;

import java.util.Collections;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

@Slf4j
@Component
public class RedisDistributedLock {

	private static final String LOCK_SUCCESS = "OK";
	private static final String SET_IF_NOT_EXIST = "NX";
	private static final String SET_WITH_EXPIRE_TIME = "PX";
	private static final Long RELEASE_SUCCESS = 1L;
	private RedisTemplate<Object, Object> redisTemplate;
	
	//constructor
	public RedisDistributedLock(RedisTemplate<Object, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	// get the lock
	public boolean tryGetDistributedLock(String resource, String owner, int expireTime) {

		String result = redisTemplate.execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				JedisCommands commands = (JedisCommands) connection.getNativeConnection();
				return commands.set(resource, owner, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
			}
		});
		if (LOCK_SUCCESS.equals(result)) {
			log.info("add lock success ! uuid = " + owner);
			return true;
		}
		log.info("add lock failed ! uuid = " + owner);
		return false;
	}

	// release the lock;
	public boolean releaseLock(String resource, String owner) {
		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		Long result = redisTemplate.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				Object nativeConnection = connection.getNativeConnection();

				// cluster mode
				if (nativeConnection instanceof JedisCluster) {
					return (Long) ((JedisCluster) nativeConnection).eval(script, Collections.singletonList(resource),
							Collections.singletonList(owner));
				}else if(nativeConnection instanceof Jedis) {
					//single mode
					return (Long)((Jedis) nativeConnection).eval(script, Collections.singletonList(resource),
							Collections.singletonList(owner));
				}
				return 0L;
			}
		});
		if(RELEASE_SUCCESS.equals(result)) {
			log.info("release lock success ! uuid = " + owner);
			return true;
		}
		log.info("release lock failed ! uuid = " + owner);
		return false;
	}
}
