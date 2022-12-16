
 package com.su.springandvue.controller;

 import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
 import com.baomidou.mybatisplus.core.metadata.IPage;
 import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
 import com.su.springandvue.common.Result;
 import com.su.springandvue.entity.SysFile;
 import com.su.springandvue.entity.User;
 import com.su.springandvue.service.UserService;
 import org.springframework.web.bind.annotation.*;
 import javax.annotation.Resource;
 import java.util.List;

 import com.su.springandvue.service.ICourseService;
 import com.su.springandvue.entity.Course;

 import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author susu
 * @since 2022-11-30
 */
@RestController
@RequestMapping("/course")
public class CourseController {

 @Resource
 private ICourseService courseService;

 @Resource
 private UserService userService;

 //新增或者更新
 @PostMapping("/save")
 public Result save(@RequestBody Course course){
       return  Result.success(courseService.saveOrUpdate(course));
 }

 //学生选课
 @PostMapping("/studentCourse/{courseId}/{studentId}")
 public Result studentCourse(@PathVariable Integer courseId,@PathVariable Integer studentId){
  courseService.setStudentCourse(courseId,studentId);
  return  Result.success();
 }

 //更新
 @PostMapping("/update")
 public Result update(@RequestBody Course course){
  return  Result.success(courseService.updateById(course));
 }

 //根据主键删除
 @DeleteMapping("/{id}")
 public Result delete(@PathVariable Integer id){return Result.success(courseService.removeById(id));}

 //批量删除
 @PostMapping("/deleteBatch")
 public Result deleteBatch(@RequestBody List<Integer> ids){
        return Result.success(courseService.removeByIds(ids));
        }

 //查询所有用户
 @GetMapping
 public Result index(){ return Result.success(courseService.list()); }

 //根据id查询
 @GetMapping("/{id}")
 public Result findOne(@PathVariable Integer id){ return Result.success(courseService.getById(id)); }

 //分页查询
 @GetMapping("/page")
 public Result findPage(@RequestParam Integer pageNum,
                        @RequestParam Integer pageSize,
                        @RequestParam(defaultValue = "") String name){
//     QueryWrapper<Course> query = new QueryWrapper<>();
//     if(!"".equals(name)){
//         query.like("name",name);
//     }
//     Page<Course> page = courseService.page(new Page<>(pageNum, pageSize), query);
//     List<Course> records = page.getRecords();
//     for (Course record : records) {
//         User teacher = userService.getById(record.getTeacherId());
//         if (teacher != null){
//             record.setTeacher(teacher.getNickname());
//         }
//     }
    Page<Course> page = courseService.findPage(new Page<>(pageNum, pageSize), name);

     return  Result.success(page);
     }

}
