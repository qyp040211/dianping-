package com.guet.service;

import com.guet.entity.Follow;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guet.result.Result;

public interface IFollowService extends IService<Follow> {

    Result followOrNot (Long id, Boolean isFollow);

    Result queryIsFollow (Long id);

    Result queryCommon (Long id);
}
