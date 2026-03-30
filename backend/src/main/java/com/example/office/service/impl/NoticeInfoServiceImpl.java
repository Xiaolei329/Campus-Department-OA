package com.example.office.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.office.entity.NoticeInfo;
import com.example.office.mapper.NoticeInfoMapper;
import com.example.office.service.NoticeInfoService;
import org.springframework.stereotype.Service;

@Service
public class NoticeInfoServiceImpl extends ServiceImpl<NoticeInfoMapper, NoticeInfo> implements NoticeInfoService {
}
