package com.chamc.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chamc.redis.service.RedisService;

@Controller
@RequestMapping("/redis")
public class RedisController {
	
	private @Autowired RedisService redisService;
	
	@GetMapping("/test")
	public ResponseEntity<String> printHello(String appName, String md5) throws InterruptedException {
		redisService.print(appName, md5);
		return null;
	}

}
