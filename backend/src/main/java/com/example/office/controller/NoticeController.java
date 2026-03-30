package com.example.office.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.office.common.Result;
import com.example.office.entity.NoticeInfo;
import com.example.office.entity.SysUser;
import com.example.office.service.NoticeInfoService;
import com.example.office.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeInfoService noticeInfoService;
    
    @Autowired
    private SysUserService sysUserService;

    @org.springframework.cache.annotation.Cacheable(value = "noticeList")
    @GetMapping("/list")
    public Result<Page<NoticeInfo>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title) {
        
        QueryWrapper<NoticeInfo> wrapper = new QueryWrapper<>();
        if (title != null && !title.trim().isEmpty()) {
            wrapper.like("title", title);
        }
        
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!"admin".equals(username) && !"manager".equals(username)) {
            wrapper.eq("status", 1);
        }
        
        wrapper.orderByDesc("create_time");
        
        Page<NoticeInfo> page = noticeInfoService.page(new Page<>(pageNum, pageSize), wrapper);
        
        for (NoticeInfo notice : page.getRecords()) {
            SysUser user = sysUserService.getById(notice.getPublisherId());
            if (user != null) {
                notice.setPublisherName(user.getRealName() != null ? user.getRealName() : user.getUsername());
            }
        }
        
        return Result.success(page);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "noticeList", allEntries = true)
    @PostMapping("/add")
    public Result<String> add(@RequestBody NoticeInfo notice) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!"admin".equals(username) && !"manager".equals(username)) {
            return Result.error(403, "权限不足，仅管理员或部门负责人可发布公告");
        }
        SysUser user = sysUserService.getByUsername(username);
        
        notice.setPublisherId(user.getId());
        notice.setCreateTime(new Date());
        notice.setUpdateTime(new Date());
        noticeInfoService.save(notice);
        return Result.success("公告发布成功");
    }

    @org.springframework.cache.annotation.CacheEvict(value = "noticeList", allEntries = true)
    @PutMapping("/update")
    public Result<String> update(@RequestBody NoticeInfo notice) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!"admin".equals(username) && !"manager".equals(username)) {
            return Result.error(403, "权限不足，仅管理员或部门负责人可修改公告");
        }
        notice.setUpdateTime(new Date());
        noticeInfoService.updateById(notice);
        return Result.success("公告修改成功");
    }

    @org.springframework.cache.annotation.CacheEvict(value = "noticeList", allEntries = true)
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!"admin".equals(username) && !"manager".equals(username)) {
            return Result.error(403, "权限不足，仅管理员或部门负责人可删除公告");
        }
        noticeInfoService.removeById(id);
        return Result.success("公告删除成功");
    }
}
