package com.coderman.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coderman.video.model.SysFile;

public interface SysFileService extends IService<SysFile> {
    /**
     * 根据hash获取文件
     * @param fileHash 文件hash
     * @return
     */
    SysFile selectByHash(String fileHash);
}
