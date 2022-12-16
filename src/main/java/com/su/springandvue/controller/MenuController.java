
 package com.su.springandvue.controller;

 import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
 import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
 import com.su.springandvue.common.Constants;
 import com.su.springandvue.common.Result;
 import com.su.springandvue.entity.Dict;
 import com.su.springandvue.entity.Menu;
 import com.su.springandvue.mapper.DictMapper;
 import com.su.springandvue.service.IMenuService;
 import org.springframework.web.bind.annotation.*;

 import javax.annotation.Resource;
 import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author susu
 * @since 2022-11-26
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

 @Resource
 private IMenuService menuService;

 @Resource
 private DictMapper dictMapper;

 //新增或者更新
 @PostMapping("/save")
 public Result save(@RequestBody Menu menu){
       return  Result.success(menuService.saveOrUpdate(menu));
 }

 //根据id查询
 @GetMapping("/ids")
 public Result findAllIds(){ return Result.success(menuService.list().stream().map(Menu::getId)); }

 //根据主键删除
 @DeleteMapping("/{id}")
 public Result delete(@PathVariable Integer id){
  menuService.removeById(id);
  return Result.success();}

 //批量删除
 @PostMapping("/deleteBatch")
 public Result deleteBatch(@RequestBody List<Integer> ids){
  menuService.removeByIds(ids);
  return Result.success();
        }

 //查询所有用户
 @GetMapping
 public Result index(@RequestParam(defaultValue = "")  String name){
  return Result.success(menuService.findMenus(name));
 }

 //根据id查询
 @GetMapping("/{id}")
 public Result findOne(@PathVariable Integer id){ return Result.success(menuService.getById(id)); }

 //分页查询
 @GetMapping("/page")
 public Result findPage(@RequestParam Integer pageNum,
                        @RequestParam Integer pageSize,
                        @RequestParam  String name){
        QueryWrapper<Menu> query = new QueryWrapper<>();
        query.like("name",name);
        return  Result.success(menuService.page(new Page<>(pageNum,pageSize),query));
        }

 //获取图标
 @GetMapping("/icons")
 public Result getIcons(){
   QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
   dictQueryWrapper.eq("type", Constants.DICT_TYPE_ICON);
   return Result.success(dictMapper.selectList(dictQueryWrapper));
 }

}
