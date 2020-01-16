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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.repository.OptionRepository;
import org.b3log.solo.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Set;

/**
 * Upgrade script from v2.9.9 to v3.0.0.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Feb 28, 2019
 * @since 3.0.0
 */
public final class V299_300 {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(V299_300.class);

    /**
     * Performs upgrade from v2.9.9 to v3.0.0.
     *
     * @throws Exception upgrade fails
     */
    public static void perform() throws Exception {
        LOGGER.log(Level.INFO, "Upgrading from version [2.9.9] to version [3.0.0]....");

        final BeanManager beanManager = BeanManager.getInstance();
        final OptionRepository optionRepository = beanManager.getReference(OptionRepository.class);
        final UserRepository userRepository = beanManager.getReference(UserRepository.class);

        try {
            Connection connection = Connections.getConnection();
            Statement statement = connection.createStatement();

            final String tablePrefix = Latkes.getLocalProperty("jdbc.tablePrefix") + "_";
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "user` ADD COLUMN `userB3Key` VARCHAR(64) DEFAULT '' NOT NULL");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "user` ADD COLUMN `userGitHubId` VARCHAR(32) DEFAULT '' NOT NULL");
            statement.close();
            connection.commit();
            connection.close();

            final Transaction transaction = optionRepository.beginTransaction();
            final JSONObject versionOpt = optionRepository.get(Option.ID_C_VERSION);
            versionOpt.put(Option.OPTION_VALUE, "3.0.0");
            optionRepository.update(Option.ID_C_VERSION, versionOpt);

            final JSONObject oauthGitHubOpt = optionRepository.get("oauthGitHub");
            if (null != oauthGitHubOpt) {
                String value = oauthGitHubOpt.optString(Option.OPTION_VALUE);
                final Set<String> githubs = CollectionUtils.jsonArrayToSet(new JSONArray(value));
                for (final String pair : githubs) {
                    final String githubId = pair.split(":@:")[0];
                    final String userId = pair.split(":@:")[1];
                    final JSONObject user = userRepository.get(userId);
                    user.put(UserExt.USER_GITHUB_ID, githubId);
                    user.put(UserExt.USER_B3_KEY, githubId);
                    userRepository.update(userId, user);
                }
            }
            optionRepository.remove("oauthGitHub");

            final String b3Key = optionRepository.get("keyOfSolo").optString(Option.OPTION_VALUE);
            final JSONObject admin = userRepository.getAdmin();
            admin.put(UserExt.USER_B3_KEY, b3Key);
            userRepository.update(admin.optString(Keys.OBJECT_ID), admin);
            optionRepository.remove("keyOfSolo");

            optionRepository.remove("qiniuAccessKey");
            optionRepository.remove("qiniuBucket");
            optionRepository.remove("qiniuDomain");
            optionRepository.remove("qiniuSecretKey");
            optionRepository.remove("ossServer");
            optionRepository.remove("aliyunAccessKey");
            optionRepository.remove("aliyunSecretKey");
            optionRepository.remove("aliyunDomain");
            optionRepository.remove("aliyunBucket");
            optionRepository.remove("editorType");

            transaction.commit();

            connection = Connections.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "article` DROP COLUMN `articleEditorType`");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "page` DROP COLUMN `pageEditorType`");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "user` DROP COLUMN `userPassword`");
            statement.close();
            connection.commit();
            connection.close();

            LOGGER.log(Level.INFO, "Upgraded from version [2.9.9] to version [3.0.0] successfully");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Upgrade failed!", e);

            throw new Exception("Upgrade failed from version [2.9.9] to version [3.0.0]");
        }
    }
}
