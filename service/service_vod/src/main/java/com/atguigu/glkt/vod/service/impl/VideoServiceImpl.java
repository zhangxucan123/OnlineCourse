package com.atguigu.glkt.vod.service.impl;

import com.atguigu.ggkt.model.vod.Video;
import com.atguigu.glkt.vod.mapper.VideoMapper;
import com.atguigu.glkt.vod.service.VideoService;
import com.atguigu.glkt.vod.service.VodService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-11
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Resource
    private VodService vodService;

    //根据课程id删除小节,并且删除所有小节中的视频
    @Override
    public void removeVideoByCourseId(long id) {

        QueryWrapper<Video> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", id);
        List<Video> videos = baseMapper.selectList(wrapper);
        for (Video video : videos) {
            String videoSourceId = video.getVideoSourceId();
            //判读视频id是否为空
            if (!StringUtils.isEmpty(videoSourceId)) {
                //根据视频id删除腾讯云中的视频
                vodService.removeVideo(videoSourceId);
            }
        }
        baseMapper.delete(wrapper);
    }

    //删除小节 包括视频
    @Override
    public void removeVideoById(Long id) {
        //ID查询小节
        Video video = baseMapper.selectById(id);
        //获取里面视频的id
        String videoSourceId = video.getVideoSourceId();
        //判读视频id是否为空
        if (!StringUtils.isEmpty(videoSourceId)) {
            //根据视频id删除腾讯云中的视频
            vodService.removeVideo(videoSourceId);
        }
        //根据id删小节
        baseMapper.deleteById(id);
    }
}
