package com.atguigu.glkt.client.course;

import com.atguigu.ggkt.model.vod.Course;
import com.atguigu.ggkt.model.vod.Teacher;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("service-vod")
public interface CourseFeignClient {

    @ApiOperation("根据关键字查询课程")
    @GetMapping("/api/vod/course/inner/findByKeyword/{keyword}")
    List<Course> findByKeyword(@PathVariable String keyword);


    @ApiOperation("根据ID查询课程")
    @GetMapping("/api/vod/course/inner/getById/{courseId}")
    Course getById(@ApiParam(value = "课程ID", required = true) @PathVariable Long courseId);

    @ApiOperation("根据id查询")
    @GetMapping("/admin/vod/teacher/inner/getTeacher/{id}")
    Teacher getTeacherInfo(@PathVariable long id);
}
