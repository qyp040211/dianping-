package com.guet.controller;


import cn.hutool.core.bean.BeanUtil;
import com.guet.dto.LoginFormDTO;
import com.guet.dto.UserDTO;
import com.guet.entity.BlogComments;
import com.guet.entity.User;
import com.guet.entity.UserInfo;
import com.guet.result.Result;
import com.guet.service.IBlogCommentsService;
import com.guet.service.IBlogService;
import com.guet.service.IUserInfoService;
import com.guet.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IUserInfoService userInfoService;

    @Resource
    private IBlogCommentsService blogCommentsService;

    @Autowired
    private IBlogService blogService;



    /**
     * 发送手机验证码
     */
    @PostMapping("code")
    public Result sendCode (@RequestParam("phone") String phone, HttpSession session) {
        return userService.sendCode(phone, session);
    }

    /**
     * 登录功能
     *
     * @param loginForm 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @PostMapping("/login")
    public Result login (@RequestBody LoginFormDTO loginForm, HttpSession session) {
        return userService.login(loginForm, session);
    }


    @PostMapping("/change-password")
    public Result changePassword(@RequestBody Map<String, String> param) {
        String phone = param.get("phone");
        String oldPassword = param.get("oldPassword"); // 可選
        String code = param.get("code");               // 可選
        String newPassword = param.get("newPassword");

        if (phone == null || newPassword == null) {
            return Result.fail("手机号和新密码不能为空");
        }

        return userService.changePassword(phone, oldPassword, code, newPassword);
    }


    /**
     * 登出功能
     * @return 无
     */
    @PostMapping("/logout")
    public Result logout (HttpServletRequest request) {
        return userService.logout(request);
    }

    @GetMapping("/me")
    public Result me () {
        return userService.me();
    }

    @GetMapping("/info/{id}")
    public Result info (@PathVariable("id") Long userId) {
        // 查询详情
        UserInfo info = userInfoService.getById(userId);
        if (info == null) {
            // 没有详情，应该是第一次查看详情
            return Result.ok();
        }
        info.setCreateTime(null);
        info.setUpdateTime(null);
        // 返回
        return Result.ok(info);
    }

    @GetMapping("/{id}")
    public Result queryUserById (@PathVariable("id") Long userId) {
        // 查询详情
        User user = userService.getById(userId);
        if (user == null) {
            return Result.ok();
        }
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        // 返回
        return Result.ok(userDTO);
    }

    @PostMapping("/sign")
    public Result userSign () {
        return userService.userSign();
    }

    @GetMapping("/sign/count")
    public Result signCount () {
        return userService.signCount();
    }



    @PostMapping("/info")
    public Result saveOrUpdateUserInfo(@RequestBody UserInfo userInfo) {
        if (userInfo == null || userInfo.getUserId() == null) {
            return Result.fail("缺少用戶ID");
        }

        // 強制排除 credits 和 level，防止前端傳入覆蓋
        userInfo.setCredits(null);
        userInfo.setLevel(null);

        boolean exists = userInfoService.getById(userInfo.getUserId()) != null;

        if (exists) {
            userInfo.setUpdateTime(LocalDateTime.now());
            userInfoService.updateById(userInfo);
        } else {
            userInfo.setCreateTime(LocalDateTime.now());
            userInfo.setUpdateTime(LocalDateTime.now());
            userInfoService.save(userInfo);
        }

        return Result.ok();
    }

    @PostMapping("/nickname")
    public Result updateNickName(@RequestBody Map<String, Object> param) {
        Long userId = param.get("userId") != null ? Long.parseLong(param.get("userId").toString()) : null;
        String nickName = param.get("nickName") != null ? param.get("nickName").toString() : null;

        if (userId == null || nickName == null || nickName.trim().isEmpty()) {
            return Result.fail("請提供有效的 userId 和 nickName");
        }

        // 更新用戶表中的暱稱
        User user = new User();
        user.setId(userId);
        user.setNickName(nickName);
        user.setUpdateTime(LocalDateTime.now());
        boolean success = userService.updateById(user);

        // 同步更新評論表中的暱稱
        if (success) {
            blogCommentsService.lambdaUpdate()
                    .eq(BlogComments::getUserId, userId)
                    .set(BlogComments::getNickName, nickName)
                    .update();
        }

        return success ? Result.ok() : Result.fail("昵稱修改失敗");
    }




}
