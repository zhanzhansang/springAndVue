package com.su.springandvue.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.su.springandvue.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author susu
 * @since 2022-11-21
 */
public interface UserMapper extends BaseMapper<User> {

    Page<User> findPage(Page<User> page, @Param("username") String username);

    @Update("update sys_user set password = #{password} where id = #{id}")
    int updatePassword(@Param("id") Integer id,@Param("password") String password);
}
