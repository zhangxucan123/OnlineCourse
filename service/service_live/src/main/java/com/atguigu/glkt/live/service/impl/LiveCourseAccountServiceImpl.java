package com.atguigu.glkt.live.service.impl;

import com.atguigu.ggkt.model.live.LiveCourseAccount;
import com.atguigu.glkt.live.mapper.LiveCourseAccountMapper;
import com.atguigu.glkt.live.service.LiveCourseAccountService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 直播课程账号表（受保护信息） 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-17
 */
@Service
public class LiveCourseAccountServiceImpl extends ServiceImpl<LiveCourseAccountMapper, LiveCourseAccount> implements LiveCourseAccountService {

    //获取直播课程的账号信息
    @Override
    public LiveCourseAccount getByLiveCourseId(Long id) {
        LambdaQueryWrapper<LiveCourseAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LiveCourseAccount::getLiveCourseId, id);
        LiveCourseAccount liveCourseAccount = baseMapper.selectOne(wrapper);
        return liveCourseAccount;
    }
}
