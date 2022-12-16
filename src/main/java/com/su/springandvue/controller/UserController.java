
 package com.su.springandvue.controller;

 import cn.hutool.core.util.StrUtil;
 import cn.hutool.poi.excel.ExcelReader;
 import cn.hutool.poi.excel.ExcelUtil;
 import cn.hutool.poi.excel.ExcelWriter;
 import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
 import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
 import com.su.springandvue.common.Constants;
 import com.su.springandvue.common.Result;
 import com.su.springandvue.controller.dto.UserDTO;
 import com.su.springandvue.entity.User;
 import com.su.springandvue.service.UserService;
 import org.springframework.web.bind.annotation.*;
 import org.springframework.web.multipart.MultipartFile;

 import javax.annotation.Resource;
 import javax.servlet.ServletOutputStream;
 import javax.servlet.http.HttpServletResponse;
 import java.io.InputStream;
 import java.net.URLEncoder;
 import java.util.List;

 /**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author susu
 * @since 2022-11-21
 */
@RestController
@RequestMapping("/user")
public class UserController {

 @Resource
 private UserService userService;

     @PostMapping("/login")
     public Result login(@RequestBody UserDTO user){
         String username = user.getUsername();
         String password = user.getPassword();
         if(StrUtil.isBlank(username) || StrUtil.isBlank(password)){
             return Result.error(Constants.CODE_400,"参数错误");
         }
         return Result.success(userService.login(user));
     }

 //新增或者更新
 @PostMapping("/save")
 public Result save(@RequestBody User user){
     String username = user.getUsername();
     if (StrUtil.isBlank(username)){
         return Result.error(Constants.CODE_400,"参数错误");
     }
     if (StrUtil.isBlank(user.getNickname() )){
         user.setNickname(username);
     }
     return  Result.success(userService.saveOrUpdate(user));
 }

     //修改密码
     @PostMapping("/password/{id}/{password}")
     public Result modifyPassword(@PathVariable Integer id,@PathVariable String password){
         userService.updatePassword(id,password);
         return Result.success();
     }

     @PostMapping("/register")
     public Result register(@RequestBody UserDTO userDTO){
         String username = userDTO.getUsername();
         String password = userDTO.getPassword();
         System.out.println(username);
         if(StrUtil.isBlank(username) || StrUtil.isBlank(password)){
             return Result.error(Constants.CODE_400,"参数错误");
         }
         return  Result.success(userService.register(userDTO));
     }

 //根据主键删除
 @DeleteMapping("/{id}")
 public Result delete(@PathVariable Integer id){return Result.success(userService.removeById(id));}

 //批量删除
 @PostMapping("/deleteBatch")
 public Result deleteBatch(@RequestBody List<Integer> ids){
        return Result.success(userService.removeByIds(ids));
        }

 //查询所有用户
 @GetMapping
 public Result index(){ return Result.success(userService.list()); }


  @GetMapping("/role/{role}")
  public Result findUserByRole(@PathVariable("role")String role ){
         QueryWrapper<User> queryWrapper = new QueryWrapper<>();
         queryWrapper.eq("role",role);
         return Result.success(userService.list(queryWrapper)); }

 //根据id查询
 @GetMapping("/{id}")
 public Result findOne(@PathVariable Integer id){ return Result.success(userService.getById(id)); }

 //根据用户名获取属性
 @GetMapping("/username/{username}")
 public Result findByUsername(@PathVariable String username){
     QueryWrapper<User> queryWrapper = new QueryWrapper<>();
     queryWrapper.eq("username",username);
     return Result.success(userService.getOne(queryWrapper)); }

 //分页查询
 @GetMapping("/page")
 public Result findPage(@RequestParam Integer pageNum,
                             @RequestParam Integer pageSize,
                             @RequestParam(defaultValue = "") String username){
//        QueryWrapper<User> query = new QueryWrapper<>();
//        if(!"".equals(username)){
//            query.like("username",username);
//        }


         return  Result.success(userService.findPage(new Page<>(pageNum,pageSize),username));
        }


    /**
     * 导出接口
     */
    @GetMapping("/export")
    public void export(HttpServletResponse reponse) throws Exception{
       List<User> list = userService.list();

       ExcelWriter excelWriter = ExcelUtil.getWriter(true);

//       excelWriter.addHeaderAlias("username","用户名");
//       excelWriter.addHeaderAlias("password","密码");
//       excelWriter.addHeaderAlias("nickname","昵称");
//       excelWriter.addHeaderAlias("email","邮箱");
//       excelWriter.addHeaderAlias("phone","电话");
//       excelWriter.addHeaderAlias("address","地址");
//       excelWriter.addHeaderAlias("createTime","创建时间");
//       excelWriter.addHeaderAlias("avatarUrl","头像");

        excelWriter.write(list,true);

        //设置浏览器响应格式
        reponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息","UTF-8");
        reponse.setHeader("Content-Disposition","attachment="+fileName+".xlsx");

        ServletOutputStream outputStream = reponse.getOutputStream();
        excelWriter.flush(outputStream,true);
        outputStream.close();
        excelWriter.close();
    }

    /**
     * 导入数据
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception{
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<User> list = reader.readAll(User.class);

        return  Result.success(userService.saveBatch(list));
    }
}
