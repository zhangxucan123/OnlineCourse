package com.atguigu.glkt.vod.service.impl;

import com.atguigu.ggkt.model.vod.Course;
import com.atguigu.ggkt.model.vod.CourseDescription;
import com.atguigu.ggkt.model.vod.Subject;
import com.atguigu.ggkt.model.vod.Teacher;
import com.atguigu.ggkt.vo.vod.*;
import com.atguigu.glkt.vod.mapper.CourseMapper;
import com.atguigu.glkt.vod.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-11
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Resource
    private TeacherService teacherService;

    @Resource
    private SubjectService subjectService;

    @Resource
    private CourseDescriptionService courseDescriptionService;

    @Resource
    private ChapterService chapterService;

    @Resource
    private VideoService videoService;

    @Override
    public Map<String, Object> findPageCourse(Page<Course> pageParam,
                                              CourseQueryVo courseQueryVo) {
        //获取条件值
        Long subjectId = courseQueryVo.getSubjectId();

        Long subjectParentId = courseQueryVo.getSubjectParentId();

        Long teacherId = courseQueryVo.getTeacherId();

        String title = courseQueryVo.getTitle();


        QueryWrapper<Course> wrapper = new QueryWrapper<>();

        if (!StringUtils.isEmpty(subjectId)) {
            wrapper.eq("subject_id", subjectId);
        }
        if (!StringUtils.isEmpty(subjectParentId)) {
            wrapper.eq("subject_parent_id", subjectParentId);
        }
        if (!StringUtils.isEmpty(teacherId)) {
            wrapper.eq("teacher_id", teacherId);
        }
        if (!StringUtils.isEmpty(title)) {
            wrapper.eq("title", title);
        }
        Page<Course> pages = baseMapper.selectPage(pageParam, wrapper);
        long totalCount = pages.getTotal();
        long totalPage = pages.getPages();
        List<Course> courseList = pages.getRecords();

        courseList.forEach(this::getNameById);


        Map<String, Object> map = new HashMap<>();
        map.put("records", courseList);
        map.put("totalPage", totalPage);
        map.put("totalCount", totalCount);
        return map;
    }

    //添加课程信息
    @Override
    public Long saveCourseInfo(CourseFormVo courseFormVo) {
        //添加课程基本信息，操作course表
        Course course = new Course();
        BeanUtils.copyProperties(courseFormVo, course);
        baseMapper.insert(course);
        //添加课程描述信息，操作course_description
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setId(course.getId());
        courseDescription.setDescription(courseFormVo.getDescription());
        courseDescriptionService.save(courseDescription);
        return course.getId();
    }

    @Override
    public CourseFormVo getCourseInfoById(Long id) {
        //课程的基本信息
        Course course = baseMapper.selectById(id);
        if (course == null) {
            return null;
        }
        //课程的描述信息
        CourseDescription courseDescription = courseDescriptionService.getById(id);
        //封装
        CourseFormVo courseFormVo = new CourseFormVo();
        BeanUtils.copyProperties(course,courseFormVo);

        if (courseDescription != null) {
            courseFormVo.setDescription(courseDescription.getDescription());
        }

        return courseFormVo;
    }

    @Override
    public void updateCourseId(CourseFormVo courseFormVo) {
        //修改课程基本信息
        Course course = new Course();
        BeanUtils.copyProperties(courseFormVo,course);
        baseMapper.updateById(course);

        //修改课程的描述信息
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseFormVo.getDescription());
        courseDescription.setId(courseFormVo.getId());
        courseDescriptionService.updateById(courseDescription);
    }

    //根据课程id查询课程发布信息
    @Override
    public CoursePublishVo getCoursePublishVo(long id) {
        return baseMapper.selectPublishVoById(id);
    }

    //最终发布课程
    @Override
    public void publishCourse(long id) {
        Course course = baseMapper.selectById(id);
        course.setStatus(1);
        course.setPublishTime(new Date());
        baseMapper.updateById(course);
    }

    //根据课程id删除课程
    @Override
    public void removeCourseById(long id) {
        //根据课程id删除小节
        videoService.removeVideoByCourseId(id);
        //根据课程id删除章节
        chapterService.removeChapterByCourseId(id);
        //根据课程id删除课程描述
        courseDescriptionService.removeById(id);
        //根据课程id删除课程
        baseMapper.deleteById(id);
    }

    //根据课程分类查询课程列表（分页）
    @Override
    public Map<String,Object> findPage(Page<Course> pageParam,
                                    CourseQueryVo courseQueryVo) {
        //获取条件值
        String title = courseQueryVo.getTitle();//名称
        Long subjectId = courseQueryVo.getSubjectId();//二级分类
        Long subjectParentId = courseQueryVo.getSubjectParentId();//一级分类
        Long teacherId = courseQueryVo.getTeacherId();//讲师
        //判断条件值是否为空，封装
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(title)) {
            wrapper.like("title",title);
        }
        if(!StringUtils.isEmpty(subjectId)) {
            wrapper.eq("subject_id",subjectId);
        }
        if(!StringUtils.isEmpty(subjectParentId)) {
            wrapper.eq("subject_parent_id",subjectParentId);
        }
        if(!StringUtils.isEmpty(teacherId)) {
            wrapper.eq("teacher_id",teacherId);
        }
        //
        Page<Course> pages = baseMapper.selectPage(pageParam, wrapper);
        long totalCount = pages.getTotal();//总记录数
        long totalPage = pages.getPages();//总页数
        long currentPage = pages.getCurrent();//当前页
        long size = pages.getSize();//每页记录数

        //每页数据集合
        List<Course> records = pages.getRecords();
        records.forEach(this::getTeacherOrSubjectName);

        Map<String,Object> map = new HashMap<>();
        map.put("totalCount",totalCount);
        map.put("totalPage",totalPage);
        map.put("records",records);
        return map;
    }

    private void getTeacherOrSubjectName(Course course) {
        Long teacherId = course.getTeacherId();
        Long subjectParentId = course.getSubjectParentId();
        Long subjectId = course.getSubjectId();
        Teacher teacher = teacherService.getById(teacherId);
        if (teacher != null) {
            course.getParam().put("teacherName", teacher.getName());
        }
        //课程分类的名称
        Subject oneSubject = subjectService.getById(subjectParentId);
        if (oneSubject != null) {
            course.getParam().put("subjectParentTitle", oneSubject.getTitle());
        }
        Subject twoSubject = subjectService.getById(subjectId);
        if (twoSubject != null) {
            course.getParam().put("subjectTitle", twoSubject.getTitle());
        }
    }

    //根据课程id查询课程详情
    @Override
    public Map<String, Object> getInfoById(long courseId) {
        //浏览量加1
        Course course = baseMapper.selectById(courseId);
        course.setViewCount(course.getViewCount()+1);
        baseMapper.updateById(course);
        //课程详情数据
        CourseVo courseVo = baseMapper.selectCourseVoById(courseId);

        //课程章节小节数据
        List<ChapterVo> chapterVoList = chapterService.getTreeList(courseId);

        //课程描述信息
        CourseDescription courseDescription = courseDescriptionService.getById(courseId);

        //查询所属讲师信息
        Teacher teacher = teacherService.getById(course.getTeacherId());

        Map<String, Object> map = new HashMap<>();
        //TODO后续完善
        Boolean isBuy = false;

        map.put("courseVo", courseVo);
        map.put("chapterVoList", chapterVoList);
        map.put("description", null != courseDescription ?
                courseDescription.getDescription() : "");
        map.put("teacher", teacher);
        map.put("isBuy", isBuy);//是否购买
        return map;
    }

    //查询所有课程
    @Override
    public List<Course> findlist() {
        List<Course> courses = baseMapper.selectList(null);
        courses.forEach(this::getTeacherOrSubjectName);
        return null;
    }

    //根据id获取对应的名称进行封装
    private Course getNameById(Course course) {
        Teacher teacher = teacherService.getById(course.getTeacherId());
        if (teacher != null) {
            String teacherName = teacher.getName();
            course.getParam().put("teacherName", teacherName);
        }
        Subject subject = subjectService.getById(course.getSubjectId());
        if (subject != null) {
            String subjectTitle = subject.getTitle();
            course.getParam().put("subjectTitle", subjectTitle);
        }
        Subject subject1 = subjectService.getById(course.getSubjectParentId());
        if (subject1 != null) {
            String subjectParentTitle = subject1.getTitle();
            course.getParam().put("subjectParentTitle", subjectParentTitle);
        }
        return course;
    }
}
