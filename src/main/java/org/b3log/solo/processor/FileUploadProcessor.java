/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2018, b3log.org & hacpai.com
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
package org.b3log.solo.processor;

import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import jodd.io.FileUtil;
import jodd.io.upload.FileUpload;
import jodd.io.upload.MultipartStreamParser;
import jodd.io.upload.impl.MemoryFileUploadFactory;
import jodd.net.MimeTypes;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.util.URLs;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Option;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * File upload processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.2, Aug 2, 2018
 * @since 2.8.0
 */
@RequestProcessor
public class FileUploadProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(FileUploadProcessor.class);

    /**
     * Qiniu enabled.
     */
    private static final Boolean QN_ENABLED = StringUtils.isBlank(Solos.UPLOAD_DIR_PATH);

    static {
        if (!QN_ENABLED) {
            final File file = new File(Solos.UPLOAD_DIR_PATH);
            if (!FileUtil.isExistingFolder(file)) {
                try {
                    FileUtil.mkdirs(Solos.UPLOAD_DIR_PATH);
                } catch (IOException ex) {
                    LOGGER.log(Level.ERROR, "Init upload dir error", ex);

                    System.exit(-1);
                }
            }

            LOGGER.info("Uses dir [" + file.getAbsolutePath() + "] for saving files uploaded");
        }
    }

    /**
     * Gets file by the specified URL.
     *
     * @param req  the specified request
     * @param resp the specified response
     * @throws Exception exception
     */
    @RequestProcessing(value = "/upload/*", method = HTTPRequestMethod.GET)
    public void getFile(final HttpServletRequest req, final HttpServletResponse resp) throws Exception {
        if (QN_ENABLED) {
            return;
        }

        final String uri = req.getRequestURI();
        String key = StringUtils.substringAfter(uri, "/upload/");
        key = StringUtils.substringBeforeLast(key, "?"); // Erase Qiniu template
        key = StringUtils.substringBeforeLast(key, "?"); // Erase Qiniu template

        String path = Solos.UPLOAD_DIR_PATH + key;
        path = URLs.decode(path);

        if (!FileUtil.isExistingFile(new File(path))) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        final byte[] data = IOUtils.toByteArray(new FileInputStream(path));

        final String ifNoneMatch = req.getHeader("If-None-Match");
        final String etag = "\"" + DigestUtils.md5Hex(new String(data)) + "\"";

        resp.addHeader("Cache-Control", "public, max-age=31536000");
        resp.addHeader("ETag", etag);
        resp.setHeader("Server", "Latke Static Server (v" + SoloServletListener.VERSION + ")");
        final String ext = StringUtils.substringAfterLast(path, ".");
        final String mimeType = MimeTypes.getMimeType(ext);
        resp.addHeader("Content-Type", mimeType);

        if (etag.equals(ifNoneMatch)) {
            resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);

            return;
        }

        try (final OutputStream output = resp.getOutputStream()) {
            IOUtils.write(data, output);
            output.flush();
        }
    }

    /**
     * Uploads file.
     *
     * @param req the specified reuqest
     * @throws Exception exception
     */
    @RequestProcessing(value = "/upload", method = HTTPRequestMethod.POST)
    public void uploadFile(final HTTPRequestContext context, final HttpServletRequest req) throws Exception {
        context.renderJSON();

        final int maxSize = 1024 * 1024 * 100;
        final MultipartStreamParser parser = new MultipartStreamParser(new MemoryFileUploadFactory().setMaxFileSize(maxSize));
        parser.parseRequestStream(req.getInputStream(), "UTF-8");
        final List<String> errFiles = new ArrayList();
        final Map<String, String> succMap = new LinkedHashMap<>();
        final FileUpload[] files = parser.getFiles("file[]");
        final String[] names = parser.getParameterValues("name[]");
        String fileName;

        Auth auth;
        UploadManager uploadManager = null;
        String uploadToken = null;
        JSONObject qiniu = null;
        final String date = DateFormatUtils.format(System.currentTimeMillis(), "yyyy/MM");
        if (QN_ENABLED) {
            try {
                final BeanManager beanManager = BeanManager.getInstance();
                final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
                qiniu = optionQueryService.getOptions(Option.CATEGORY_C_QINIU);
                if (null == qiniu) {
                    final String msg = "Qiniu settings failed, please visit https://hacpai.com/article/1442418791213 for more details";
                    LOGGER.log(Level.ERROR, msg);
                    context.renderMsg(msg);

                    return;
                }

                auth = Auth.create(qiniu.optString(Option.ID_C_QINIU_ACCESS_KEY), qiniu.optString(Option.ID_C_QINIU_SECRET_KEY));
                uploadToken = auth.uploadToken(qiniu.optString(Option.ID_C_QINIU_BUCKET), null, 3600 * 6, null);
                uploadManager = new UploadManager(new Configuration());
            } catch (final Exception e) {
                final String msg = "Qiniu settings failed, please visit https://hacpai.com/article/1442418791213 for more details";
                LOGGER.log(Level.ERROR, msg);
                context.renderMsg(msg);

                return;
            }
        }

        for (int i = 0; i < files.length; i++) {
            final FileUpload file = files[i];
            String originalName = fileName = file.getHeader().getFileName();
            originalName = originalName.replaceAll("\\W", "");
            try {
                String suffix = StringUtils.substringAfterLast(fileName, ".");
                final String contentType = file.getHeader().getContentType();
                if (StringUtils.isBlank(suffix)) {
                    String[] exts = MimeTypes.findExtensionsByMimeTypes(contentType, false);
                    if (null != exts && 0 < exts.length) {
                        suffix = exts[0];
                    } else {
                        suffix = StringUtils.substringAfter(contentType, "/");
                    }
                }

                final String name = StringUtils.substringBeforeLast(fileName, ".");
                final String processName = name.replaceAll("\\W", "");
                final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                fileName = uuid + '_' + processName + "." + suffix;

                if (QN_ENABLED) {
                    fileName = "file/" + date + "/" + fileName;
                    if (!ArrayUtils.isEmpty(names)) {
                        fileName = names[i];
                    }
                    uploadManager.put(file.getFileInputStream(), fileName, uploadToken, null, contentType);
                    succMap.put(originalName, qiniu.optString(Option.ID_C_QINIU_DOMAIN) + "/" + fileName);
                } else {
                    try (final OutputStream output = new FileOutputStream(Solos.UPLOAD_DIR_PATH + fileName);
                         final InputStream input = file.getFileInputStream()) {
                        IOUtils.copy(input, output);
                    }
                    succMap.put(originalName, Latkes.getServePath() + "/upload/" + fileName);
                }
            } catch (final Exception e) {
                LOGGER.log(Level.WARN, "Uploads file failed", e);

                errFiles.add(originalName);
            }
        }

        final JSONObject data = new JSONObject();
        data.put("errFiles", errFiles);
        data.put("succMap", succMap);
        context.renderJSONValue("data", data).renderTrueResult();
    }
}
