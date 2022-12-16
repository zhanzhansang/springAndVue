package com.su.springandvue.utils;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.su.springandvue.entity.User;
import com.su.springandvue.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class TokenUtils {

    private static UserService staticUserService;

    @Resource
    private  UserService userService;

    @PostConstruct
    public void setUserService(){
        staticUserService = userService;
    }

    /**
     * 生成token
     * @param userId
     * @param sign
     * @return
     */
    public static String genToken(String userId,String sign){

       return JWT.create().withAudience(userId)
                    .withExpiresAt(DateUtil.offsetHour(new Date(),2))
                    .sign(Algorithm.HMAC256(sign));
    }

    /**
     * 获取当前用户信息
     * @return
     */
    public static User getCurrentUser(){
        try {
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (!StrUtil.isBlank(token)){
                String userId = JWT.decode(token).getAudience().get(0);
                return staticUserService.getById(userId);
            }
        }catch (Exception e){
            return null;
        }

        return null;
    }
}
