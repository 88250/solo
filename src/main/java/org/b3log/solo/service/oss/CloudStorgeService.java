package org.b3log.solo.service.oss;

import static org.b3log.solo.model.Option.CATEGORY_C_ALIYUN;
import static org.b3log.solo.model.Option.CATEGORY_C_QINIU;
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
            "Cloud Storge settings failed, please visit http://hzchendou.com/articles/2018/12/09/1544343203696.html for more details";


    /**
     * 创建存储对象
     *
     * @return
     */
    public OssService createStorge() {
        final BeanManager beanManager = BeanManager.getInstance();
        final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
        JSONObject cloudStorgeJson = optionQueryService.getOptions(Option.CATEGORY_C_CLOU_STORGE);
        //查询存储服务商(默认使用qiniu)
        String cloudServer = CATEGORY_C_QINIU;
        if (cloudStorgeJson != null) {
            cloudServer = cloudStorgeJson.getString(ID_C_CLOUD_STORGE_KEY);
        }
        if (cloudServer == null || cloudServer.length() < 1) {
            throw new RuntimeException(ERR_MSG);
        }
        if (Objects.equals(cloudServer, CATEGORY_C_ALIYUN)) {
            return new AliyunOssService();
        }
        if (Objects.equals(cloudServer, CATEGORY_C_QINIU)) {
            return new QiniuOssService();
        }
        throw new RuntimeException(ERR_MSG);
    }
}
