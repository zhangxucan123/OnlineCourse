package com.atguigu.glkt.user.controller;


import com.atguigu.ggkt.model.user.UserInfo;
import com.atguigu.glkt.result.Result;
import com.atguigu.glkt.user.service.UserInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-02-15
 */
@RestController
@RequestMapping("/admin/user/userInfo")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @ApiOperation("获取")
    @GetMapping("inner/getById/{id}")
    public UserInfo getById(@PathVariable long id) {
        UserInfo userInfo = userInfoService.getById(id);
        return userInfo;
    }
}

