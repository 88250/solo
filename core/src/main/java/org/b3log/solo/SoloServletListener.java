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
package org.b3log.solo;


import java.util.ResourceBundle;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import org.b3log.latke.Keys;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.plugin.ViewLoadEventHandler;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.servlet.AbstractServletListener;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.latke.util.Strings;
import org.b3log.solo.event.comment.ArticleCommentReplyNotifier;
import org.b3log.solo.event.comment.PageCommentReplyNotifier;
import org.b3log.solo.event.ping.AddArticleGoogleBlogSearchPinger;
import org.b3log.solo.event.ping.UpdateArticleGoogleBlogSearchPinger;
import org.b3log.solo.event.plugin.PluginRefresher;
import org.b3log.solo.event.rhythm.ArticleSender;
import org.b3log.solo.event.rhythm.ArticleUpdater;
import org.b3log.solo.event.symphony.CommentSender;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Skin;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.repository.impl.PreferenceRepositoryImpl;
import org.b3log.solo.service.PreferenceMgmtService;
import org.b3log.solo.service.StatisticMgmtService;
import org.b3log.solo.util.Skins;
import org.json.JSONObject;


/**
 * B3log Solo servlet listener.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.2, Oct 27, 2013
 * @since 0.3.1
 */
public final class SoloServletListener extends AbstractServletListener {

    /**
     * B3log Solo version.
     */
    public static final String VERSION = "0.6.5";

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(SoloServletListener.class.getName());

    /**
     * JSONO print indent factor.
     */
    public static final int JSON_PRINT_INDENT_FACTOR = 4;

    /**
     * Enter escape.
     */
    public static final String ENTER_ESC = "_esc_enter_88250_";

    /**
     * B3log Rhythm address.
     */
    public static final String B3LOG_RHYTHM_SERVE_PATH;

    /**
     * B3log Symphony address.
     */
    public static final String B3LOG_SYMPHONY_SERVE_PATH;

    /**
     * Bean manager.
     */
    private LatkeBeanManager beanManager;

    static {
        final ResourceBundle b3log = ResourceBundle.getBundle("b3log");

        B3LOG_RHYTHM_SERVE_PATH = b3log.getString("rhythm.servePath");
        B3LOG_SYMPHONY_SERVE_PATH = b3log.getString("symphony.servePath");
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);

        beanManager = Lifecycle.getBeanManager();

        Stopwatchs.start("Context Initialized");

        // Default to skin "ease", loads from preference later
        Skins.setDirectoryForTemplateLoading("ease");

        final PreferenceRepository preferenceRepository = beanManager.getReference(PreferenceRepositoryImpl.class);

        final Transaction transaction = preferenceRepository.beginTransaction();

        try {
            loadPreference();

            if (transaction.isActive()) {
                transaction.commit();
            }
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }

        registerEventProcessor();

        final PluginManager pluginManager = beanManager.getReference(PluginManager.class);

        pluginManager.load();

        LOGGER.info("Initialized the context");

