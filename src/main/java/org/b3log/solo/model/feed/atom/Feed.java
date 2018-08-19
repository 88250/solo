/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2018, b3log.org & hacpai.com
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
package org.b3log.solo.model.feed.atom;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.XMLs;
import org.b3log.solo.processor.FeedProcessor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Atom Feed.
 * <p>
 * See <a href="http://tools.ietf.org/html/rfc4287">RFC 4278</a> for more details.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.1, Aug 2, 2018
 * @see Entry
 * @see Category
 * @since 0.3.1
 */
public final class Feed {

    /**
     * Time zone id.
     */
    public static final String TIME_ZONE_ID = "Asia/Shanghai";

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(FeedProcessor.class);

    /**
     * Link variable.
     */
    private static final String LINK_VARIABLE = "${link}";

    /**
     * Start document.
     */
    private static final String START_DOCUMENT = "<?xml version=\"1.0\"?>";

    /**
     * Start feed element.
     */
    private static final String START_FEED_ELEMENT = "<feed xmlns=\"http://www.w3.org/2005/Atom\">";

    /**
     * End feed element.
     */
    private static final String END_FEED_ELEMENT = "</feed>";

    /**
     * Start id element.
     */
    private static final String START_ID_ELEMENT = "<id>";

    /**
     * End if element.
     */
    private static final String END_ID_ELEMENT = "</id>";

    /**
     * Start title element.
     */
    private static final String START_TITLE_ELEMENT = "<title type=\"text\">";

    /**
     * End title element.
     */
    private static final String END_TITLE_ELEMENT = "</title>";

    /**
     * Start subtitle element.
     */
    private static final String START_SUBTITLE_ELEMENT = "<subtitle type=\"text\"> ";

    /**
     * End subtitle element.
     */
    private static final String END_SUBTITLE_ELEMENT = "</subtitle>";

    /**
     * Start updated element.
     */
    private static final String START_UPDATED_ELEMENT = "<updated>";

    /**
     * End updated element.
     */
    private static final String END_UPDATED_ELEMENT = "</updated>";

    /**
     * Start author element.
     */
    private static final String START_AUTHOR_ELEMENT = "<author>";

    /**
     * End author element.
     */
    private static final String END_AUTHOR_ELEMENT = "</author>";

    /**
     * Start name element.
     */
    private static final String START_NAME_ELEMENT = "<name>";

    /**
     * End name element.
     */
    private static final String END_NAME_ELEMENT = "</name>";

    /**
     * Link element.
     */
    private static final String LINK_ELEMENT = "<link href=\"" + LINK_VARIABLE + "\" rel=\"self\" " + "type=\"application/atom+xml\" />";

    /**
     * Id.
     */
    private String id;

    /**
     * Title.
     */
    private String title;

    /**
     * Subtitle.
     */
    private String subtitle;

    /**
     * Update date.
     */
    private Date updated;

    /**
     * Author.
     */
    private String author;

    /**
     * Link.
     */
    private String link;

    /**
     * Entries.
     */
    private List<Entry> entries = new ArrayList<>();

    /**
     * Gets the id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id with the specified id.
     *
     * @param id the specified id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Gets the link.
     *
     * @return link
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the link with the specified link.
     *
     * @param link the specified link
     */
    public void setLink(final String link) {
        this.link = link;
    }

    /**
     * Gets the title.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title with the specified title.
     *
     * @param title the specified title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Gets the subtitle.
     *
     * @return subtitle
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * Sets the subtitle with the specified subtitle.
     *
     * @param subtitle the specified subtitle
     */
    public void setSubtitle(final String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * Gets the author.
     *
     * @return author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author with the specified author.
     *
     * @param author the specified author
     */
    public void setAuthor(final String author) {
        this.author = author;
    }

    /**
     * Gets update date.
     *
     * @return update date
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * Sets the update date with the specified update date.
     *
     * @param updated the specified update date
     */
    public void setUpdated(final Date updated) {
        this.updated = updated;
    }

    /**
     * Adds the specified entry.
     *
     * @param entry the specified entry
     */
    public void addEntry(final Entry entry) {
        entries.add(entry);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(START_DOCUMENT);
        stringBuilder.append(START_FEED_ELEMENT);

        stringBuilder.append(START_ID_ELEMENT);
        stringBuilder.append(StringEscapeUtils.escapeXml(id));
        stringBuilder.append(END_ID_ELEMENT);

        stringBuilder.append(START_TITLE_ELEMENT);
        stringBuilder.append(StringEscapeUtils.escapeXml(title));
        stringBuilder.append(END_TITLE_ELEMENT);

        stringBuilder.append(START_SUBTITLE_ELEMENT);
        stringBuilder.append(StringEscapeUtils.escapeXml(subtitle));
        stringBuilder.append(END_SUBTITLE_ELEMENT);

        stringBuilder.append(START_UPDATED_ELEMENT);
        stringBuilder.append(DateFormatUtils.format(// using ISO-8601 instead of RFC-3339
                updated, DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(), TimeZone.getTimeZone(TIME_ZONE_ID)));
        stringBuilder.append(END_UPDATED_ELEMENT);

        stringBuilder.append(START_AUTHOR_ELEMENT);
        stringBuilder.append(START_NAME_ELEMENT);
        stringBuilder.append(author);
        stringBuilder.append(END_NAME_ELEMENT);
        stringBuilder.append(END_AUTHOR_ELEMENT);

        stringBuilder.append(LINK_ELEMENT.replace(LINK_VARIABLE, StringEscapeUtils.escapeXml(link)));

        for (final Entry entry : entries) {
            stringBuilder.append(entry.toString());
        }

        stringBuilder.append(END_FEED_ELEMENT);

        return XMLs.format(stringBuilder.toString());
    }
}
