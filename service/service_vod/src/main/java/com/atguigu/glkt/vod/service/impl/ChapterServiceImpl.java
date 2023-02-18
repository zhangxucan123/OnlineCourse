package com.atguigu.glkt.vod.service.impl;

import com.atguigu.ggkt.model.vod.Chapter;
import com.atguigu.ggkt.model.vod.Video;
import com.atguigu.ggkt.vo.vod.ChapterVo;
import com.atguigu.ggkt.vo.vod.VideoVo;
import com.atguigu.glkt.vod.mapper.ChapterMapper;
import com.atguigu.glkt.vod.service.ChapterService;
import com.atguigu.glkt.vod.service.VideoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-11
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    @Resource
    private VideoService videoService;

    //1.大纲列表（章节 和  小结）
    @Override
    public List<ChapterVo> getTreeList(Long courseId) {
        List<ChapterVo> finalChapterList = new ArrayList<>();

        //根据courseId获取课程中的章节
        QueryWrapper<Chapter> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        List<Chapter> chapterList = baseMapper.selectList(wrapper);

        //根据courseId获取课程中的小结
        LambdaQueryWrapper<Video> wrapperVideo = new LambdaQueryWrapper<>();
        wrapperVideo.eq(Video::getCourseId, courseId);
        List<Video> videoList = videoService.list(wrapperVideo);
        //封装章节
        for (Chapter chapter : chapterList) {
            //chapter -- chapterVo
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(chapter, chapterVo);
            //封装章节里面的小节
            List<VideoVo> videoVoList = new ArrayList<>();
            for (Video video : videoList) {
                //判断小节是否是一个章节的小节
                if (chapter.getId().equals(video.getChapterId())) {
                    //video -- videoVO
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(video, videoVo);
                    videoVoList.add(videoVo);
                }
            }
            chapterVo.setChildren(videoVoList);
            finalChapterList.add(chapterVo);
        }
        return finalChapterList;
    }

    // 根据课程id删除章节
    @Override
    public void removeChapterByCourseId(long id) {
        QueryWrapper<Chapter> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", id);
        baseMapper.delete(wrapper);
    }
}
