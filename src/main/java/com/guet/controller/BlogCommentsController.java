package com.guet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guet.dto.UserDTO;
import com.guet.entity.Blog;
import com.guet.entity.BlogComments;
import com.guet.result.Result;
import com.guet.service.IBlogCommentsService;
import com.guet.service.IBlogService;
import com.guet.utils.UserHolderUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/blog-comments")
public class BlogCommentsController {

    @Resource
    private IBlogCommentsService blogCommentsService;

    @Resource
    private IBlogService blogService;

    // 發佈一級評論（需先驗證 Blog 存在）
    @PostMapping
    public Result addTopLevelComment(@RequestBody BlogComments comment) {
        UserDTO user = UserHolderUtil.getUser();

        Blog blog = blogService.getById(comment.getBlogId());
        if (blog == null) {
            return Result.fail("博客不存在");
        }

        comment.setUserId(user.getId());
        comment.setNickName(user.getNickName()); // 新增設置用戶暱稱
        comment.setParentId(0L);
        comment.setAnswerId(0L);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());

        boolean success = blogCommentsService.save(comment);
        return success ? Result.ok() : Result.fail("評論失敗");
    }

    // 刪除評論（需先驗證 Blog 存在 & 是本人評論）
    @DeleteMapping("/{id}")
    public Result deleteComment(@PathVariable("id") Long id) {
        UserDTO user = UserHolderUtil.getUser();

        BlogComments comment = blogCommentsService.getById(id);
        if (comment == null) {
            return Result.fail("評論不存在");
        }
        Blog blog = blogService.getById(comment.getBlogId());
        if (blog == null) {
            return Result.fail("原始博客已被刪除");
        }
        if (!comment.getUserId().equals(user.getId())) {
            return Result.fail("無權刪除他人評論");
        }

        boolean removed = blogCommentsService.removeById(id);
        return removed ? Result.ok() : Result.fail("刪除失敗");
    }

    // 根據博客 ID 查詢所有一級評論
    @GetMapping("/of/blog/{blogId}")
    public Result getCommentsByBlogId(@PathVariable("blogId") Long blogId) {
        Blog blog = blogService.getById(blogId);
        if (blog == null) {
            return Result.fail("博客不存在");
        }

        List<BlogComments> comments = blogCommentsService.list(
                new QueryWrapper<BlogComments>()
                        .eq("blog_id", blogId)
                        .eq("parent_id", 0)
                        .orderByDesc("create_time")
        );
        return Result.ok(comments);
    }
}
