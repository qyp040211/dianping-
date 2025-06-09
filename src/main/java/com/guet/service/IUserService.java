package com.guet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guet.dto.LoginFormDTO;
import com.guet.result.Result;
import com.guet.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface IUserService extends IService<User> {

    Result sendCode (String phone, HttpSession session);

    Result login (LoginFormDTO loginForm, HttpSession session);

    Result changePassword(String phone, String oldPassword, String code, String newPassword);

    Result me ();

    Result logout (HttpServletRequest request);

    boolean updateUserIcon(Long userId, String iconPath);

    Result userSign ();

    Result signCount ();
}
