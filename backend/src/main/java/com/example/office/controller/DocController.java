package com.example.office.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.office.common.Result;
import com.example.office.entity.DocInfo;
import com.example.office.entity.SysUser;
import com.example.office.service.DocInfoService;
import com.example.office.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/document")
public class DocController {

    @Autowired
    private DocInfoService docInfoService;
    
    @Autowired
    private SysUserService sysUserService;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @GetMapping("/list")
    public Result<List<DocInfo>> list() {
        // ID 降序，最新的在上面
        List<DocInfo> docs = docInfoService.list(new QueryWrapper<DocInfo>().orderByDesc("id"));
        for (DocInfo doc : docs) {
            SysUser user = sysUserService.getById(doc.getUploaderId());
            if (user != null) {
                doc.setUploaderName(user.getRealName() != null ? user.getRealName() : user.getUsername());
            } else {
                doc.setUploaderName("系统");
            }
        }
        return Result.success(docs);
    }

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        try {
            // 获取当前登录用户
            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            SysUser user = sysUserService.getByUsername(username);
            
            // 保存在项目根目录的绝对路径 uploads/docs/ 下
            File dir = new File(uploadDir + File.separator + "docs");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            // UUID 防止重名
            String newFilename = UUID.randomUUID().toString().replace("-", "") + ext;
            File dest = new File(dir.getAbsolutePath() + File.separator + newFilename);
            file.transferTo(dest);
            
            // 构建数据库实体，存入 doc_info 表
            DocInfo doc = new DocInfo();
            doc.setTitle(originalFilename);
            doc.setFileName(originalFilename);
            doc.setFileUrl(dest.getAbsolutePath());
            doc.setFileSize(file.getSize());
            doc.setFileType(ext.replace(".", ""));
            doc.setUploaderId(user.getId());
            doc.setIsShare(1); // 默认公开共享
            
            docInfoService.save(doc);
            
            return Result.success("文件上传成功: " + originalFilename);
        } catch(Exception e) {
            e.printStackTrace();
            return Result.error("文件读写失败：" + e.getMessage());
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable("id") Long id) throws Exception {
        DocInfo target = docInfoService.getById(id);
        if (target == null) {
            return ResponseEntity.notFound().build();
        }
        
        File file = new File(target.getFileUrl());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(file.toURI());
        // 解决中文文件名下载乱码
        String encodedFilename = URLEncoder.encode(target.getFileName(), "UTF-8").replaceAll("\\+", "%20");
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"")
            .body(resource);
    }
}
