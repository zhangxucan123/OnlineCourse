package com.atguigu.glkt.vod.service;

import com.atguigu.ggkt.model.vod.VideoVisitor;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 视频来访者记录表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-13
 */
public interface VideoVisitorService extends IService<VideoVisitor> {

    Map<String, Object> findCount(long courseId, String startDate, String endDate);
}
