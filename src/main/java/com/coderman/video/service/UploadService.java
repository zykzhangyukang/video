package com.coderman.video.service;

import com.coderman.video.request.UploadCheckRequest;
import com.coderman.video.request.UploadInitRequest;
import com.coderman.video.request.UploadMergeRequest;
import com.coderman.video.request.UploadPartRequest;
import com.coderman.video.vo.UploadCheckVO;

import java.io.IOException;

public interface UploadService {
    /**
     * 检查上传任务是否已存在（用于秒传、断点续传判断）。
     *
     * @param request 上传检查请求，包含 fileHash、fileName 等信息
     * @return UploadCheckVO 返回是否需要上传、已上传分片等信息
     */
    UploadCheckVO checkUpload(UploadCheckRequest request);

    /**
     * 初始化上传任务，生成并返回 uploadId，用于后续分片上传标识。
     *
     * @param request 上传初始化请求，包含 fileHash、fileName、总大小等参数
     * @return String 返回上传任务 ID（uploadId）
     */
    String initUpload(UploadInitRequest request);

    /**
     * 上传单个分片数据。
     *
     * @param request 上传分片请求，包含 uploadId、分片索引、分片数据等
     * @throws IOException 分片写入过程中可能发生的 IO 异常
     */
    void uploadPart(UploadPartRequest request) throws IOException;

    /**
     * 合并所有已上传分片，生成最终完整文件。
     *
     * @param request 合并请求，包含 uploadId、分片数量、目标路径等
     * @return String 返回最终生成文件的 URL 或本地路径
     * @throws IOException 合并过程中可能发生的 IO 异常
     */
    String mergeUpload(UploadMergeRequest request) throws IOException;
}
