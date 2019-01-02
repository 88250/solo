/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2019, b3log.org & hacpai.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.b3log.solo.service.oss;

import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import jodd.io.upload.FileUpload;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.solo.model.Option;
import org.b3log.solo.service.OptionQueryService;
import org.json.JSONObject;

/**
 * 七牛云对象存储服务.
 *
 * @author <a href="https://github.com/hzchendou">hzchendou</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Dec 23, 2018
 * @since 2.9.8
 */
public class QiniuOssService implements OssService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CloudStorageService.class);

    private static final String ERR_MSG = "Qiniu settings failed, please visit https://hacpai.com/article/1442418791213 for more details";

    /**
     * 七牛数据库对象.
     */
    private JSONObject qiniu;

    /**
     * 上传文件对象.
     */
    private UploadManager uploadManager;

    /**
     * 上传凭证 Token.
     */
    private String uploadToken;

    /**
     * 创建七牛云 Oss 服务对象
     */
    public QiniuOssService() {
        try {
            final BeanManager beanManager = BeanManager.getInstance();
            final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
            qiniu = optionQueryService.getOptions(Option.CATEGORY_C_QINIU);
            if (null == qiniu) {
                LOGGER.log(Level.ERROR, ERR_MSG);

                throw new IllegalStateException();
            }
            Auth auth = Auth.create(qiniu.optString(Option.ID_C_QINIU_ACCESS_KEY),
                    qiniu.optString(Option.ID_C_QINIU_SECRET_KEY));
            uploadToken = auth.uploadToken(qiniu.optString(Option.ID_C_QINIU_BUCKET), null, 3600 * 6, null);
            uploadManager = new UploadManager(new Configuration());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, ERR_MSG);

            throw new IllegalStateException(ERR_MSG);
        }
    }

    @Override
    public String upload(final FileUpload file, final String fileName) throws Exception {
        try {
            final String contentType = file.getHeader().getContentType();
            uploadManager.put(file.getFileInputStream(), fileName, uploadToken, null, contentType);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Uploads file to Qiniu OSS failed", e);

            throw new Exception(ERR_MSG);
        }

        return qiniu.optString(Option.ID_C_QINIU_DOMAIN) + "/" + fileName;
    }
}
