package org.b3log.solo.service.oss;

import jodd.io.upload.FileUpload;

/**
 * 对象存储服务.
 *
 * @author <a href="https://github.com/hzchendou">hzchendou</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Dec 23, 2018
 * @since 2.9.8
 */
public interface OssService {

    /**
     * 上传文件，返回文件链接.
     *
     * @param file     the specified file
     * @param fileName the specified filename
     * @return 文件外链地址
     */
    String upload(final FileUpload file, final String fileName) throws Exception;

    /**
     * 销毁.
     */
    default void close() {
    }
}
