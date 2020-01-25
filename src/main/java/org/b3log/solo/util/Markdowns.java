/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
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
package org.b3log.solo.util;

import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Callstacks;
import org.b3log.latke.util.Stopwatchs;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.NodeVisitor;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

/**
 * <a href="http://en.wikipedia.org/wiki/Markdown">Markdown</a> utilities.
 * <p>
 * Uses the <a href="https://github.com/88250/markdown-http">markdown-http</a> as the processor, if not found this command, try
 * built-in <a href="https://github.com/vsch/flexmark-java">flexmark</a> instead.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.3.1.15, Jan 25, 2020
 * @since 0.4.5
 */
public final class Markdowns {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Markdowns.class);

    /**
     * Markdown cache.
     */
    private static final Map<String, JSONObject> MD_CACHE = new ConcurrentHashMap<>();

    /**
     * Markdown to HTML timeout.
     */
    private static final int MD_TIMEOUT = 10000;

    /**
     * Built-in MD engine options.
     */
    private static final DataHolder OPTIONS = new MutableDataSet().
            set(com.vladsch.flexmark.parser.Parser.EXTENSIONS, Arrays.asList(
                    TablesExtension.create(),
                    TaskListExtension.create(),
                    StrikethroughExtension.create(),
                    AutolinkExtension.create())).
            set(HtmlRenderer.SOFT_BREAK, "<br />\n");

    /**
     * Built-in MD engine parser.
     */
    private static final com.vladsch.flexmark.parser.Parser PARSER =
            com.vladsch.flexmark.parser.Parser.builder(OPTIONS).build();

    /**
     * Built-in MD engine HTML renderer.
     */
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();

    /**
     * Lute engine serve path. https://github.com/88250/lute-http
     */
    public static String LUTE_ENGINE_URL = "http://localhost:8249";

    /**
     * Whether Lute is available.
     */
    public static boolean LUTE_AVAILABLE;

    public static boolean SHOW_CODE_BLOCK_LN = false;
    public static boolean FOOTNOTES = false;
    public static boolean SHOW_TOC = false;
    public static boolean AUTO_SPACE = false;
    public static boolean FIX_TERM_TYPO = false;
    public static boolean CHINESE_PUNCT = false;
    public static boolean IMADAOM = false;

    /**
     * Clears cache.
     */
    public static void clearCache() {
        MD_CACHE.clear();
    }

    /**
     * Cleans the specified HTML.
     *
     * @param html the specified HTML
     * @return html
     */
    public static String clean(final String html) {
        final Whitelist whitelist = Whitelist.relaxed();
        // 允许代码块语言高亮信息
        whitelist.addAttributes("pre", "class").
                addAttributes("div", "class").
                addAttributes("span", "class").
                addAttributes("code", "class");
        return Jsoup.clean(html, whitelist);
    }

    /**
     * Converts the specified markdown text to HTML.
     *
     * @param markdownText the specified markdown text
     * @return converted HTML, returns an empty string "" if the specified markdown text is "" or {@code null}, returns
     * 'markdownErrorLabel' if exception
     */
    public static String toHTML(final String markdownText) {
        if (StringUtils.isBlank(markdownText)) {
            return "";
        }

        final String cachedHTML = getHTML(markdownText);
        if (null != cachedHTML) {
            return cachedHTML;
        }

        final LangPropsService langPropsService = BeanManager.getInstance().getReference(LangPropsService.class);

        final ExecutorService pool = Executors.newSingleThreadExecutor();
        final long[] threadId = new long[1];

        final Callable<String> call = () -> {
            threadId[0] = Thread.currentThread().getId();

            String html = null;
            if (LUTE_AVAILABLE) {
                try {
                    html = toHtmlByLute(markdownText);
                } catch (final Exception e) {
                    LOGGER.log(Level.WARN, "Failed to use Lute [" + LUTE_ENGINE_URL + "] for markdown [md=" + StringUtils.substring(markdownText, 0, 256) + "]: " + e.getMessage());
                }
            }

            if (StringUtils.isBlank(html)) {
                html = toHtmlByFlexmark(markdownText);
            }

            final Document doc = Jsoup.parse(html);
            doc.select("a").forEach(a -> {
                final String src = a.attr("href");
                if (!StringUtils.startsWithIgnoreCase(src, Latkes.getServePath()) && !StringUtils.startsWithIgnoreCase(src, "#")) {
                    a.attr("target", "_blank");
                }
                a.removeAttr("id");
            });


            final List<Node> toRemove = new ArrayList<>();
            doc.traverse(new NodeVisitor() {
                @Override
                public void head(final org.jsoup.nodes.Node node, int depth) {
                    if (node instanceof org.jsoup.nodes.TextNode) {
                        final org.jsoup.nodes.TextNode textNode = (org.jsoup.nodes.TextNode) node;
                        final org.jsoup.nodes.Node parent = textNode.parent();

                        if (parent instanceof Element) {
                            final Element parentElem = (Element) parent;
                            if (parentElem.tagName().equals("code") || parentElem.tagName().equals("pre")) {
                                return;
                            }

                            if (parentElem.tagName().equals("span") && StringUtils.startsWithIgnoreCase(parentElem.attr("class"), "hljs")) {
                                return;
                            }

                            String text = textNode.getWholeText();
                            text = Emotions.convert(text);
                            if (text.contains("@<a href=") || text.contains("<img")) {
                                final List<org.jsoup.nodes.Node> nodes = Parser.parseFragment(text, parentElem, "");
                                final int index = textNode.siblingIndex();
                                parentElem.insertChildren(index, nodes);
                                toRemove.add(node);
                            } else {
                                textNode.text(text);
                            }
                        }
                    }
                }

                @Override
                public void tail(org.jsoup.nodes.Node node, int depth) {
                }
            });

            toRemove.forEach(Node::remove);

            doc.outputSettings().prettyPrint(false);

            String ret = doc.select("body").html();
            ret = StringUtils.trim(ret);
            ret = Images.qiniuImgProcessing(ret);

            // cache it
            putHTML(markdownText, ret);

            return ret;
        };

        Stopwatchs.start("Md to HTML");
        try {
            final Future<String> future = pool.submit(call);

            return future.get(MD_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (final TimeoutException e) {
            LOGGER.log(Level.ERROR, "Markdown timeout [md=" + markdownText + "]");
            Callstacks.printCallstack(Level.ERROR, new String[]{"org.b3log"}, null);

            final Set<Thread> threads = Thread.getAllStackTraces().keySet();
            for (final Thread thread : threads) {
                if (thread.getId() == threadId[0]) {
                    thread.stop();

                    break;
                }
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Markdown failed [md=" + markdownText + "]", e);
        } finally {
            pool.shutdownNow();

            Stopwatchs.end();
        }

        return langPropsService.get("contentRenderFailedLabel");
    }

    private static String toHtmlByLute(final String markdownText) throws Exception {
        final URL url = new URL(LUTE_ENGINE_URL);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("X-CodeSyntaxHighlightLineNum", String.valueOf(Markdowns.SHOW_CODE_BLOCK_LN));
        conn.setRequestProperty("X-Footnotes", String.valueOf(Markdowns.FOOTNOTES));
        conn.setRequestProperty("X-ToC", String.valueOf(Markdowns.SHOW_TOC));
        conn.setRequestProperty("X-AutoSpace", String.valueOf(Markdowns.AUTO_SPACE));
        conn.setRequestProperty("X-FixTermTypo", String.valueOf(Markdowns.FIX_TERM_TYPO));
        conn.setRequestProperty("X-ChinesePunct", String.valueOf(Markdowns.CHINESE_PUNCT));
        conn.setRequestProperty("X-IMADAOM", String.valueOf(Markdowns.IMADAOM));
        conn.setConnectTimeout(100);
        conn.setReadTimeout(3000);
        conn.setDoOutput(true);

        try (final OutputStream outputStream = conn.getOutputStream()) {
            IOUtils.write(markdownText, outputStream, "UTF-8");
        }

        String ret;
        try (final InputStream inputStream = conn.getInputStream()) {
            ret = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }

        conn.disconnect();

        return ret;
    }

    private static String toHtmlByFlexmark(final String markdownText) {
        com.vladsch.flexmark.util.ast.Node document = PARSER.parse(markdownText);

        return RENDERER.render(document);
    }

    /**
     * Gets HTML for the specified markdown text.
     *
     * @param markdownText the specified markdown text
     * @return HTML
     */
    private static String getHTML(final String markdownText) {
        final String hash = DigestUtils.md5Hex(markdownText);
        final JSONObject value = MD_CACHE.get(hash);
        if (null == value) {
            return null;
        }

        return value.optString("data");
    }

    /**
     * Puts the specified HTML into cache.
     *
     * @param markdownText the specified markdown text
     * @param html         the specified HTML
     */
    private static void putHTML(final String markdownText, final String html) {
        final String hash = DigestUtils.md5Hex(markdownText);
        final JSONObject value = new JSONObject();
        value.put("data", html);
        MD_CACHE.put(hash, value);
    }

    /**
     * Private constructor.
     */
    private Markdowns() {
    }
}
