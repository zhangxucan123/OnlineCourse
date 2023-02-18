package com.atguigu.glkt.live.service;

import com.atguigu.ggkt.model.live.LiveCourseAccount;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 直播课程账号表（受保护信息） 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-17
 */
public interface LiveCourseAccountService extends IService<LiveCourseAccount> {

    LiveCourseAccount getByLiveCourseId(Long id);
}
