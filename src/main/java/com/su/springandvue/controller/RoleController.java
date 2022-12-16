
 package com.su.springandvue.controller;

 import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
 import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
 import com.su.springandvue.common.Result;
 import org.springframework.web.bind.annotation.*;
 import javax.annotation.Resource;
 import java.util.List;

 import com.su.springandvue.service.IRoleService;
 import com.su.springandvue.entity.Role;

 import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author susu
 * @since 2022-11-25
 */
@RestController
@RequestMapping("/role")
public class RoleController {

 @Resource
 private IRoleService roleService;

 //新增或者更新
 @PostMapping("/save")
 public Result save(@RequestBody Role role){
       return  Result.success(roleService.saveOrUpdate(role));
 }

 //根据主键删除
 @DeleteMapping("/{id}")
 public Result delete(@PathVariable Integer id){
  roleService.removeById(id);
  return Result.success();}

 //批量删除
 @PostMapping("/deleteBatch")
 public Result deleteBatch(@RequestBody List<Integer> ids){
  roleService.removeByIds(ids);
        return Result.success();
        }

 //查询所有用户
 @GetMapping
 public Result index(){ return Result.success(roleService.list()); }

 //根据id查询
 @GetMapping("/{id}")
 public Result findOne(@PathVariable Integer id){ return Result.success(roleService.getById(id)); }

 //分页查询
 @GetMapping("/page")
 public Result findPage(@RequestParam Integer pageNum,
                        @RequestParam Integer pageSize,
                        @RequestParam(defaultValue = "") String name){
        QueryWrapper<Role> query = new QueryWrapper<>();
        if(!"".equals(name)){
           query.like("name",name);
        }
        return  Result.success(roleService.page(new Page<>(pageNum,pageSize),query));
        }

    /**
     * 绑定角色和菜单关系
     * @param roleId
     * @param menuIds
     * @return
     */
    @PostMapping("/roleMenu/{roleId}")
    public Result roleMenu(@PathVariable Integer roleId,@RequestBody List<Integer> menuIds){
     roleService.setRoleMenu(roleId,menuIds);
     return Result.success(); }

    @GetMapping("/roleMenu/{roleId}")
    public Result getRoleMenu(@PathVariable Integer roleId){

        return Result.success(roleService.getRoleMenu(roleId)); }
}
