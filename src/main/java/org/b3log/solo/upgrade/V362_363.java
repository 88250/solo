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
package org.b3log.solo.upgrade;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.OptionRepository;
import org.b3log.solo.util.Images;
import org.json.JSONObject;

import java.util.List;

/**
 * Upgrade script from v3.6.2 to v3.6.3.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 13, 2019
 * @since 3.6.3
 */
public final class V362_363 {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(V362_363.class);

    /**
     * Performs upgrade from v3.6.2 to v3.6.3.
     *
     * @throws Exception upgrade fails
     */
    public static void perform() throws Exception {
        final String fromVer = "3.6.2";
        final String toVer = "3.6.3";

        LOGGER.log(Level.INFO, "Upgrading from version [" + fromVer + "] to version [" + toVer + "]....");

        final BeanManager beanManager = BeanManager.getInstance();
        final OptionRepository optionRepository = beanManager.getReference(OptionRepository.class);
        final ArticleRepository articleRepository = beanManager.getReference(ArticleRepository.class);

        try {
            final Transaction transaction = optionRepository.beginTransaction();

            final JSONObject versionOpt = optionRepository.get(Option.ID_C_VERSION);
            versionOpt.put(Option.OPTION_VALUE, toVer);
            optionRepository.update(Option.ID_C_VERSION, versionOpt);

            // 提升文章首图精度
            final List<JSONObject> articles = articleRepository.getList(new Query());
            for (final JSONObject article : articles) {
                String imgURL = article.optString(Article.ARTICLE_IMG1_URL);
                if (StringUtils.isBlank(imgURL)) {
                    imgURL = Images.imageSize(Images.randImage(), Article.ARTICLE_THUMB_IMG_WIDTH, Article.ARTICLE_THUMB_IMG_HEIGHT);
                } else {
                    imgURL = StringUtils.replace(imgURL, "/w/768", "/w/" + Article.ARTICLE_THUMB_IMG_WIDTH);
                    imgURL = StringUtils.replace(imgURL, "/h/432", "/h/" + Article.ARTICLE_THUMB_IMG_HEIGHT);
                }
                article.put(Article.ARTICLE_IMG1_URL, imgURL);
                articleRepository.update(article.optString(Keys.OBJECT_ID), article);
            }

            transaction.commit();

            LOGGER.log(Level.INFO, "Upgraded from version [" + fromVer + "] to version [" + toVer + "] successfully");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Upgrade failed!", e);
            throw new Exception("Upgrade failed from version [" + fromVer + "] to version [" + toVer + "]");
        }
    }
}
