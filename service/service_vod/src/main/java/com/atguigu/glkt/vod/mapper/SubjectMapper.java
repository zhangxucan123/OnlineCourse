package com.atguigu.glkt.vod.mapper;

import com.atguigu.ggkt.model.vod.Subject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 课程科目 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2023-02-09
 */
public interface SubjectMapper extends BaseMapper<Subject> {

    void selectList();
}
