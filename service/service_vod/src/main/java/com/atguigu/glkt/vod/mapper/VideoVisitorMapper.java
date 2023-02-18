package com.atguigu.glkt.vod.mapper;

import com.atguigu.ggkt.model.vod.VideoVisitor;
import com.atguigu.ggkt.vo.vod.VideoVisitorCountVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 视频来访者记录表 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2023-02-13
 */
public interface VideoVisitorMapper extends BaseMapper<VideoVisitor> {

    List<VideoVisitorCountVo> findCount(@Param("courseId") long courseId,
                                        @Param("startDate")String startDate,
                                        @Param("endDate")String endDate);
}
