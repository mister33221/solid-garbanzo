package com.kai.spring_boot_redis_practice.controller;

import com.kai.spring_boot_redis_practice.service.LuaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LuaController {

    public final LuaService luaService;

    public LuaController(LuaService luaService) {
        this.luaService = luaService;
    }

    @Operation(summary = "Execute Lua script", description = "Execute Lua script")
    @Tag(name = "Lua")
    @GetMapping("/executeLuaScript")
    public String executeLuaScript() {
        return luaService.executeLuaScript();
    }

}
