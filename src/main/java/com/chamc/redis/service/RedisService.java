package com.chamc.redis.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chamc.redis.common.redis.RedisDistributedLock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RedisService {

	private @Autowired RedisDistributedLock redisDistributedLock;
	private static final int EXPIRE_TIME = 20000;
	
	public void print(String appName, String md5) throws InterruptedException {
		String resource = appName + "-" + md5;
		String owner = UUID.randomUUID().toString().replaceAll("-", "");
		if(redisDistributedLock.tryGetDistributedLock(resource, owner, EXPIRE_TIME)) {
			Thread.sleep(1000);//do something
			log.info("current thread is running...");
			redisDistributedLock.releaseLock(resource, owner);
		}else {
			//add lock failed
		}
	}

}
