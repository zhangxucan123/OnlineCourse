package com.atguigu.glkt.vod.service.impl;

import com.atguigu.ggkt.model.vod.VideoVisitor;
import com.atguigu.ggkt.vo.vod.VideoVisitorCountVo;
import com.atguigu.glkt.vod.mapper.VideoVisitorMapper;
import com.atguigu.glkt.vod.service.VideoVisitorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 视频来访者记录表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-13
 */
@Service
public class VideoVisitorServiceImpl extends ServiceImpl<VideoVisitorMapper, VideoVisitor> implements VideoVisitorService {

    //课程统计接口
    @Override
    public Map<String, Object> findCount(long courseId, String startDate, String endDate) {
        List<VideoVisitorCountVo> videoVisitorCountVoList = baseMapper.findCount(courseId, startDate, endDate);
        //创建map
        HashMap<String, Object> map = new HashMap<>();
        //创建两个list集合，一个代表所有日期，一个代表日期对应数量
        List<String> dates = videoVisitorCountVoList.stream().map(VideoVisitorCountVo::getJoinTime).collect(Collectors.toList());
        List<Integer> counts = videoVisitorCountVoList.stream().map(VideoVisitorCountVo::getUserCount).collect(Collectors.toList());
        //放入map
        map.put("xData", dates);
        map.put("yData", counts);
        return map;
    }
}
