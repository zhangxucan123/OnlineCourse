package com.atguigu.glkt.live.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.ggkt.model.live.*;
import com.atguigu.ggkt.model.user.UserInfo;
import com.atguigu.ggkt.model.vod.Teacher;
import com.atguigu.ggkt.vo.live.LiveCourseConfigVo;
import com.atguigu.ggkt.vo.live.LiveCourseFormVo;
import com.atguigu.ggkt.vo.live.LiveCourseGoodsView;
import com.atguigu.ggkt.vo.live.LiveCourseVo;
import com.atguigu.glkt.client.course.CourseFeignClient;
import com.atguigu.glkt.client.user.UserInfoFeignClient;
import com.atguigu.glkt.exception.GlktException;
import com.atguigu.glkt.live.mapper.LiveCourseMapper;
import com.atguigu.glkt.live.mtcloud.CommonResult;
import com.atguigu.glkt.live.mtcloud.MTCloud;
import com.atguigu.glkt.live.service.*;
import com.atguigu.glkt.utils.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <p>
 * 直播课程表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-17
 */
@Service
public class LiveCourseServiceImpl extends ServiceImpl<LiveCourseMapper, LiveCourse> implements LiveCourseService {

    @Autowired
    private CourseFeignClient courseFeignClient;

    @Autowired
    private MTCloud mtCloudClient;

    @Autowired
    private LiveCourseDescriptionService liveCourseDescriptionService;

    @Autowired
    private LiveCourseAccountService liveCourseAccountService;

    @Autowired
    private LiveCourseConfigService liveCourseConfigService;

    @Autowired
    private LiveCourseGoodsService liveCourseGoodsService;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    //直播课程分页查询
    @Override
    public IPage<LiveCourse> selectPage(Page<LiveCourse> pageParam) {
        IPage<LiveCourse> page = baseMapper.selectPage(pageParam, null);
        List<LiveCourse> liveCourseList = page.getRecords();

        for(LiveCourse liveCourse : liveCourseList) {
            Teacher teacher = courseFeignClient.getTeacherInfo(liveCourse.getTeacherId());
            if (teacher != null) {
                liveCourse.getParam().put("teacherName", teacher.getName());
                liveCourse.getParam().put("teacherLevel", teacher.getLevel());
            }
        }
        return page;
    }

