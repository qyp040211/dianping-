package com.guet.controller;

import com.guet.result.Result;
import com.guet.service.IFollowService;
import com.guet.service.IUserInfoService;
import com.guet.utils.UserHolderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private IFollowService followService;

    @Autowired
    private IUserInfoService userInfoService;

    @PutMapping("/{id}/{isFollow}")
    public Result followOrNot(@PathVariable Long id, @PathVariable Boolean isFollow) {
        Long currentUserId = UserHolderUtil.getUser().getId();

        // 關注邏輯
        if (Boolean.TRUE.equals(isFollow)) {
            // +1 對方粉絲數
            userInfoService.lambdaUpdate()
                    .eq(com.guet.entity.UserInfo::getUserId, id)
                    .setSql("fans = fans + 1")
                    .update();
            // +1 自己關注數
            userInfoService.lambdaUpdate()
                    .eq(com.guet.entity.UserInfo::getUserId, currentUserId)
                    .setSql("followee = followee + 1")
                    .update();
        } else {
            // -1 對方粉絲數
            userInfoService.lambdaUpdate()
                    .eq(com.guet.entity.UserInfo::getUserId, id)
                    .setSql("fans = fans - 1")
                    .update();
            // -1 自己關注數
            userInfoService.lambdaUpdate()
                    .eq(com.guet.entity.UserInfo::getUserId, currentUserId)
                    .setSql("followee = followee - 1")
                    .update();
        }

        return followService.followOrNot(id, isFollow);
    }

    @GetMapping("/or/not/{id}")
    public Result queryIsFollow(@PathVariable Long id) {
        return followService.queryIsFollow(id);
    }

    @GetMapping("/common/{id}")
    public Result queryCommon(@PathVariable Long id) {
        return followService.queryCommon(id);
    }
}
