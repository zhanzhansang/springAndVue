package com.su.springandvue.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.su.springandvue.entity.Course;
import com.su.springandvue.mapper.CourseMapper;
import com.su.springandvue.service.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author susu
 * @since 2022-11-30
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Resource
    private CourseMapper courseMapper;

    @Override
    public Page<Course> findPage(Page<Course> page, String name) {
        return courseMapper.findPage(page,name);
    }

    @Transactional
    @Override
    public void setStudentCourse(Integer courseId, Integer studentId) {
      courseMapper.deleteStudentCourse(courseId,studentId);
      courseMapper.setStudentCourse(courseId,studentId);
    }
}
