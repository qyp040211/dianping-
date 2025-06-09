package com.guet.service.impl;

import com.guet.entity.UserInfo;
import com.guet.mapper.UserInfoMapper;
import com.guet.service.IUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

}
