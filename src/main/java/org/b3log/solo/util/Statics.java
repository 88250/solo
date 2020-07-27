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
package org.b3log.solo.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Article;
import org.b3log.solo.processor.SkinRenderer;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Static utilities. 页面静态化 https://github.com/88250/solo/issues/107
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.2, Jul 4, 2020
 * @since 4.1.0
 */
public final class Statics {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Statics.class);

    /**
     * Generated page expire time.
     */
    private static final long EXPIRED = TimeUnit.HOURS.toMillis(6);

    private static File DIR;

    static {
        final String userHome = System.getProperty("user.home");
        final Path staticCache = Paths.get(userHome, ".solo", "static-cache");
        final String staticDir = staticCache.toString();
        if (StringUtils.isNotBlank(staticDir)) {
            try {
                FileUtils.forceMkdir(new File(staticDir));
                DIR = new File(staticDir);
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Creates static cache dir failed", e);
            }
        }
    }

    /**
     * Gets static HTML.
     *
     * @param context the specified context
     * @return HTML, returns {@code null} if not found
     */
    public static String get(final RequestContext context) {
        if (Solos.GEN_STATIC_SITE || Latkes.RuntimeMode.DEVELOPMENT == Latkes.getRuntimeMode()) {
            // 生成静态站点时和开发环境时不走缓存
            return null;
        }

        if (Solos.isLoggedIn(context)) {
            // 登录用户不走缓存
            return null;
        }

        final String remoteAddr = Requests.getRemoteAddr(context.getRequest());
        if (Strings.isIPv4(remoteAddr)) {
            // 直接用 IP 访问不走缓存
            return null;
        }

        final String key = key(context);
        if (null == key) {
            return null;
        }

        final Path path = Paths.get(DIR.getAbsolutePath(), key);
        final File file = path.toFile();
        if (!file.exists()) {
            return null;
        }

        final long now = System.currentTimeMillis();
        final long lastModified = file.lastModified();
        if (EXPIRED <= now - lastModified) {
            return null;
        }

        return readFile(file);
    }

    /**
     * Puts static HTML.
     *
     * @param context the specified context
     */
    public static void put(final RequestContext context) {
        if (Solos.GEN_STATIC_SITE) {
            // 生成静态站点时不走缓存
            return;
        }

        if (Solos.isLoggedIn(context)) {
            // 登录用户生成的内容不写入缓存
            return;
        }

        final String key = key(context);
        if (null == key) {
            return;
        }

        final Path path = Paths.get(DIR.getAbsolutePath(), key);
        final File file = path.toFile();
        if (file.exists()) {
            final long now = System.currentTimeMillis();
            final long lastModified = file.lastModified();
            if (EXPIRED > now - lastModified) {
                return;
            }
        }

        try {
            final byte[] html = context.getResponse().getBytes();
            byte[] commpressed = gzip(html);
            if (null == commpressed) {
                commpressed = html;
            }
            FileUtils.writeByteArrayToFile(path.toFile(), commpressed);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Writes static file failed", e);
        }
    }

    /**
     * Clears all files under ~/.solo/static-cache.
     */
    public static void clear() {
        try {
            FileUtils.cleanDirectory(DIR);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Clears static cached files failed", e);
        }
    }

    private static String readFile(final File file) {
        try {
            final byte[] compressed = FileUtils.readFileToByteArray(file);
            byte[] html = unGzip(compressed);
            if (null == html) {
                html = compressed;
            }
            final String content = new String(html, StandardCharsets.UTF_8);
            final List<String> lines = Arrays.asList(content.split("\n"));
            final long elapsed = ThreadLocalRandom.current().nextLong(64, 128);
            final String dateString = DateFormatUtils.format(System.currentTimeMillis(), "yyyy/MM/dd HH:mm:ss");
            final String lastLine = String.format(SkinRenderer.LATKE_INFO, elapsed, dateString);
            lines.set(lines.size() - 1, lastLine);

            return StringUtils.join(lines, "\n");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Reads static file failed", e);
        }

        return null;
    }

    /**
     * Calculates key of the specified context.
     *
     * @param context the specified context
     * @return key, returns {@code null} if can not be static
     */
    private static String key(final RequestContext context) {
        if (null == DIR) {
            return null;
        }

        if (!StringUtils.equalsIgnoreCase("get", context.method())) {
            return null;
        }

        String ret, requestURL;

        // 判断文章自定义链接
        final JSONObject article = (JSONObject) context.attr(Article.ARTICLE);
        if (null != article) {
            requestURL = article.optString(Article.ARTICLE_PERMALINK);
        } else {
            requestURL = context.requestURI();
        }
        String requestQueryStr = context.requestQueryStr();
        if (StringUtils.isNotBlank(requestQueryStr)) {
            requestURL += "?" + requestQueryStr;
        }

        ret = StringUtils.replace(requestURL, "/", "_");
        ret = StringUtils.replace(ret, "?", "_");
        if (Solos.isMobile(context.getRequest())) {
            ret = "m" + ret;
        }
        if (SkinRenderer.isPJAX(context)) {
            ret += "pjax";
        }
        ret = DigestUtils.md5Hex(ret);
        return ret;
    }

    private static byte[] gzip(final byte[] data) {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream();
             final GZIPOutputStream zout = new GZIPOutputStream(out)) {
            zout.write(data);
            zout.close();
            return out.toByteArray();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gzip failed", e);

            return null;
        }
    }

    private static byte[] unGzip(final byte[] compressed) {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream();
             final ByteArrayInputStream in = new ByteArrayInputStream(compressed);
             final GZIPInputStream zin = new GZIPInputStream(in)) {

            final byte[] buffer = new byte[1024];
            int offset;
            while ((offset = zin.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }

            return out.toByteArray();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "UnGzip failed", e);

            return null;
        }
    }

    private Statics() {
    }
}
