package org.b3log.solo.service.oss;

import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Option;
import org.b3log.solo.service.OptionQueryService;
import org.json.JSONObject;

import java.util.Objects;

import static org.b3log.solo.model.Option.*;

/**
 * Cloud object storage service.
 *
 * @author <a href="https://github.com/hzchendou">hzchendou</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Dec 23, 2018
 * @since 2.9.8
 */
@Service
public class CloudStorageService {

    /**
     * Creates an object storage service.
     *
     * @return an object storage service
     */
    public OssService createStorage() {
        final BeanManager beanManager = BeanManager.getInstance();
        final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
        String cloudServer = CATEGORY_C_QINIU;
        final JSONObject cloudStorageJson = optionQueryService.getOptions(Option.CATEGORY_C_CLOU_STORAGE);
        if (cloudStorageJson != null) {
            cloudServer = cloudStorageJson.getString(ID_C_CLOUD_STORAGE_KEY);
        }

        if (cloudServer == null || cloudServer.length() < 1) {
            throw new IllegalStateException(AliyunOssService.ERR_MSG);
        }

        if (Objects.equals(cloudServer, CATEGORY_C_ALIYUN)) {
            return new AliyunOssService();
        }
        if (Objects.equals(cloudServer, CATEGORY_C_QINIU)) {
            return new QiniuOssService();
        }

        throw new IllegalStateException(AliyunOssService.ERR_MSG);
    }
}
