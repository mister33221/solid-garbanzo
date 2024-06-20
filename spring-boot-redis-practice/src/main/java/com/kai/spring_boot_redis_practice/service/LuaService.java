package com.kai.spring_boot_redis_practice.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class LuaService {

    private final StringRedisTemplate stringRedisTemplate;

    private final DefaultRedisScript<String> redisScript;

    public LuaService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        redisScript = new DefaultRedisScript<>();
    }

    public String executeLuaScript() {
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("test.lua")));
        redisScript.setResultType(String.class);
        return stringRedisTemplate.execute(redisScript, Collections.emptyList());
    }


}
