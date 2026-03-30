package com.example.office.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.office.entity.DocInfo;
import com.example.office.mapper.DocInfoMapper;
import com.example.office.service.DocInfoService;
import org.springframework.stereotype.Service;

@Service
public class DocInfoServiceImpl extends ServiceImpl<DocInfoMapper, DocInfo> implements DocInfoService {
}
