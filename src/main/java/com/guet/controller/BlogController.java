package com.guet.controller;

import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guet.constants.SystemConstants;
import com.guet.dto.UserDTO;
import com.guet.entity.Blog;
import com.guet.entity.UserInfo;
import com.guet.result.Result;
import com.guet.service.IBlogService;
import com.guet.service.IUserInfoService;
import com.guet.utils.UserHolderUtil;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController {

    @Resource
    private IBlogService blogService;

    @Resource
    private IUserInfoService userInfoService;

    // 發佈 Blog 並加積分
    @PostMapping
    public Result saveBlog(@RequestBody Blog blog) {
        UserDTO user = UserHolderUtil.getUser();
        blog.setUserId(user.getId());
        Result result = blogService.saveBlogAndPush(blog);
        // 發佈成功則加分
        if (result.getSuccess()) {
            userInfoService.lambdaUpdate()
                    .eq(UserInfo::getUserId, user.getId())
                    .setSql("credits = credits + 1")
                    .update();
        }
        return result;
    }
    // 点赞 Blog 并给作者加积分，同时更新用户的点赞记录
    @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id) {
        Blog blog = blogService.getById(id);
        if (blog == null) {
            return Result.fail("博客不存在");
        }

        // 获取当前登录的用户
        UserDTO user = UserHolderUtil.getUser();
        UserInfo userInfo = userInfoService.getById(user.getId());

        // 如果当前用户已经点踩该博客，先取消点踩
        if (userInfo.getDislikedBlogIds() != null && userInfo.getDislikedBlogIds().contains(id.toString())) {
            // 移除点踩记录
            userInfo.setDislikedBlogIds(userInfo.getDislikedBlogIds().replace(id.toString(), ""));
        }

        // 添加当前博客的点赞记录
        String likedBlogs = userInfo.getLikedBlogIds();
        if (likedBlogs == null || likedBlogs.trim().isEmpty() || likedBlogs.equals("[]")) {
            likedBlogs = "[]";  // 初始化为空的 JSON 数组
        }
        JSONArray jsonArray = new JSONArray(likedBlogs);  // 使用 JSONArray 处理
        if (!jsonArray.toString().contains("\"" + id.toString() + "\"")) {  // 确保 id 被双引号包围
            jsonArray.put(id.toString());  // 将 id 作为字符串加入 JSON 数组
        }

        userInfo.setLikedBlogIds(cleanEmptyEntries(jsonArray).toString());  // 将 JSON 数组转换为字符串存入
        userInfoService.updateById(userInfo);  // 更新数据库

        Result result = blogService.likeBlog(id);
        if (result.getSuccess()) {
            // 给作者加积分
            if (blog.getUserId() != user.getId()) {
                userInfoService.lambdaUpdate()
                        .eq(UserInfo::getUserId, blog.getUserId())
                        .setSql("credits = credits + 1")
                        .update();
            }
        }

        return result;
    }

    // 点踩 Blog，扣除作者积分并更新用户的点踩记录
    @PutMapping("/dislike/{id}")
    public Result dislikeBlog(@PathVariable("id") Long id) {
        Blog blog = blogService.getById(id);
        if (blog == null) {
            return Result.fail("博客不存在");
        }

        // 获取当前登录的用户
        UserDTO user = UserHolderUtil.getUser();
        UserInfo userInfo = userInfoService.getById(user.getId());

        // 如果当前用户已经点赞该博客，先取消点赞
        if (userInfo.getLikedBlogIds() != null && userInfo.getLikedBlogIds().contains(id.toString())) {
            // 移除点赞记录
            userInfo.setLikedBlogIds(userInfo.getLikedBlogIds().replace(id.toString(), ""));
        }

        // 添加当前博客的点踩记录
        String dislikedBlogs = userInfo.getDislikedBlogIds();
        if (dislikedBlogs == null || dislikedBlogs.trim().isEmpty() || dislikedBlogs.equals("[]")) {
            dislikedBlogs = "[]";  // 初始化为空的 JSON 数组
        }
        JSONArray jsonArray = new JSONArray(dislikedBlogs);  // 使用 JSONArray 处理
        if (!jsonArray.toString().contains("\"" + id.toString() + "\"")) {  // 确保 id 被双引号包围
            jsonArray.put(id.toString());  // 将 id 作为字符串加入 JSON 数组
        }

        userInfo.setDislikedBlogIds(cleanEmptyEntries(jsonArray).toString());  // 将 JSON 数组转换为字符串存入
        userInfoService.updateById(userInfo);  // 更新数据库


        Result result = blogService.disLikeBlog(id);

        if (result.getSuccess()) {
            // 给作者減积分
            if (blog.getUserId() != user.getId()) {
                userInfoService.lambdaUpdate()
                        .eq(UserInfo::getUserId, blog.getUserId())
                        .setSql("credits = credits - 1")
                        .update();
            }
        }

        return result;
    }

    // 清理 JSON 数组中的空字符串或空元素
    private JSONArray cleanEmptyEntries(JSONArray jsonArray) {
        // 遍历数组，检查并移除空字符串
        for (int i = 0; i < jsonArray.size(); i++) {
            if (jsonArray.get(i).equals("")) {
                jsonArray.remove(i);
                i--; // 需要调整索引，因为移除元素后，后面的元素会向前移动
            }
        }
        return jsonArray; // 返回清理后的 JSON 数组
    }


    @GetMapping("/of/me")
    public Result queryMyBlog (@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 获取登录用户
        UserDTO user = UserHolderUtil.getUser();
        // 根据用户查询
        Page<Blog> page = blogService.query()
                .eq("user_id", user.getId()).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }

    @GetMapping("/hot")
    public Result queryHotBlog (@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return blogService.queryHotBlog(current);
    }

    @GetMapping("/{id}")
    public Result queryBlogById (@PathVariable("id") Long id) throws Exception {
        return blogService.queryBlogById(id);
    }

    @GetMapping("/likes/{id}")
    public Result queryTop5 (@PathVariable Long id) {
        return Result.ok(blogService.queryTop5(id));
    }

    @GetMapping("/of/user")
    public Result queryBlogByUserId (
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam("id") Long id) {
        // 根据用户查询
        Page<Blog> page = blogService.query()
                .eq("user_id", id).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }

    @GetMapping("/of/follow")
    public Result queryBlogOfFollow (@RequestParam("lastId") Long maxTime,
                                     @RequestParam(defaultValue = "0") Integer offset) {
        return blogService.queryBlogOfFollow(maxTime,offset);
    }
}
