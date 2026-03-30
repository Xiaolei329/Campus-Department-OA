package com.example.office.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.office.common.Result;
import com.example.office.entity.MeetingInfo;
import com.example.office.mapper.MeetingInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

@RestController
@RequestMapping("/api/meeting")
public class MeetingController {

    @Autowired
    private MeetingInfoMapper meetingInfoMapper;

    @org.springframework.cache.annotation.Cacheable(value = "meetingList")
    @GetMapping("/list")
    public Result<java.util.List<MeetingInfo>> list() {
        return Result.success(meetingInfoMapper.selectList(new QueryWrapper<MeetingInfo>().orderByDesc("create_time")));
    }

    @org.springframework.cache.annotation.CacheEvict(value = "meetingList", allEntries = true)
    @PostMapping("/save")
    public Result<String> save(@RequestBody MeetingInfo meeting) {
        meeting.setCreateTime(new Date());
        meetingInfoMapper.insert(meeting);
        return Result.success("会议预约发布成功");
    }
}
