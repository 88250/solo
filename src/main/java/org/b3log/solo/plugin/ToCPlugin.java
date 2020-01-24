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
package org.b3log.solo.plugin;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.plugin.NotInteractivePlugin;
import org.b3log.latke.plugin.PluginStatus;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Article;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ToC event handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.1.0, Jan 24, 2020
 * @since 0.6.7
 */
public class ToCPlugin extends NotInteractivePlugin {

    private ToCEventHandler handler = new ToCEventHandler();

    @Override
    public void changeStatus() {
        super.changeStatus();

        final EventManager eventManager = BeanManager.getInstance().getReference(EventManager.class);
        final PluginStatus status = getStatus();
        if (PluginStatus.DISABLED == status) {
            eventManager.unregisterListener(handler);
        } else {
            eventManager.registerListener(handler);
        }
    }

    @Override
    public void prePlug(RequestContext context) {
    }

    @Override
    public void postPlug(Map<String, Object> dataModel, RequestContext context) {
    }
}

/**
 * ToC event handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="http://www.annpeter.cn">Ann Peter</a>
 * @author <a href="http://vanessa.b3log.org">Vanessa</a>
 * @version 2.0.0.1, Jan 24, 2020
 * @since 0.6.7
 */
class ToCEventHandler extends AbstractEventListener<JSONObject> {

    @Override
    public String getEventType() {
        return EventTypes.BEFORE_RENDER_ARTICLE;
    }

    @Override
    public void action(final Event<JSONObject> event) {
        final JSONObject data = event.getData();
        final JSONObject article = data.optJSONObject(Article.ARTICLE);
        final String content = article.optString(Article.ARTICLE_CONTENT);
        final Document doc = Jsoup.parse(content, StringUtils.EMPTY, Parser.htmlParser());
        doc.outputSettings().prettyPrint(false);

        final List<JSONObject> toc = new ArrayList<>();
        final Elements hs = doc.select("h1, h2, h3, h4, h5");
        for (int i = 0; i < hs.size(); i++) {
            final Element element = hs.get(i);
            final String tagName = element.tagName().toLowerCase();
            final String text = element.text();
            final String id = "toc_" + tagName + "_" + i;
            element.attr("id", id);
            final JSONObject li = new JSONObject().
                    put("className", "toc__" + tagName).
                    put("id", id).
                    put("text", text);
            toc.add(li);
        }
        final Element body = doc.getElementsByTag("body").get(0);
        article.put(Article.ARTICLE_CONTENT, body.html());
        article.put(Article.ARTICLE_T_TOC, (Object) toc);
    }
}
