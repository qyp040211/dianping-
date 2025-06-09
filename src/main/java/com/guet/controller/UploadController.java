package com.guet.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.guet.result.Result;
import com.guet.constants.SystemConstants;
import com.guet.service.IBlogService;
import com.guet.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IBlogService blogService;
    // 博客圖片上傳並更新圖片路徑


    // 博客圖片上傳
    @PostMapping("blog")
    public Result uploadBlogImage(@RequestParam("file") MultipartFile image) {
        return uploadImageByType1(image, "blogs");
    }

    // 通用圖片上傳方法（根據類型分目錄）
    private Result uploadImageByType1(MultipartFile image, String type) {
        try {
            String originalFilename = image.getOriginalFilename();
            String fileName = createNewFileName(originalFilename, type);

            // ⬇️ 確保最終儲存的完整目錄存在
            File destFile = new File(SystemConstants.IMAGE_UPLOAD_DIR, fileName);
            File parentDir = destFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            image.transferTo(destFile);
            log.debug("文件上传成功 [{}]: {}", type, fileName);
            return Result.ok(fileName);
        } catch (IOException e) {
            log.error("文件上传失败 [{}]", type, e);
            throw new RuntimeException("文件上传失败", e);
        }
    }



    // 用戶頭像上傳並更新icon欄位
    @PostMapping("avatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile image, @RequestParam("userId") Long userId) {
        try {
            // 上傳圖片並獲得路徑
            String imagePath = uploadImageByType(image, "avatars");

            // 更新對應用戶的icon欄位
            boolean updateSuccess = userService.updateUserIcon(userId, imagePath);
            if (updateSuccess) {
                return Result.ok(imagePath);
            } else {
                return Result.fail("用戶頭像更新失敗");
            }
        } catch (Exception  e) {
            log.error("文件上傳失敗", e);
            return Result.fail("文件上傳失敗");
        }
    }


    // 博客圖片刪除
    @GetMapping("/blog/delete")
    public Result deleteBlogImg(@RequestParam("name") String filename) {
        File file = new File(SystemConstants.IMAGE_UPLOAD_DIR, filename);
        if (file.isDirectory()) {
            return Result.fail("错误的文件名称");
        }
        FileUtil.del(file);
        return Result.ok();
    }

    // 通用圖片上傳方法（根據類型分目錄），並返回完整的路徑
    private String uploadImageByType(MultipartFile image, String type) {
        try {
            // 獲取原始檔案名
            String originalFilename = image.getOriginalFilename();
            // 生成新檔案名稱
            String fileName = createNewFileName(originalFilename, type);

            // 確保最終儲存的完整目錄存在
            File destFile = new File(SystemConstants.IMAGE_UPLOAD_DIR, fileName);
            File parentDir = destFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            // 上傳文件
            image.transferTo(destFile);
            log.debug("文件上传成功 [{}]: {}", type, fileName);

            // 返回儲存的完整路徑
            return SystemConstants.IMAGE_UPLOAD_DIR + File.separator + fileName;
        } catch (IOException e) {
            log.error("文件上传失败 [{}]", type, e);
            throw new RuntimeException("文件上传失败", e);
        }
    }



    // 根據類型生成圖片儲存路徑與名稱
    private String createNewFileName(String originalFilename, String type) {
        String suffix = StrUtil.subAfter(originalFilename, ".", true);
        String name = UUID.randomUUID().toString();
        int hash = name.hashCode();
        int d1 = hash & 0xF;
        int d2 = (hash >> 4) & 0xF;

        return StrUtil.format("{}{}{}{}{}{}{}.{}", type, File.separator, d1, File.separator, d2, File.separator, name, suffix);
    }

}
