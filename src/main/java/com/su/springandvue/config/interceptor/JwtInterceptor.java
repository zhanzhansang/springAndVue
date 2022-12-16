package com.su.springandvue.config.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.su.springandvue.common.Constants;
import com.su.springandvue.config.AuthAccess;
import com.su.springandvue.entity.User;
import com.su.springandvue.exception.ServiceException;
import com.su.springandvue.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        //如果请求的不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }else {
           HandlerMethod h = (HandlerMethod) handler;
            AuthAccess authAccess = h.getMethodAnnotation(AuthAccess.class);
            if ( authAccess != null){
                return true;
            }
        }
        //执行认证
        if (StrUtil.isBlank(token)){
            throw new ServiceException(Constants.CODE_401,"没有token,请重新登录");
        }
        //获取token中的userId
        String userId;
        try {
        userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException e) {
            throw new ServiceException(Constants.CODE_401,"token验证失败");
        }

        User user = userService.getById(userId);
        if (user == null){
            throw new ServiceException(Constants.CODE_401,"用户不存在,请重新登录");
        }

        //用户密码加签验证token
        JWTVerifier build = JWT.require(Algorithm.HMAC256(user.getPassword())).build();

        try {
            build.verify(token);
        } catch (JWTVerificationException e) {
            throw new ServiceException(Constants.CODE_401,"token验证失败");
        }
        return true;
    }
}
