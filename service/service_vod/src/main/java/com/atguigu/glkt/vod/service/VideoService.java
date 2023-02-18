package com.atguigu.glkt.vod.service;

import com.atguigu.ggkt.model.vod.Video;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程视频 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-11
 */
public interface VideoService extends IService<Video> {

    void removeVideoByCourseId(long id);

    //删除小节 包括视频
    void removeVideoById(Long id);
}
