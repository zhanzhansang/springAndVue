package com.su.springandvue.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.springandvue.common.Constants;
import com.su.springandvue.controller.dto.UserDTO;
import com.su.springandvue.entity.Menu;
import com.su.springandvue.entity.User;
import com.su.springandvue.exception.ServiceException;
import com.su.springandvue.mapper.RoleMapper;
import com.su.springandvue.mapper.RoleMenuMapper;
import com.su.springandvue.mapper.UserMapper;
import com.su.springandvue.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService  extends ServiceImpl<UserMapper,User>{

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

    public UserDTO login(UserDTO userDTO) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username",userDTO.getUsername());
        query.eq("password",userDTO.getPassword());
        User one;
        try {
            one = getOne(query);
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_500,"系统错误");
        }

        if ( one != null){
            BeanUtil.copyProperties(one,userDTO,true);
            //设置token
            String token = TokenUtils.genToken(one.getId().toString(),one.getPassword());
            userDTO.setToken(token);

            //查询当前用户的角色
            String role = one.getRole();
            //设置用户菜单列表
            List<Menu> roleMenus = getRoleMenus(role);
            userDTO.setMenus(roleMenus);

            return userDTO;
        }else {
             throw new ServiceException(Constants.CODE_600,"用户名或密码错误");
        }
    }


    private List<Menu> getRoleMenus(String roleFlag){
        Integer roleId = roleMapper.selectByFlag(roleFlag);

        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);

        List<Menu> menus = menuService.findMenus("");
        List<Menu> roleMenus = new ArrayList<>();

        for (Menu menu : menus) {
            if (menuIds.contains(menu.getId())){
                roleMenus.add(menu);
            }
            //获取子菜单
            List<Menu> children = menu.getChildren();
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }

        return roleMenus;
    }


    public Page<User> findPage(Page<User> page, String username) {
        return userMapper.findPage(page,username);
    }


    public User register(UserDTO userDTO) {
        //校验
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username",userDTO.getUsername());
        query.eq("password",userDTO.getPassword());
        User one;
        try{
            one = getOne(query);
        }catch (Exception e){
            throw new ServiceException(Constants.CODE_500,"系统错误");
        }

        if (one == null){
            one = new User();
            BeanUtil.copyProperties(userDTO,one,true);
            save(one);
        }else{
            throw new ServiceException(Constants.CODE_600,"用户已存在");
        }
       return one;
    }

    public void updatePassword(Integer id, String password) {
        int i = userMapper.updatePassword(id, password);
        if (i < 1){
            throw new ServiceException(Constants.CODE_600,"修改失败");
        }
    }
}
