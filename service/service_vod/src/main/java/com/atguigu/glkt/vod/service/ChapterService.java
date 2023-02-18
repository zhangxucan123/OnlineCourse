package com.atguigu.glkt.vod.service;

import com.atguigu.ggkt.model.vod.Chapter;
import com.atguigu.ggkt.vo.vod.ChapterVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-11
 */
public interface ChapterService extends IService<Chapter> {

    //1.大纲列表（章节 和  小结）
    List<ChapterVo> getTreeList(Long courseId);

    void removeChapterByCourseId(long id);
}