    //直播课程添加
    @Override
    public void saveLive(LiveCourseFormVo liveCourseVo) {
        LiveCourse liveCourse = new LiveCourse();
        BeanUtils.copyProperties(liveCourseVo, liveCourse);

        Teacher teacher = courseFeignClient.getTeacherInfo(liveCourseVo.getTeacherId());
        HashMap<Object, Object> options = new HashMap<>();
        options.put("scenes", 2);//直播类型。1: 教育直播，2: 生活直播。默认 1，说明：根据平台开通的直播类型填写
        options.put("password", liveCourseVo.getPassword());
        //调用方法添加课程
        try {
            String res = mtCloudClient.courseAdd(liveCourse.getCourseName(),
                    teacher.getId().toString(),
                    new DateTime(liveCourse.getStartTime()).toString("yyyy-MM-dd HH:mm:ss"),
                    new DateTime(liveCourse.getEndTime()).toString("yyyy-MM-dd HH:mm:ss"), teacher.getName(), teacher.getIntro(), options);
            System.out.println("return:: "+res);
            CommonResult<JSONObject> commonResult = JSON.parseObject(res, CommonResult.class);
            if (Integer.parseInt(commonResult.getCode()) == MTCloud.CODE_SUCCESS) {
                JSONObject object = commonResult.getData();
                liveCourse.setCourseId(object.getLong("course_id"));
                baseMapper.insert(liveCourse);

                //保存课程详情信息
                LiveCourseDescription liveCourseDescription = new LiveCourseDescription();
                liveCourseDescription.setDescription(liveCourseVo.getDescription());
                liveCourseDescription.setLiveCourseId(liveCourse.getId());
                liveCourseDescriptionService.save(liveCourseDescription);

                //保存课程账号信息
                LiveCourseAccount liveCourseAccount = new LiveCourseAccount();
                liveCourseAccount.setLiveCourseId(liveCourse.getId());
                liveCourseAccount.setZhuboAccount(object.getString("bid"));
                liveCourseAccount.setZhuboPassword(liveCourseVo.getPassword());
                liveCourseAccount.setAdminKey(object.getString("admin_key"));
                liveCourseAccount.setUserKey(object.getString("user_key"));
                liveCourseAccount.setZhuboKey(object.getString("zhubo_key"));
                liveCourseAccountService.save(liveCourseAccount);
            } else {
                throw new GlktException(20001, "直播创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除直播课程
    @Override
    public void removeLive(Long id) {
        //根据id查询直播课程信息
        LiveCourse liveCourse = baseMapper.selectById(id);
        if (liveCourse != null) {
            Long courseId = liveCourse.getCourseId();
            try {
                mtCloudClient.courseDelete(courseId.toString());
                baseMapper.deleteById(courseId);
            } catch (Exception e) {
                e.printStackTrace();
                throw new GlktException(20001, "删除直播课程失败");
            }
        }
    }

    @Override
    public LiveCourseFormVo getLiveCourseFormVo(Long id) {

        //获取直播课程基本信息
        LiveCourse liveCourse = baseMapper.selectById(id);
        //获取直播课程描述信息
        LiveCourseDescription liveCourseDescription = liveCourseDescriptionService.getByLiveCourseId(id);
        //封装
        LiveCourseFormVo liveCourseFormVo = new LiveCourseFormVo();
        BeanUtils.copyProperties(liveCourse, liveCourseFormVo);
        liveCourseFormVo.setDescription(liveCourseDescription.getDescription());
        return liveCourseFormVo;
    }

    @Override
    public void updateLiveById(LiveCourseFormVo liveCourseVo) {
        //获取直播课程的基本信息
        LiveCourse liveCourse = baseMapper.selectById(liveCourseVo.getId());
        BeanUtils.copyProperties(liveCourseVo, liveCourse);
        //讲师
        Teacher teacher = courseFeignClient.getTeacherInfo(liveCourseVo.getTeacherId());
        HashMap<Object, Object> options = new HashMap<>();
        try {
            String res = mtCloudClient.courseUpdate(liveCourse.getCourseId().toString(),
                    teacher.getId().toString(),
                    liveCourse.getCourseName(),
                    new DateTime(liveCourse.getStartTime()).toString("yyyy-MM-dd HH:mm:ss"),
                    new DateTime(liveCourse.getEndTime()).toString("yyyy-MM-dd HH:mm:ss"),
                    teacher.getName(),
                    teacher.getIntro(),
                    options);
            //返回结果转换，判断是否成功
            CommonResult<JSONObject> commonResult = JSON.parseObject(res, CommonResult.class);
            if(Integer.parseInt(commonResult.getCode()) == MTCloud.CODE_SUCCESS) {
                JSONObject object = commonResult.getData();
                //更新直播课程基本信息
                liveCourse.setCourseId(object.getLong("course_id"));
                baseMapper.updateById(liveCourse);
                //直播课程描述信息更新
                LiveCourseDescription liveCourseDescription =
                        liveCourseDescriptionService.getByLiveCourseId(liveCourse.getId());
                liveCourseDescription.setDescription(liveCourseVo.getDescription());
                liveCourseDescriptionService.updateById(liveCourseDescription);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlktException(20001, "课程更新失败");
        }
    }

    @Override
    public LiveCourseConfigVo getCourseConfig(long id) {
        LiveCourseConfig liveCourseConfig = liveCourseConfigService.getCourseConfigCourseId(id);
        LiveCourseConfigVo liveCourseConfigVo = new LiveCourseConfigVo();
        if (liveCourseConfig != null) {
            List<LiveCourseGoods> list = liveCourseGoodsService.getGoodsListCourseId(id);
            BeanUtils.copyProperties(liveCourseConfig,liveCourseConfigVo);
            liveCourseConfigVo.setLiveCourseGoodsList(list);

        }
        return liveCourseConfigVo;
    }

    @Override
    public void updateConfig(LiveCourseConfigVo liveCourseConfigVo) {
        LiveCourseConfig liveCourseConfig = new LiveCourseConfig();
        BeanUtils.copyProperties(liveCourseConfigVo, liveCourseConfig);
        if (null == liveCourseConfigVo.getId()) {
            liveCourseConfigService.save(liveCourseConfig);
        } else {
            liveCourseConfigService.updateById(liveCourseConfig);
        }
        liveCourseGoodsService.remove(new LambdaQueryWrapper<LiveCourseGoods>().eq(LiveCourseGoods::getLiveCourseId,
                liveCourseConfigVo.getLiveCourseId()));
        if(!CollectionUtils.isEmpty(liveCourseConfigVo.getLiveCourseGoodsList())) {
            liveCourseGoodsService.saveBatch(liveCourseConfigVo.getLiveCourseGoodsList());
        }

        this.updateLifeConfig(liveCourseConfigVo);
    }

    @Override
    public List<LiveCourseVo> getLatelyList() {
        List<LiveCourseVo> liveCourseVoList = baseMapper.getLatelyList();
        for (LiveCourseVo liveCourseVo : liveCourseVoList) {
            liveCourseVo.setStartTimeString(new DateTime(liveCourseVo.getStartTime()).toString("yyyy年MM月dd HH:mm"));
            liveCourseVo.setEndTimeString(new DateTime(liveCourseVo.getEndTime()).toString("HH:mm"));

            Long teacherId = liveCourseVo.getTeacherId();
            Teacher teacher = courseFeignClient.getTeacherInfo(teacherId);
            liveCourseVo.setTeacher(teacher);

            liveCourseVo.setLiveStatus(this.getLiveStatus(liveCourseVo));
        }

        return liveCourseVoList;
    }


    //获取用户access_token
    @Override
    public JSONObject getPlayAuth(Long id, Long userId) {
        LiveCourse liveCourse = baseMapper.selectById(id);
        UserInfo userInfo = userInfoFeignClient.getById(userId);
        HashMap<Object,Object> options = new HashMap<Object, Object>();
        try {
            String res = mtCloudClient.courseAccess(liveCourse.getCourseId().toString(),
                    userId.toString(), userInfo.getNickName(), MTCloud.ROLE_USER, 3600, options);
            CommonResult<JSONObject> commonResult = JSON.parseObject(res, CommonResult.class);
            if(Integer.parseInt(commonResult.getCode()) == MTCloud.CODE_SUCCESS) {
                JSONObject object = commonResult.getData();
                System.out.println("access::"+object.getString("access_token"));
                return object;
            } else {
                throw new GlktException(20001,"获取失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, Object> getInfoById(Long id) {
        LiveCourse liveCourse = this.getById(id);
        liveCourse.getParam().put("startTimeString", new DateTime(liveCourse.getStartTime()).toString("yyyy年MM月dd HH:mm"));
        liveCourse.getParam().put("endTimeString", new DateTime(liveCourse.getEndTime()).toString("yyyy年MM月dd HH:mm"));
        Teacher teacher = courseFeignClient.getTeacherInfo(liveCourse.getTeacherId());
        LiveCourseDescription liveCourseDescription = liveCourseDescriptionService.getByLiveCourseId(id);

        Map<String, Object> map = new HashMap<>();
        map.put("liveCourse", liveCourse);
        map.put("liveStatus", this.getLiveStatus(liveCourse));
        map.put("teacher", teacher);
        if(null != liveCourseDescription) {
            map.put("description", liveCourseDescription.getDescription());
        } else {
            map.put("description", "");
        }
        return map;
    }

    /**
     * 直播状态 0：未开始 1：直播中 2：直播结束
     * @param liveCourse
     * @return
     */
    private int getLiveStatus(LiveCourse liveCourse) {
        // 直播状态 0：未开始 1：直播中 2：直播结束
        int liveStatus = 0;
        Date curTime = new Date();
        if(DateUtil.dateCompare(curTime, liveCourse.getStartTime())) {
            liveStatus = 0;
        } else if(DateUtil.dateCompare(curTime, liveCourse.getEndTime())) {
            liveStatus = 1;
        } else {
            liveStatus = 2;
        }
        return liveStatus;
    }
    private void updateLifeConfig(LiveCourseConfigVo liveCourseConfigVo) {
        LiveCourse liveCourse =
                baseMapper.selectById(liveCourseConfigVo.getLiveCourseId());
        //参数设置
        HashMap<Object,Object> options = new HashMap<Object, Object>();
        //界面模式
        options.put("pageViewMode", liveCourseConfigVo.getPageViewMode());
        //观看人数开关
        JSONObject number = new JSONObject();
        number.put("enable", liveCourseConfigVo.getNumberEnable());
        options.put("number", number.toJSONString());
        //观看人数开关
        JSONObject store = new JSONObject();
        number.put("enable", liveCourseConfigVo.getStoreEnable());
        number.put("type", liveCourseConfigVo.getStoreType());
        options.put("store", number.toJSONString());
        //商城列表
        List<LiveCourseGoods> liveCourseGoodsList = liveCourseConfigVo.getLiveCourseGoodsList();
        if(!CollectionUtils.isEmpty(liveCourseGoodsList)) {
            List<LiveCourseGoodsView> liveCourseGoodsViewList = new ArrayList<>();
            for(LiveCourseGoods liveCourseGoods : liveCourseGoodsList) {
                LiveCourseGoodsView liveCourseGoodsView = new LiveCourseGoodsView();
                BeanUtils.copyProperties(liveCourseGoods, liveCourseGoodsView);
                liveCourseGoodsViewList.add(liveCourseGoodsView);
            }
            JSONObject goodsListEdit = new JSONObject();
            goodsListEdit.put("status", "0");
            options.put("goodsListEdit ", goodsListEdit.toJSONString());
            options.put("goodsList", JSON.toJSONString(liveCourseGoodsViewList));
        }
        try {
            String res = mtCloudClient.courseUpdateConfig(liveCourse.getCourseId().toString(), options);
            CommonResult<JSONObject> commonResult = JSON.parseObject(res, CommonResult.class);
            if(Integer.parseInt(commonResult.getCode()) != MTCloud.CODE_SUCCESS) {
                throw new GlktException(20001,"修改配置信息失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
