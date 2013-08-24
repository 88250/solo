/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.solo.processor;


import javax.inject.Inject;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.StatisticQueryService;
import org.b3log.solo.service.TagQueryService;
import org.json.JSONObject;


/**
 * Blog processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Jan 29, 2013
 * @since 0.4.6
 */
@RequestProcessor
public class BlogProcessor {

    /**
     * Article query service.
     */
    @Inject
    private ArticleQueryService articleQueryService;

    /**
     * Tag query service.
     */
    @Inject
    private TagQueryService tagQueryService;

    /**
     * Statistic query service.
     */
    @Inject
    private StatisticQueryService statisticQueryService;

    /**
     * Gets blog information.
     * 
     * <ul>
     *   <li>Time of the recent updated article</li>
     *   <li>Article count</li>
     *   <li>Comment count</li>
     *   <li>Tag count</li>
     *   <li>Serve path</li>
     *   <li>Static serve path</li>
     *   <li>Solo version</li>
     *   <li>Runtime environment (GAE/LOCAL)</li>
     *   <li>Locale</li>
     * </ul>
     * 
     * @param context the specified context
     * @throws Exception exception 
     */
    @RequestProcessing(value = "/blog/info", method = HTTPRequestMethod.GET)
    public void getRecentArticleTime(final HTTPRequestContext context) throws Exception {
        final JSONRenderer renderer = new JSONRenderer();

        context.setRenderer(renderer);

        final JSONObject jsonObject = new JSONObject();

        renderer.setJSONObject(jsonObject);

        jsonObject.put("recentArticleTime", articleQueryService.getRecentArticleTime());
        final JSONObject statistic = statisticQueryService.getStatistic();

        jsonObject.put("articleCount", statistic.getLong(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT));
        jsonObject.put("commentCount", statistic.getLong(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT));
        jsonObject.put("tagCount", tagQueryService.getTagCount());
        jsonObject.put("servePath", Latkes.getServePath());
        jsonObject.put("staticServePath", Latkes.getStaticServePath());
        jsonObject.put("version", SoloServletListener.VERSION);
        jsonObject.put("locale", Latkes.getLocale());
        jsonObject.put("runtimeMode", Latkes.getRuntimeMode());
        final RuntimeEnv runtimeEnv = Latkes.getRuntimeEnv();

        jsonObject.put("runtimeEnv", runtimeEnv);
        if (RuntimeEnv.LOCAL == runtimeEnv) {
            jsonObject.put("runtimeDatabase", Latkes.getRuntimeDatabase());
        }
    }
}
