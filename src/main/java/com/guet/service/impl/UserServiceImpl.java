package com.guet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.constants.SystemConstants;
import com.guet.dto.LoginFormDTO;
import com.guet.dto.UserDTO;
import com.guet.entity.User;
import com.guet.mapper.UserMapper;
import com.guet.result.Result;
import com.guet.service.IUserService;
import com.guet.utils.RegexUtil;
import com.guet.utils.UserHolderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.guet.constants.RedisConstants.*;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate srt;

    /**
     * @Author: 梁雨佳
     * @Date: 2024/2/15 17:42:40
     * @Description: 发送验证码
     */
    @Override
    public Result sendCode (String phone, HttpSession session) {
        // 1.验证手机号有效性
        if (RegexUtil.isPhoneInvalid(phone)) {
            // 2.如果不成功，提示手机号有误
            return Result.fail("手机号格式错误！");
        }

        // 3.如果成功，就生成验证码
        String code = RandomUtil.randomNumbers(6);

        // 4.将code保存到Redis中，3min后过期
        srt.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);

        // 5.发送验证码，需要使用第三方的发送短信平台，这里就不做了
        log.warn("发送短信验证码成功，验证码：{}", code);
        return Result.ok();
    }

    /**
     * @Author: 梁雨佳
     * @Date: 2024/2/15 17:42:48
     * @Description: 用户登录
     */
    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        String phone = loginForm.getPhone();

        // 校驗手機格式
        if (RegexUtil.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误！");
        }

        // 判斷是「密碼登錄」還是「驗證碼登錄」
        String password = loginForm.getPassword();
        if (password != null && !password.isEmpty()) {
            // ===== 密碼登錄邏輯 =====
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userMapper.selectOne(queryWrapper);

            if (user == null) {
                return Result.fail("用户不存在！");
            }

            if (!password.equals(user.getPassword())) {
                return Result.fail("密码错误！");
            }

            return loginSuccess(user); // 封裝 Token 和 Redis 的邏輯
        }

        // ===== 驗證碼登錄邏輯 =====
        String cacheCode = srt.opsForValue().get(LOGIN_CODE_KEY + phone);
        String code = loginForm.getCode();
        if (cacheCode == null || !cacheCode.equals(code)) {
            return Result.fail("验证码错误！");
        }

        // 驗證通過，查詢或創建用戶
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            user = insertUserWithPhone(phone);
        }

        // 刪除驗證碼
        srt.delete(LOGIN_CODE_KEY + phone);

        return loginSuccess(user);
    }

    private Result loginSuccess(User user) {
        String tokenKey = LOGIN_USER_KEY + UUID.randomUUID().toString(true);

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);

        Map<String, Object> map = BeanUtil.beanToMap(userDTO);
        map.replace("id", map.get("id").toString());

        srt.opsForHash().putAll(tokenKey, map);
        srt.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);

        return Result.ok(tokenKey);
    }




    /**
     * @Author: 梁雨佳
     * @Date: 2024/2/15 21:41:31
     * @Description: 插入数据，并设置初始的昵称
     */
    private User insertUserWithPhone (String phone) {
        User user = new User();
        user.setPhone(phone)
                .setNickName(SystemConstants.USER_NICK_NAME_PREFIX + RandomUtil.randomString(10))
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
        return user;
    }

    @Override
    public Result changePassword(String phone, String oldPassword, String code, String newPassword) {
        // 驗證手機號格式
        if (RegexUtil.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误！");
        }

        // 查找用戶
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return Result.fail("用户不存在！");
        }

        // 如果有輸入舊密碼，走密碼驗證邏輯
        if (oldPassword != null && !oldPassword.isEmpty()) {
            if (!oldPassword.equals(user.getPassword())) {
                return Result.fail("原密码错误！");
            }
        } else {
            // 否則走短信驗證碼邏輯
            String cacheCode = srt.opsForValue().get(LOGIN_CODE_KEY + phone);
            if (cacheCode == null || !cacheCode.equals(code)) {
                return Result.fail("验证码错误！");
            }
        }

        // 修改密碼
        user.setPassword(newPassword);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        return Result.ok("密码修改成功");
    }


    @Override
    public boolean updateUserIcon(Long userId, String iconPath) {
        return userMapper.updateUserIcon(userId, iconPath) > 0;
    }


    @Override
    public Result me () {
        return Result.ok(UserHolderUtil.getUser());
    }

    @Override
    public Result logout (HttpServletRequest request) {
        String token = request.getHeader("authorization");
        srt.delete(token);
        UserHolderUtil.removeUser();
        return Result.ok();
    }

    @Override
    public Result userSign () {
        // 获取当前用户
        Long userId = UserHolderUtil.getUser().getId();

        // 获取日期
        LocalDateTime now = LocalDateTime.now();
        String keySuffix = now.format(DateTimeFormatter.ofPattern("yyyyMM"));

        // 封装key
        String key = USER_SIGN_KEY + userId + ":" + keySuffix;

        // 得到今天是这个月的第几天，这样才能给对应BitMap索引赋值
        int dayOfMonth = now.getDayOfMonth();

        // 写入Redis，设置BitMap的值
        // 因为所以是从0开始，所以这里要-1；true代表写入1，使用boolean只需要1字节，int要4字节
        srt.opsForValue().setBit(key, dayOfMonth - 1, true);

        // 返回
        return Result.ok();
    }

    @Override
    public Result signCount () {
        // 获取当前用户
        Long userId = UserHolderUtil.getUser().getId();

        // 获取日期
        LocalDateTime now = LocalDateTime.now();
        String keySuffix = now.format(DateTimeFormatter.ofPattern("yyyyMM"));

        // 封装key
        String key = USER_SIGN_KEY + userId + ":" + keySuffix;

        // 得到今天是这个月的第几天，这样才能给对应BitMap索引赋值
        int dayOfMonth = now.getDayOfMonth();

        // 今天是本月的第几天，就去查多少个bit
        // BITFIELD key GET u3 0
        List<Long> results = srt.opsForValue().bitField(key,
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth))
                        .valueAt(0)
        );

        // 空说明没签到
        if (results.isEmpty()) {
            return Result.ok(0);
        }

        Long num = results.get(0);
        if (num == null) {
            return Result.ok(0);
        }
        // 因为只是get，所以这个集合只有一个值
        // 循环遍历十进制，让这位数字和1做与运算，判断它是否为0
        int count = 0;
        while (true) {
            // 数字和1做and运算
            if ((num & 1) == 0) {
                // 是0就结束
                break;
            } else {
                // 不是0就计数器+1
                count++;
            }
            // 数字右移一位，这样才能判断下一个bit
            num = num >> 1;
        }
        return Result.ok(count);
    }
}