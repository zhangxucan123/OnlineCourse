package com.atguigu.glkt.vod.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.ggkt.model.vod.Subject;
import com.atguigu.ggkt.vo.vod.SubjectEeVo;
import com.atguigu.glkt.exception.GlktException;
import com.atguigu.glkt.vod.listener.SubjectListener;
import com.atguigu.glkt.vod.mapper.SubjectMapper;
import com.atguigu.glkt.vod.service.SubjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-09
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Resource
    private SubjectListener subjectListener;
    @Override
    public List<Subject> selectSubjectList(Long id) {
        QueryWrapper<Subject> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        List<Subject> subjectList = baseMapper.selectList(wrapper);
        for (Subject s : subjectList) {
            //获取s的id值
            Long subjectId = s.getId();
            boolean isChild = this.isChildren(subjectId);
            s.setHasChildren(isChild);
        }
        return subjectList;
    }

    //课程分类导出
    @Override
    public void exportData(HttpServletResponse response) {

        try {
            //设置下载信息
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("课程分类", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");


            //查询课程分类
            List<Subject> list = baseMapper.selectList(null);
            List<SubjectEeVo> subjectList = new ArrayList<>();
            for (Subject subject : list) {
                SubjectEeVo subjectEeVo = new SubjectEeVo();
//                subjectEeVo.setId(subject.getId());
//                subjectEeVo.setSort(subject.getSort());
//                subjectEeVo.setParentId(subject.getParentId());
//                subjectEeVo.setTitle(subject.getTitle());
                BeanUtils.copyProperties(subject,subjectEeVo);
                subjectList.add(subjectEeVo);
            }

            EasyExcel.write(response.getOutputStream(), SubjectEeVo.class)
                    .sheet()
                    .doWrite(subjectList);
        } catch (Exception e) {
            throw new GlktException(20001, "导出失败");
        }

    }

    //课程分类导入
    @Override
    public void importData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), SubjectEeVo.class, subjectListener);
        } catch (Exception e) {
            throw new GlktException(20001, "导入失败");
        }
    }

    private boolean isChildren(Long subjectId) {
        QueryWrapper<Subject> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", subjectId);
        Integer count = baseMapper.selectCount(wrapper);
        return count>0;
    }
}
