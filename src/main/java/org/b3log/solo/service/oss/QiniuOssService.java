package org.b3log.solo.service.oss;

import java.io.IOException;

import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.solo.model.Option;
import org.b3log.solo.service.OptionQueryService;
import org.json.JSONObject;

import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import jodd.io.upload.FileUpload;

/**
 * 请求对象存储服务.
 *
 * @author hzchendou
 * @date 2018/12/5
 * @since 1.0
 */
public class QiniuOssService implements OssService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CloudStorgeService.class);

    private static final String ERR_MSG = "Qiniu settings failed, please visit https://hacpai.com/article/1442418791213 for more details";

    /**
     * 七牛数据库对象
     */
    private JSONObject qiniu = null;

    /**
     * 上传文件对象
     */
    private UploadManager uploadManager = null;

    /**
     * 上传凭证Token
     */
    private String uploadToken = null;

    /**
     * 创建七牛oos服务对象
     */
    public QiniuOssService() {
        try {
            final BeanManager beanManager = BeanManager.getInstance();
            final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
            qiniu = optionQueryService.getOptions(Option.CATEGORY_C_QINIU);
            if (null == qiniu) {
                LOGGER.log(Level.ERROR, ERR_MSG);
                throw new RuntimeException();
            }
            Auth auth = Auth.create(qiniu.optString(Option.ID_C_QINIU_ACCESS_KEY),
                    qiniu.optString(Option.ID_C_QINIU_SECRET_KEY));
            uploadToken = auth.uploadToken(qiniu.optString(Option.ID_C_QINIU_BUCKET), null, 3600 * 6, null);
            uploadManager = new UploadManager(new Configuration());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, ERR_MSG);
            throw new RuntimeException(ERR_MSG);
        }
    }

    /**
     * 上传文件
     *
     * @param file
     * @param fileName
     * @return
     */
    @Override
    public String upload(FileUpload file, String fileName) throws IOException {
        final String contentType = file.getHeader().getContentType();
        uploadManager.put(file.getFileInputStream(), fileName, uploadToken, null, contentType);
        return qiniu.optString(Option.ID_C_QINIU_DOMAIN) + "/" + fileName;
    }
}
