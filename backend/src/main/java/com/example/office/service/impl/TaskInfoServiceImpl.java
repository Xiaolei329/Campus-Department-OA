package com.example.office.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.office.entity.TaskInfo;
import com.example.office.mapper.TaskInfoMapper;
import com.example.office.service.TaskInfoService;
import org.springframework.stereotype.Service;

@Service
public class TaskInfoServiceImpl extends ServiceImpl<TaskInfoMapper, TaskInfo> implements TaskInfoService {
}
