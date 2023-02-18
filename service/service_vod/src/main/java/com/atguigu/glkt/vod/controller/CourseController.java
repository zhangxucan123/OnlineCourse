package com.atguigu.glkt.vod.controller;


import com.atguigu.ggkt.model.vod.Course;
import com.atguigu.ggkt.vo.vod.CourseFormVo;
import com.atguigu.ggkt.vo.vod.CoursePublishVo;
import com.atguigu.ggkt.vo.vod.CourseQueryVo;
import com.atguigu.glkt.result.Result;
import com.atguigu.glkt.vod.service.CourseService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-02-11
 */
@RestController
@RequestMapping("/admin/vod/course")
//@CrossOrigin
public class CourseController {

    @Resource
    private CourseService courseService;

    @GetMapping("findAll")
    public Result findAll() {
        List<Course> list = courseService.findlist();
        return Result.ok(list);
    }

    @ApiOperation("点播课程列表")
    @PostMapping("{page}/{limit}")
    public Result courseList(@PathVariable Long page,
                             @PathVariable Long limit,
                             @RequestBody CourseQueryVo courseQueryVo) {
        Page<Course> pageParam = new Page<>(page, limit);
        Map<String, Object> map = courseService.findPageCourse(pageParam, courseQueryVo);
        return Result.ok(map);
    }

    //添加课程的基本信息
    @ApiOperation("添加课程基本信息")
    @PostMapping("save")
    public Result save(@RequestBody CourseFormVo courseFormVo) {
        Long courseId = courseService.saveCourseInfo(courseFormVo);
        return Result.ok(courseId);
    }


    @ApiOperation("根据id获取课程信息")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        CourseFormVo courseFormVo = courseService.getCourseInfoById(id);
        return Result.ok(courseFormVo);
    }


    @ApiOperation("修改课程信息")
    @PostMapping("update")
    public Result update(@RequestBody CourseFormVo courseFormVo) {
        courseService.updateCourseId(courseFormVo);
        return Result.ok(courseFormVo.getId());
    }

    //根据课程id查询课程发布信息
    @ApiOperation("id查询发布课程信息")
    @GetMapping("getCoursePublishVo/{id}")
    public Result getCoursePublishVo(@PathVariable long id) {
        CoursePublishVo coursePublishVo = courseService.getCoursePublishVo(id);
        return Result.ok(coursePublishVo);
    }

    //课程的最终发布
    @ApiOperation("课程的最终发布")
    @PutMapping("publishCourse/{id}")
    public Result publishCourse(@PathVariable long id) {
        courseService.publishCourse(id);
        return Result.ok(null);
    }

    //删除课程
    @ApiOperation("删除课程")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable long id) {
        courseService.removeCourseById(id);
        return Result.ok(null);
    }
}

