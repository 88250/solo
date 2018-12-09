package org.b3log.solo.service.oss;

import java.io.IOException;

import jodd.io.upload.FileUpload;

/**
 * 对象存储服务.
 *
 * @author hzchendou
 * @date 2018/12/5
 * @since 1.0
 */
public interface OssService {

    /**
     * 上传文件，返回文件链接
     *
     * @param file
     * @param fileName
     * @return
     */
    String upload(FileUpload file, String fileName) throws IOException;

    /**
     * 销毁
     */
    default void close() {}
}
