package com.atguigu.glkt.vod.mapper;

import com.atguigu.ggkt.model.vod.Course;
import com.atguigu.ggkt.vo.vod.CoursePublishVo;
import com.atguigu.ggkt.vo.vod.CourseVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2023-02-11
 */
public interface CourseMapper extends BaseMapper<Course> {

    CoursePublishVo selectPublishVoById(long id);

    CourseVo selectCourseVoById(long courseId);
}
