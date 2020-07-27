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

import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.function.Handler;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.util.Requests;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.service.StatisticMgmtService;
import org.b3log.solo.util.Skins;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

/**
 * Skin handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Apr 30, 2020
 * @since 3.6.7
 */
public class SkinHandler implements Handler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(SkinHandler.class);

    @Override
    public void handle(final RequestContext context) {
        final Request request = context.getRequest();
        Requests.log(request, Level.DEBUG, LOGGER);

        fillBotAttrs(request);
        if (!Solos.isBot(request)) {
            final StatisticMgmtService statisticMgmtService = BeanManager.getInstance().getReference(StatisticMgmtService.class);
            statisticMgmtService.onlineVisitorCount(request);
        }

        resolveSkinDir(request);

        context.handle();
    }

    /**
     * Resolve skin (template) for the specified HTTP request.
     * 前台皮肤切换 https://github.com/b3log/solo/issues/12060
     * 调整前台动态皮肤预览逻辑 https://github.com/88250/solo/issues/116
     *
     * @param request the specified HTTP request
     */
    private void resolveSkinDir(final Request request) {
        String skin = Skins.getQuerySkin(request);
        if (StringUtils.isBlank(skin)) {
            final BeanManager beanManager = BeanManager.getInstance();
            final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
            final JSONObject skinOpt = optionQueryService.getSkin();
            if (Solos.isMobile(request)) {
                if (null != skinOpt) {
                    skin = skinOpt.optString(org.b3log.solo.model.Option.ID_C_MOBILE_SKIN_DIR_NAME);
                } else {
                    skin = org.b3log.solo.model.Option.DefaultPreference.DEFAULT_MOBILE_SKIN_DIR_NAME;
                }
            } else {
                if (null != skinOpt) {
                    skin = skinOpt.optString(org.b3log.solo.model.Option.ID_C_SKIN_DIR_NAME);
                } else {
                    skin = org.b3log.solo.model.Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME;
                }
            }
        }
        request.setAttribute(Keys.TEMPLATE_DIR_NAME, skin);
    }

    private static void fillBotAttrs(final Request request) {
        final String userAgentStr = request.getHeader("User-Agent");
        final UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
        BrowserType browserType = userAgent.getBrowser().getBrowserType();

        if (StringUtils.containsIgnoreCase(userAgentStr, "mobile")
                || StringUtils.containsIgnoreCase(userAgentStr, "MQQBrowser")
                || StringUtils.containsIgnoreCase(userAgentStr, "iphone")
                || StringUtils.containsIgnoreCase(userAgentStr, "MicroMessenger")
                || StringUtils.containsIgnoreCase(userAgentStr, "CFNetwork")
                || StringUtils.containsIgnoreCase(userAgentStr, "Android")) {
            browserType = BrowserType.MOBILE_BROWSER;
        } else if (StringUtils.containsIgnoreCase(userAgentStr, "Iframely")
                || StringUtils.containsIgnoreCase(userAgentStr, "Google")
                || StringUtils.containsIgnoreCase(userAgentStr, "BUbiNG")
                || StringUtils.containsIgnoreCase(userAgentStr, "ltx71")
                || StringUtils.containsIgnoreCase(userAgentStr, "py")) {
            browserType = BrowserType.ROBOT;
        }

        request.setAttribute(Keys.HttpRequest.IS_SEARCH_ENGINE_BOT, BrowserType.ROBOT == browserType);
        request.setAttribute(Keys.HttpRequest.IS_MOBILE_BOT, BrowserType.MOBILE_BROWSER == browserType);
    }
}
