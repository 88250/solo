package org.b3log.solo.service.oss;

import com.aliyun.oss.OSSClient;
import jodd.io.upload.FileUpload;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.solo.model.Option;
import org.b3log.solo.service.OptionQueryService;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * 阿里云对象存储服务.
 *
 * @author <a href="https://github.com/hzchendou">hzchendou</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Dec 23, 2018
 * @since 2.9.8
 */
public class AliyunOssService implements OssService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AliyunOssService.class);

    /**
     * 阿里云 Oss 配置报错文案.
     */
    public static final String ERR_MSG =
            "Aliyun settings failed, please visit http://hzchendou.com/articles/2018/12/09/1544343203696.html for more details";

    /**
     * Oss 客户端.
     */
    private OSSClient ossClient;

    /**
     * 阿里云数据库对象.
     */
    private JSONObject aliyun;

    /**
     * 文件路径前缀.
     */
    private String OSS_FILE_PATH_PREFIX = null;

    /**
     * 创建阿里云对象存储.
     */
    public AliyunOssService() {
        try {
            final BeanManager beanManager = BeanManager.getInstance();
            final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
            aliyun = optionQueryService.getOptions(Option.CATEGORY_C_ALIYUN);
            if (null == aliyun) {
                LOGGER.log(Level.ERROR, ERR_MSG);

                throw new IllegalStateException();
            }
            ossClient = new OSSClient(aliyun.optString(Option.ID_C_ALIYUN_DOMAIN),
                    aliyun.optString(Option.ID_C_ALIYUN_ACCESS_KEY), aliyun.optString(Option.ID_C_ALIYUN_SECRET_KEY));
            initFilfPrefix();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, ERR_MSG);

            throw new IllegalStateException(ERR_MSG);
        }
    }

    @Override
    public String upload(final FileUpload file, final String fileName) throws IOException {
        ossClient.putObject(aliyun.optString(Option.ID_C_ALIYUN_BUCKET), fileName, file.getFileInputStream());

        return OSS_FILE_PATH_PREFIX + fileName;
    }

    @Override
    public void close() {
        ossClient.shutdown();
    }


    /**
     * 初始化路径前缀.
     */
    private void initFilfPrefix() {
        try {
            String bucketName = aliyun.optString(Option.ID_C_ALIYUN_BUCKET);
            URL url = new URL(aliyun.optString(Option.ID_C_ALIYUN_DOMAIN));
            OSS_FILE_PATH_PREFIX = "http://" + bucketName + "." + url.getHost() + "/";
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, ERR_MSG);

            throw new IllegalStateException(ERR_MSG);
        }
    }
}
