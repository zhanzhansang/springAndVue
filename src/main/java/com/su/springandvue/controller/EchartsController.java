package com.su.springandvue.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Quarter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.su.springandvue.common.Result;
import com.su.springandvue.config.AuthAccess;
import com.su.springandvue.entity.SysFile;
import com.su.springandvue.entity.User;
import com.su.springandvue.mapper.FileMapper;
import com.su.springandvue.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/echarts")
public class EchartsController {

    @Autowired
    private UserService userService;

    @Resource
    private FileMapper fileMapper;

    @GetMapping("/example")
    public Result get(){
        Map<String, Object> map = new HashMap<>();
        map.put("x", CollUtil.newArrayList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
        map.put("y",CollUtil.newArrayList(150, 230, 224, 218, 135, 147, 260));
        return Result.success(map);
    }

    @GetMapping("/member")
    public Result VipMembers(){
        List<User> list = userService.list();
        int q1 = 0,q2 = 0,q3 = 0,q4 = 0;
        Random random = new Random();
        for (User user : list) {
            Date createTime = user.getCreateTime();
            Quarter quarter = DateUtil.quarterEnum(createTime);
            switch (quarter){
                case Q1:q1 += random.nextInt(1000);break;
                case Q2:q2 += random.nextInt(1000);break;
                case Q3:q3 += random.nextInt(1000);break;
                case Q4:q4 += random.nextInt(1000);break;
                default:break;
            }
        }

        return Result.success(CollUtil.newArrayList(q1,q2,q3,q4));
    }

    @AuthAccess
    @GetMapping("/file/front/all")
    @Cacheable(value = "files",key = "'findAll'")
    public Result findAll(){
        QueryWrapper<SysFile> query = new QueryWrapper<>();
        query.eq("is_delete",false).eq("enable",true);
        return Result.success(fileMapper.selectList(query));
    }
}
