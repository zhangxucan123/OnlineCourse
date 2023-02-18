package com.atguigu.glkt.vod.controller;


import com.atguigu.glkt.result.Result;
import com.atguigu.glkt.vod.service.VideoVisitorService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 视频来访者记录表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-02-13
 */
@RestController
@RequestMapping("/admin/vod/videoVisitor")
//@CrossOrigin
public class VideoVisitorController {

    @Resource
    private VideoVisitorService videoVisitorService;

    //课程统计
    @GetMapping("findCount/{courseId}/{startDate}/{endDate}")
    public Result findCount(@PathVariable long courseId,
                            @PathVariable String startDate,
                            @PathVariable String endDate) {
        Map<String, Object> map = videoVisitorService.findCount(courseId, startDate, endDate);
        return Result.ok(map);
    }

}

