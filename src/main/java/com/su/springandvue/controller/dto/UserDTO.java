package com.su.springandvue.controller.dto;

import com.su.springandvue.entity.Menu;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String avatarUrl;
    private String token;
    private String role;
    private List<Menu> menus;
}
