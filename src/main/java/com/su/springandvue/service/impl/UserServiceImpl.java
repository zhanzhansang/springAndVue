package com.su.springandvue.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.su.springandvue.entity.User;
import com.su.springandvue.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author susu
 * @since 2022-11-21
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IService<User> {

}
