package com.atguigu.glkt.live.service;

import com.atguigu.ggkt.model.live.LiveCourseDescription;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程简介 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-17
 */
public interface LiveCourseDescriptionService extends IService<LiveCourseDescription> {

    LiveCourseDescription getByLiveCourseId(Long id);

}
