/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * Solo is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.solo.processor;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.net.MimeTypes;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Common;
import org.b3log.solo.util.Images;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * File fetch upload processor. 第三方图床自动替换为社区图床 https://github.com/88250/solo/issues/114
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Apr 30, 2020
 * @since 4.1.0
 */
@Singleton
public class FetchUploadProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(FetchUploadProcessor.class);

    /**
     * Fetches the remote file and upload it.
     *
     * @param context the specified context
     */
    public void fetchUpload(final RequestContext context) {
        final JSONObject result = Solos.newFail();
        context.renderJSONPretty(result);
        final JSONObject data = new JSONObject();

        final JSONObject requestJSONObject = context.requestJSON();
        final String originalURL = requestJSONObject.optString(Common.URL);
        if (!Strings.isURL(originalURL) || !StringUtils.startsWithIgnoreCase(originalURL, "http")) {
            return;
        }

        if (Images.uploaded(originalURL)) {
            return;
        }

        final JSONObject upload = Solos.getUploadToken(context);
        if (null == upload) {
            final String msg = "Gets upload token failed";
            LOGGER.log(Level.ERROR, msg);
            result.put(Keys.MSG, msg);
            return;
        }

        String url;
        byte[] bytes;
        String contentType;
        try {
            final HttpRequest req = HttpRequest.get(originalURL).header("User-Agent", Solos.USER_AGENT);
            final HttpResponse res = req.connectionTimeout(3000).timeout(5000).send();
            res.close();
            if (200 != res.statusCode()) {
                result.put(Keys.MSG, "Fetch upload return status code is [" + res.statusCode() + "]");
                return;
            }

            bytes = res.bodyBytes();
            contentType = res.contentType();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Fetch file [url=" + originalURL + "] failed", e);
            result.put(Keys.MSG, "Fetch file [url=" + originalURL + "] failed");
            return;
        }

        final String uploadURL = upload.optString(Common.UPLOAD_URL);
        final String uploadToken = upload.optString(Common.UPLOAD_TOKEN);

        try {
            final String suffix = "." + getSuffix(contentType);
            final Path imgFilePath = Files.createTempFile("solo-fetchupload-", suffix);
            final File file = imgFilePath.toFile();
            FileUtils.writeByteArrayToFile(file, bytes);
            final HttpRequest req = HttpRequest.post(uploadURL).
                    header("User-Agent", Solos.USER_AGENT).header("X-Upload-Token", uploadToken).
                    connectionTimeout(3000).timeout(5000).form(
                    "file[]", file);
            final HttpResponse res = req.send();
            res.close();
            if (200 != res.statusCode()) {
                result.put(Keys.MSG, "Upload file to community OSS return status code is [" + res.statusCode() + "]");
                return;
            }
            res.charset("UTF-8");
            final JSONObject uploadResult = new JSONObject(res.bodyText());
            final JSONObject succMap = uploadResult.optJSONObject("data").optJSONObject("succMap");
            final String key = succMap.keys().next();
            url = succMap.optString(key);
        } catch (final Exception e) {
            final String msg = "Upload file to community OSS [url=" + originalURL + "] failed";
            LOGGER.log(Level.ERROR, msg, e);
            result.put(Keys.MSG, msg);
            return;
        }


        data.put(Common.URL, url);
        data.put("originalURL", originalURL);

        result.put(Common.DATA, data);
        result.put(Keys.CODE, 0);
        result.put(Keys.MSG, "");
    }

    /**
     * Gets suffix (for example jpg) with the specified content type.
     *
     * @param contentType the specified content type
     * @return suffix
     */
    public static String getSuffix(final String contentType) {
        String ret;
        final String[] exts = MimeTypes.findExtensionsByMimeTypes(contentType, false);
        if (null != exts && 0 < exts.length) {
            ret = exts[0];
        } else {
            ret = StringUtils.substringAfter(contentType, "/");
            ret = StringUtils.substringBefore(ret, ";");
        }

        return ret;
    }
}
