package com.example.office.controller;

import com.example.office.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传控制器
 * 负责接收上传文件，存储到本地 uploads 目录，并返回可访问的 URL
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    /**
     * 通用文件上传接口
     * @param file 上传的文件（图片、文档等）
     * @return 文件访问路径
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        try {
            // 头像存储在单独的 avatars 子目录下，与共享文档区分开
            File dir = new File(uploadDir + File.separator + "avatars");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 使用 UUID 重命名文件，防止文件名冲突
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString().replace("-", "") + ext;

            // 保存文件到本地
            File dest = new File(dir.getAbsolutePath() + File.separator + newFilename);
            file.transferTo(dest);

            // 返回可通过浏览器直接访问的 URL 路径
            String accessUrl = "/uploads/avatars/" + newFilename;
            return Result.success(accessUrl);

        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}
