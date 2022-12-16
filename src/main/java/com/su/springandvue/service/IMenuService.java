package com.su.springandvue.service;

import com.su.springandvue.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author susu
 * @since 2022-11-26
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus(String name);
}
