package org.b3log.solo.service.oss;

import static org.b3log.solo.model.Option.ID_C_CLOUD_STORGE_KEY;

import java.util.Objects;

import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Option;
import org.b3log.solo.service.OptionQueryService;
import org.json.JSONObject;

/**
 * 云存储服务.
 *
 * @author hzchendou
 * @date 2018/12/5
 * @since 1.0
 */
@Service
public class CloudStorgeService {

    public static final String ERR_MSG =
            "Cloud Storge settings failed, please visit https://hacpai.com/article/1442418791213 for more details";


    /**
     * 创建存储对象
     *
     * @return
     */
    public OssService createStorge() {
        final BeanManager beanManager = BeanManager.getInstance();
        final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
        JSONObject cloudStorgeJson = optionQueryService.getOptions(Option.CATEGORY_C_CLOU_STORGE);
        if (cloudStorgeJson == null) {
            throw new RuntimeException(ERR_MSG);
        }
        //查询云存储服务商
        String cloudServer = cloudStorgeJson.getString(ID_C_CLOUD_STORGE_KEY);
        if (cloudServer == null || cloudServer.length() < 1) {
            throw new RuntimeException(ERR_MSG);
        }
        if (Objects.equals(cloudServer, "aliyun")) {
            return new AliyunOssService();
        }
        if (Objects.equals(cloudServer, "qiniu")) {
            return new QiniuOssService();
        }
        throw new RuntimeException(ERR_MSG);
    }
}