        Stopwatchs.end();
        LOGGER.log(Level.DEBUG, "Stopwatch: {0}{1}", Strings.LINE_SEPARATOR, Stopwatchs.getTimingStat());
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent);

        LOGGER.info("Destroyed the context");
    }

    @Override
    public void sessionCreated(final HttpSessionEvent httpSessionEvent) {}

    // Note: This method will never invoked on GAE production environment
    @Override
    public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {}

    @Override
    public void requestInitialized(final ServletRequestEvent servletRequestEvent) {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequestEvent.getServletRequest();

        Requests.log(httpServletRequest, Level.DEBUG, LOGGER);

        final String requestURI = httpServletRequest.getRequestURI();

        Stopwatchs.start("Request Initialized[requestURI=" + requestURI + "]");

        if (Requests.searchEngineBotRequest(httpServletRequest)) {
            LOGGER.log(Level.DEBUG, "Request made from a search engine[User-Agent={0}]", httpServletRequest.getHeader("User-Agent"));
            httpServletRequest.setAttribute(Keys.HttpRequest.IS_SEARCH_ENGINE_BOT, true);
        } else {
            // Gets the session of this request
            final HttpSession session = httpServletRequest.getSession();

            LOGGER.log(Level.DEBUG, "Gets a session[id={0}, remoteAddr={1}, User-Agent={2}, isNew={3}]", session.getId(),
                httpServletRequest.getRemoteAddr(), httpServletRequest.getHeader("User-Agent"), session.isNew());
            // Online visitor count
            final StatisticMgmtService statisticMgmtService = beanManager.getReference(StatisticMgmtService.class);

            statisticMgmtService.onlineVisitorCount(httpServletRequest);
        }

        resolveSkinDir(httpServletRequest);
    }

    @Override
    public void requestDestroyed(final ServletRequestEvent servletRequestEvent) {
        Stopwatchs.end();

        LOGGER.log(Level.DEBUG, "Stopwatch: {0}{1}", Strings.LINE_SEPARATOR, Stopwatchs.getTimingStat());
        Stopwatchs.release();

        super.requestDestroyed(servletRequestEvent);
    }

    /**
     * Loads preference.
     * 
     * <p>
     *   Loads preference from repository, loads skins from skin directory then sets it into preference if the skins changed. 
     * </p>
     */
    private void loadPreference() {
        Stopwatchs.start("Load Preference");

        LOGGER.info("Loading preference....");

        final PreferenceRepository preferenceRepository = beanManager.getReference(PreferenceRepositoryImpl.class);
        JSONObject preference;

        try {
            preference = preferenceRepository.get(Preference.PREFERENCE);
            if (null == preference) {
                LOGGER.log(Level.WARN, "Can't not init default skin, please init B3log Solo first");
                return;
            }

            final PreferenceMgmtService preferenceMgmtService = beanManager.getReference(PreferenceMgmtService.class);

            preferenceMgmtService.loadSkins(preference);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            throw new IllegalStateException(e);
        }

        Stopwatchs.end();
    }

    /**
     * Register event processors.
     */
    private void registerEventProcessor() {
        Stopwatchs.start("Register Event Processors");

        LOGGER.log(Level.INFO, "Registering event processors....");
        try {
            final EventManager eventManager = beanManager.getReference(EventManager.class);

            // Comment
            eventManager.registerListener(new ArticleCommentReplyNotifier());
            eventManager.registerListener(new PageCommentReplyNotifier());
            // Article
            eventManager.registerListener(new AddArticleGoogleBlogSearchPinger());
            eventManager.registerListener(new UpdateArticleGoogleBlogSearchPinger());
            // Plugin
            eventManager.registerListener(new PluginRefresher());
            eventManager.registerListener(new ViewLoadEventHandler());
            // Sync
            eventManager.registerListener(new ArticleSender());
            eventManager.registerListener(new ArticleUpdater());
            eventManager.registerListener(new CommentSender());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Register event processors error", e);
            throw new IllegalStateException(e);
        }

        LOGGER.log(Level.INFO, "Registering event processors....");

        Stopwatchs.end();
    }

    /**
     * Resolve skin (template) for the specified HTTP servlet request.
     * 
     * @param httpServletRequest the specified HTTP servlet request
     */
    private void resolveSkinDir(final HttpServletRequest httpServletRequest) {
        try {
            final PreferenceRepository preferenceRepository = beanManager.getReference(PreferenceRepositoryImpl.class);
            final JSONObject preference = preferenceRepository.get(Preference.PREFERENCE);

            if (null == preference) { // Did not initialize yet
                return;
            }

            final String requestURI = httpServletRequest.getRequestURI();

            String desiredView = Requests.mobileSwitchToggle(httpServletRequest);

            if (desiredView == null && !Requests.mobileRequest(httpServletRequest) || desiredView != null && desiredView.equals("normal")) {
                desiredView = preference.getString(Skin.SKIN_DIR_NAME);
            } else {
                desiredView = "mobile";
                LOGGER.log(Level.DEBUG, "The request [URI={0}] comes frome mobile device", requestURI);
            }

            httpServletRequest.setAttribute(Keys.TEMAPLTE_DIR_NAME, desiredView);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Resolves skin failed", e);
        }
    }
}
