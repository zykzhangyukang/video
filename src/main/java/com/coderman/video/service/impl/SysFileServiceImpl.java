package com.coderman.video.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coderman.video.mapper.SysFileMapper;
import com.coderman.video.model.SysFile;
import com.coderman.video.service.SysFileService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ：zhangyukang
 * @date ：2025/05/29 14:38
 */
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements SysFileService {

    @Resource
    private SysFileMapper sysFileMapper;

    @Override
    public SysFile selectByHash(String fileHash) {

        return this.sysFileMapper.selectOne(Wrappers.<SysFile>lambdaQuery()
                .eq(SysFile::getFileHash, fileHash)
                .orderByDesc(SysFile::getId)
                .last("limit 1"));
    }
}
