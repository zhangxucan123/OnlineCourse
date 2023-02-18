package com.atguigu.glkt.live.service.impl;

import com.atguigu.ggkt.model.live.LiveCourseConfig;
import com.atguigu.glkt.live.mapper.LiveCourseConfigMapper;
import com.atguigu.glkt.live.service.LiveCourseConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 直播课程配置表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-17
 */
@Service
public class LiveCourseConfigServiceImpl extends ServiceImpl<LiveCourseConfigMapper, LiveCourseConfig> implements LiveCourseConfigService {

    @Override
    public LiveCourseConfig getCourseConfigCourseId(long id) {
        LambdaQueryWrapper<LiveCourseConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LiveCourseConfig::getLiveCourseId, id);
        LiveCourseConfig LiveCourseConfig = baseMapper.selectOne(wrapper);
        return LiveCourseConfig;
    }
}
