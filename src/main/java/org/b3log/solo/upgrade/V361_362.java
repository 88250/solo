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
package org.b3log.solo.upgrade;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.OptionRepository;
import org.b3log.solo.util.Emotions;
import org.json.JSONObject;

import java.util.List;

/**
 * Upgrade script from v3.6.1 to v3.6.2.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jun 1, 2019
 * @since 3.6.2
 */
public final class V361_362 {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(V361_362.class);

    /**
     * Performs upgrade from v3.6.1 to v3.6.2.
     *
     * @throws Exception upgrade fails
     */
    public static void perform() throws Exception {
        final String fromVer = "3.6.1";
        final String toVer = "3.6.2";

        LOGGER.log(Level.INFO, "Upgrading from version [" + fromVer + "] to version [" + toVer + "]....");

        final BeanManager beanManager = BeanManager.getInstance();
        final OptionRepository optionRepository = beanManager.getReference(OptionRepository.class);
        final CommentRepository commentRepository = beanManager.getReference(CommentRepository.class);

        try {
            final Transaction transaction = optionRepository.beginTransaction();

            final JSONObject versionOpt = optionRepository.get(Option.ID_C_VERSION);
            versionOpt.put(Option.OPTION_VALUE, toVer);
            optionRepository.update(Option.ID_C_VERSION, versionOpt);

            // 迁移历史表情图片 https://github.com/b3log/solo/issues/12787
            final List<JSONObject> comments = commentRepository.getList(new Query());
            for (final JSONObject comment : comments) {
                final String oldContent = comment.optString(Comment.COMMENT_CONTENT);
                String commentContent = oldContent;
                commentContent = Emotions.convert(commentContent);
                commentContent = convertEm00(commentContent);
                if (!StringUtils.equalsIgnoreCase(oldContent, commentContent)) {
                    comment.put(Comment.COMMENT_CONTENT, commentContent);
                    final String commentId = comment.optString(Keys.OBJECT_ID);
                    comment.put(Comment.COMMENT_CONTENT, commentContent);
                    commentRepository.update(commentId, comment);
                    LOGGER.log(Level.INFO, "Migrated comment [id=" + commentId + "]'s content emoji");
                }
            }

            transaction.commit();

            LOGGER.log(Level.INFO, "Upgraded from version [" + fromVer + "] to version [" + toVer + "] successfully");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Upgrade failed!", e);

            throw new Exception("Upgrade failed from version [" + fromVer + "] to version [" + toVer + "]");
        }
    }

    private static String convertEm00(final String content) {
        String ret = StringUtils.replace(content, "[em00]", "\uD83D\uDE04");
        ret = StringUtils.replace(ret, "[em01]", "\uD83D\uDE02");
        ret = StringUtils.replace(ret, "[em02]", "\uD83D\uDE1C");
        ret = StringUtils.replace(ret, "[em03]", "\uD83D\uDE2B");
        ret = StringUtils.replace(ret, "[em04]", "\uD83D\uDE2D");
        ret = StringUtils.replace(ret, "[em05]", "\uD83D\uDE30");
        ret = StringUtils.replace(ret, "[em06]", "\uD83D\uDE21");
        ret = StringUtils.replace(ret, "[em07]", "\uD83D\uDE24");
        ret = StringUtils.replace(ret, "[em08]", "\uD83D\uDC40");
        ret = StringUtils.replace(ret, "[em09]", "\uD83D\uDE31");
        ret = StringUtils.replace(ret, "[em10]", "\uD83D\uDE0E");
        ret = StringUtils.replace(ret, "[em11]", "\uD83D\uDE0B");
        ret = StringUtils.replace(ret, "[em12]", "❤️");
        ret = StringUtils.replace(ret, "[em13]", "\uD83D\uDC94");
        ret = StringUtils.replace(ret, "[em14]", "\uD83D\uDC7F");

        return ret;
    }
}
