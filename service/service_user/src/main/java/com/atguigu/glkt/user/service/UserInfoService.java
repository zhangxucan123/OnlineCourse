package com.atguigu.glkt.user.service;

import com.atguigu.ggkt.model.user.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-15
 */
public interface UserInfoService extends IService<UserInfo> {

    UserInfo getUserInfoOpenid(String openId);
}
