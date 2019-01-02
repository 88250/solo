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
package org.b3log.solo.processor;

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
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.servlet.HttpMethod;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.util.URLs;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.service.oss.CloudStorageService;
import org.b3log.solo.service.oss.OssService;
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
 * @author <a href="https://github.com/hzchendou">hzchendou</a>
 * @version 1.0.2.3, Dec 23, 2018
 * @since 2.8.0
 */
@RequestProcessor
public class FileUploadProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(FileUploadProcessor.class);

    /**
     * Cloud OSS (Aliyun/Qiniu) enabled.
     */
    private static final Boolean OSS_ENABLED = StringUtils.isBlank(Solos.UPLOAD_DIR_PATH);

    static {
        if (!OSS_ENABLED) {
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

    @Inject
    private CloudStorageService cloudStorageService;

    /**
     * Gets file by the specified URL.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/upload/{file}", method = HttpMethod.GET)
    public void getFile(final RequestContext context) {
        if (OSS_ENABLED) {
            return;
        }

        final String uri = context.requestURI();
        String key = StringUtils.substringAfter(uri, "/upload/");
        key = StringUtils.substringBeforeLast(key, "?"); // Erase Qiniu template
        key = StringUtils.substringBeforeLast(key, "?"); // Erase Qiniu template

        String path = Solos.UPLOAD_DIR_PATH + key;
        path = URLs.decode(path);

        if (!FileUtil.isExistingFile(new File(path))) {
            context.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        byte[] data;
        try {
            data = IOUtils.toByteArray(new FileInputStream(path));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Reads input stream failed: " + e.getMessage());
            context.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return;
        }
        final HttpServletRequest req = context.getRequest();
        final String ifNoneMatch = req.getHeader("If-None-Match");
        final String etag = "\"" + DigestUtils.md5Hex(new String(data)) + "\"";

        context.addHeader("Cache-Control", "public, max-age=31536000");
        context.addHeader("ETag", etag);
        context.setHeader("Server", "Latke Static Server (v" + SoloServletListener.VERSION + ")");
        final String ext = StringUtils.substringAfterLast(path, ".");
        final String mimeType = MimeTypes.getMimeType(ext);
        context.addHeader("Content-Type", mimeType);

        if (etag.equals(ifNoneMatch)) {
            context.setStatus(HttpServletResponse.SC_NOT_MODIFIED);

            return;
        }

        final HttpServletResponse response = context.getResponse();
        try (final OutputStream output = response.getOutputStream()) {
            IOUtils.write(data, output);
            output.flush();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Writes output stream failed: " + e.getMessage());
        }
    }

    /**
     * Uploads file.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/upload", method = HttpMethod.POST)
    public void uploadFile(final RequestContext context) {
        context.renderJSON();
        final HttpServletRequest request = context.getRequest();
        if (!Solos.isLoggedIn(context)) {
            context.sendError(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        final JSONObject currentUser = Solos.getCurrentUser(context.getRequest(), context.getResponse());
        if (Role.VISITOR_ROLE.equals(currentUser.optString(User.USER_ROLE))) {
            context.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        final int maxSize = 1024 * 1024 * 100;
        final MultipartStreamParser parser = new MultipartStreamParser(new MemoryFileUploadFactory().setMaxFileSize(maxSize));
        try {
            parser.parseRequestStream(request.getInputStream(), "UTF-8");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Parses request stream failed: " + e.getMessage());
            context.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return;
        }
        final List<String> errFiles = new ArrayList<>();
        final Map<String, String> succMap = new LinkedHashMap<>();
        final FileUpload[] files = parser.getFiles("file[]");
        final String[] names = parser.getParameterValues("name[]");
        String fileName;

        OssService ossService = null;
        final String date = DateFormatUtils.format(System.currentTimeMillis(), "yyyy/MM");
        if (OSS_ENABLED) {
            try {
                ossService = cloudStorageService.createStorage();
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, e.getMessage());
                context.renderMsg(e.getMessage());
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

                if (OSS_ENABLED) {
                    fileName = "file/" + date + "/" + fileName;
                    if (!ArrayUtils.isEmpty(names)) {
                        fileName = names[i];
                    }
                    String fileLink = ossService.upload(file, fileName);
                    succMap.put(originalName, fileLink);
                } else {
                    try (final OutputStream output = new FileOutputStream(Solos.UPLOAD_DIR_PATH + fileName);
                         final InputStream input = file.getFileInputStream()) {
                        IOUtils.copy(input, output);
                    }
                    succMap.put(originalName, Latkes.getServePath() + "/upload/" + fileName);
                }
            } catch (final Exception e) {
                LOGGER.log(Level.WARN, "Uploads file failed: " + e.getMessage());

                errFiles.add(originalName);
            }
        }

        final JSONObject data = new JSONObject();
        data.put("errFiles", errFiles);
        data.put("succMap", succMap);
        context.renderJSONValue("data", data).renderTrueResult();
    }
}
