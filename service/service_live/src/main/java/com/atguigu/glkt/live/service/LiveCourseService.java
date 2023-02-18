package com.atguigu.glkt.live.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.ggkt.model.live.LiveCourse;
import com.atguigu.ggkt.vo.live.LiveCourseConfigVo;
import com.atguigu.ggkt.vo.live.LiveCourseFormVo;
import com.atguigu.ggkt.vo.live.LiveCourseVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 直播课程表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-17
 */
public interface LiveCourseService extends IService<LiveCourse> {

    IPage<LiveCourse> selectPage(Page<LiveCourse> pageParam);

    void saveLive(LiveCourseFormVo liveCourseVo);

    void removeLive(Long id);

    LiveCourseFormVo getLiveCourseFormVo(Long id);

    void updateLiveById(LiveCourseFormVo liveCourseVo);

    LiveCourseConfigVo getCourseConfig(long id);

    void updateConfig(LiveCourseConfigVo liveCourseConfigVo);

    List<LiveCourseVo> getLatelyList();

    JSONObject getPlayAuth(Long id, Long userId);

    Map<String, Object> getInfoById(Long courseId);
}
