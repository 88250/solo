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
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.OptionRepository;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Upgrade script from v3.3.0 to v3.4.0.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Mar 19, 2019
 * @since 3.4.0
 */
public final class V330_340 {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(V330_340.class);

    /**
     * Performs upgrade from v3.3.0 to v3.4.0.
     *
     * @throws Exception upgrade fails
     */
    public static void perform() throws Exception {
        final String fromVer = "3.3.0";
        final String toVer = "3.4.0";

        LOGGER.log(Level.INFO, "Upgrading from version [" + fromVer + "] to version [" + toVer + "]....");

        final BeanManager beanManager = BeanManager.getInstance();
        final OptionRepository optionRepository = beanManager.getReference(OptionRepository.class);

        try {
            final String tablePrefix = Latkes.getLocalProperty("jdbc.tablePrefix") + "_";
            final Connection connection = Connections.getConnection();
            final Statement statement = connection.createStatement();
            // 修复升级程序问题 https://github.com/b3log/solo/issues/12717
            final ResultSet resultSet = statement.executeQuery("SELECT count(*) AS C FROM information_schema.COLUMNS WHERE table_name = '" + tablePrefix + "user" + "' AND column_name = 'userPassword'");
            while (resultSet.next()) {
                final int c = resultSet.getInt("C");
                if (0 < c) {
                    final Statement drop = connection.createStatement();
                    drop.executeUpdate("ALTER TABLE `" + tablePrefix + "user` DROP COLUMN `userPassword`");
                    drop.close();
                }
            }
            resultSet.close();
            statement.close();
            connection.commit();
            connection.close();

            final Transaction transaction = optionRepository.beginTransaction();

            JSONObject syncGitHubOpt = optionRepository.get(Option.ID_C_SYNC_GITHUB);
            if (null == syncGitHubOpt) {
                syncGitHubOpt = new JSONObject();
                syncGitHubOpt.put(Keys.OBJECT_ID, Option.ID_C_SYNC_GITHUB);
                syncGitHubOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
                syncGitHubOpt.put(Option.OPTION_VALUE, Option.DefaultPreference.DEFAULT_SYNC_GITHUB);
                optionRepository.add(syncGitHubOpt);
            }

            final JSONObject versionOpt = optionRepository.get(Option.ID_C_VERSION);
            versionOpt.put(Option.OPTION_VALUE, toVer);
            optionRepository.update(Option.ID_C_VERSION, versionOpt);

            transaction.commit();

            LOGGER.log(Level.INFO, "Upgraded from version [" + fromVer + "] to version [" + toVer + "] successfully");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Upgrade failed!", e);

            throw new Exception("Upgrade failed from version [" + fromVer + "] to version [" + toVer + "]");
        }
    }
}
