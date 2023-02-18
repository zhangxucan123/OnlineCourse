package com.atguigu.glkt.vod.service.impl;


import com.atguigu.ggkt.model.vod.Teacher;
import com.atguigu.glkt.vod.mapper.TeacherMapper;
import com.atguigu.glkt.vod.service.TeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-07
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
}
