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
package org.b3log.solo.processor.console;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.JsonRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.URLs;
import org.b3log.solo.model.Category;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Tag;
import org.b3log.solo.service.CategoryMgmtService;
import org.b3log.solo.service.CategoryQueryService;
import org.b3log.solo.service.TagQueryService;
import org.b3log.solo.util.Solos;
import org.b3log.solo.util.StatusCodes;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Category console request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://ld246.com/member/lzh984294471">lzh984294471</a>
 * @version 2.0.1.0, Jul 21, 2020
 * @since 2.0.0
 */
@Singleton
public class CategoryConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(CategoryConsole.class);

    /**
     * Category management service.
     */
    @Inject
    private CategoryMgmtService categoryMgmtService;

    /**
     * Category query service.
     */
    @Inject
    private CategoryQueryService categoryQueryService;

    /**
     * Tag query service.
     */
    @Inject
    private TagQueryService tagQueryService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Changes a category order by the specified category id and direction.
     * <p>
     * Request json:
     * <pre>
     * {
     *     "oId": "",
     *     "direction": "" // "up"/"down"
     * }
     * </pre>
     * </p>
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void changeOrder(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();
        try {
            final JSONObject requestJSON = context.requestJSON();
            final String categoryId = requestJSON.getString(Keys.OBJECT_ID);
            final String direction = requestJSON.getString(Common.DIRECTION);

            categoryMgmtService.changeOrder(categoryId, direction);

            ret.put(Keys.CODE, StatusCodes.SUCC);
            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));
            renderer.setJSONObject(ret);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("updateFailLabel"));
        }
    }

    /**
     * Gets a category by the specified request.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "category": {
     *         "oId": "",
     *         "categoryTitle": "",
     *         "categoryURI": "",
     *         ....
     *     }
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void getCategory(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        try {
            final String categoryId = context.pathVar("id");
            final JSONObject result = categoryQueryService.getCategory(categoryId);
            if (null == result) {
                renderer.setJSONObject(new JSONObject().put(Keys.CODE, StatusCodes.ERR));
                return;
            }

            final StringBuilder tagBuilder = new StringBuilder();
            final List<JSONObject> tags = (List<JSONObject>) result.opt(Category.CATEGORY_T_TAGS);
            for (final JSONObject tag : tags) {
                if (null == tag || !tag.has(Tag.TAG_TITLE)) { // 修复修改分类时空指针错误 https://github.com/b3log/solo/pull/12876
                    continue;
                }
                tagBuilder.append(tag.optString(Tag.TAG_TITLE)).append(",");
            }
            if (0 < tagBuilder.length()) {
                tagBuilder.deleteCharAt(tagBuilder.length() - 1);
            }
            result.put(Category.CATEGORY_T_TAGS, tagBuilder.toString());

            renderer.setJSONObject(result);
            result.put(Keys.CODE, StatusCodes.SUCC);
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Removes a category by the specified request.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     * @throws Exception exception
     */
    public void removeCategory(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);
        try {
            final String categoryId = context.pathVar("id");
            categoryMgmtService.removeCategory(categoryId);

            jsonObject.put(Keys.CODE, StatusCodes.SUCC);
            jsonObject.put(Keys.MSG, langPropsService.get("removeSuccLabel"));
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            jsonObject.put(Keys.CODE, StatusCodes.ERR);
            jsonObject.put(Keys.MSG, langPropsService.get("removeFailLabel"));
        }
    }

    /**
     * Updates a category by the specified request.
     * <p>
     * Request json:
     * <pre>
     * {
     *     "oId": "",
     *     "categoryTitle": "",
     *     "categoryURI": "",
     *     "categoryDescription": "", // optional
     *     "categoryTags": "tag1, tag2" // optional
     * }
     * </pre>
     * </p>
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void updateCategory(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();
        renderer.setJSONObject(ret);

        try {
            final JSONObject requestJSON = context.requestJSON();
            String tagsStr = requestJSON.optString(Category.CATEGORY_T_TAGS);
            tagsStr = Tag.formatTags(tagsStr, 64);
            if (StringUtils.isBlank(tagsStr)) {
                throw new ServiceException(langPropsService.get("tagsEmptyLabel"));
            }
            final String[] tagTitles = tagsStr.split(",");
            String addArticleWithTagFirstLabel = langPropsService.get("addArticleWithTagFirstLabel");

            final List<JSONObject> tags = new ArrayList<>();
            final Set<String> deduplicate = new HashSet<>();
            for (int i = 0; i < tagTitles.length; i++) {
                String tagTitle = StringUtils.trim(tagTitles[i]);
                if (StringUtils.isBlank(tagTitle)) {
                    continue;
                }

                final JSONObject tagResult = tagQueryService.getTagByTitle(tagTitle);
                if (null == tagResult) {
                    addArticleWithTagFirstLabel = addArticleWithTagFirstLabel.replace("{tag}", tagTitle);

                    final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
                    renderer.setJSONObject(jsonObject);
                    jsonObject.put(Keys.MSG, addArticleWithTagFirstLabel);
                    return;
                }

                if (deduplicate.contains(tagTitle)) {
                    continue;
                }

                tags.add(tagResult.optJSONObject(Tag.TAG));
                deduplicate.add(tagTitle);
            }

            final String categoryId = requestJSON.optString(Keys.OBJECT_ID);
            final String title = requestJSON.optString(Category.CATEGORY_TITLE, "Category");
            JSONObject mayExist = categoryQueryService.getByTitle(title);
            if (null != mayExist && !mayExist.optString(Keys.OBJECT_ID).equals(categoryId)) {
                final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
                renderer.setJSONObject(jsonObject);
                jsonObject.put(Keys.MSG, langPropsService.get("duplicatedCategoryLabel"));
                return;
            }

            final String uri = requestJSON.optString(Category.CATEGORY_URI);
            if (StringUtils.isBlank(uri) || !uri.equals(URLs.encode(uri))) {
                final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
                renderer.setJSONObject(jsonObject);
                jsonObject.put(Keys.MSG, langPropsService.get("categoryURIMustBeASCIILabel"));
                return;
            }

            mayExist = categoryQueryService.getByURI(uri);
            if (null != mayExist && !mayExist.optString(Keys.OBJECT_ID).equals(categoryId)) {
                final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
                renderer.setJSONObject(jsonObject);
                jsonObject.put(Keys.MSG, langPropsService.get("duplicatedCategoryURILabel"));
                return;
            }
            if (255 <= StringUtils.length(uri)) {
                final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
                renderer.setJSONObject(jsonObject);
                jsonObject.put(Keys.MSG, langPropsService.get("categoryURITooLongLabel"));
                return;
            }

            final String desc = requestJSON.optString(Category.CATEGORY_DESCRIPTION);

            final JSONObject category = new JSONObject();
            category.put(Category.CATEGORY_TITLE, title);
            category.put(Category.CATEGORY_URI, uri);
            category.put(Category.CATEGORY_DESCRIPTION, desc);

            categoryMgmtService.updateCategory(categoryId, category);
            categoryMgmtService.removeCategoryTags(categoryId); // remove old relations

            // add new relations
            for (final JSONObject tag : tags) {
                final JSONObject categoryTag = new JSONObject();
                categoryTag.put(Category.CATEGORY + "_" + Keys.OBJECT_ID, categoryId);
                categoryTag.put(Tag.TAG + "_" + Keys.OBJECT_ID, tag.optString(Keys.OBJECT_ID));

                categoryMgmtService.addCategoryTag(categoryTag);
            }

            ret.put(Keys.OBJECT_ID, categoryId);
            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));
            ret.put(Keys.CODE, StatusCodes.SUCC);
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("updateFailLabel"));
        }
    }

    /**
     * Adds a category with the specified request.
     * <p>
     * Request json:
     * <pre>
     * {
     *     "categoryTitle": "",
     *     "categoryURI": "",
     *     "categoryDescription": "", // optional
     *     "categoryTags": "tag1, tag2" // optional
     * }
     * </pre>
     * </p>
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "oId": "", // Generated category id
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void addCategory(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();
        renderer.setJSONObject(ret);

        try {
            final JSONObject requestJSONObject = context.requestJSON();
            String tagsStr = requestJSONObject.optString(Category.CATEGORY_T_TAGS);
            tagsStr = Tag.formatTags(tagsStr, 64);
            if (StringUtils.isBlank(tagsStr)) {
                throw new ServiceException(langPropsService.get("tagsEmptyLabel"));
            }
            final String[] tagTitles = tagsStr.split(",");
            String addArticleWithTagFirstLabel = langPropsService.get("addArticleWithTagFirstLabel");

            final List<JSONObject> tags = new ArrayList<>();
            final Set<String> deduplicate = new HashSet<>();
            for (int i = 0; i < tagTitles.length; i++) {
                String tagTitle = StringUtils.trim(tagTitles[i]);
                if (StringUtils.isBlank(tagTitle)) {
                    continue;
                }

                final JSONObject tagResult = tagQueryService.getTagByTitle(tagTitle);
                if (null == tagResult) {
                    addArticleWithTagFirstLabel = addArticleWithTagFirstLabel.replace("{tag}", tagTitle);

                    final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
                    renderer.setJSONObject(jsonObject);
                    jsonObject.put(Keys.MSG, addArticleWithTagFirstLabel);
                    return;
                }

                if (deduplicate.contains(tagTitle)) {
                    continue;
                }

                tags.add(tagResult.optJSONObject(Tag.TAG));
                deduplicate.add(tagTitle);
            }

            final String title = requestJSONObject.optString(Category.CATEGORY_TITLE, "Category");
            JSONObject mayExist = categoryQueryService.getByTitle(title);
            if (null != mayExist) {
                final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
                renderer.setJSONObject(jsonObject);
                jsonObject.put(Keys.MSG, langPropsService.get("duplicatedCategoryLabel"));
                return;
            }

            final String uri = requestJSONObject.optString(Category.CATEGORY_URI);
            if (StringUtils.isBlank(uri) || !uri.equals(URLs.encode(uri))) {
                final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
                renderer.setJSONObject(jsonObject);
                jsonObject.put(Keys.MSG, langPropsService.get("categoryURIMustBeASCIILabel"));
                return;
            }
            mayExist = categoryQueryService.getByURI(uri);
            if (null != mayExist) {
                final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
                renderer.setJSONObject(jsonObject);
                jsonObject.put(Keys.MSG, langPropsService.get("duplicatedCategoryURILabel"));
                return;
            }
            if (255 <= StringUtils.length(uri)) {
                final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
                renderer.setJSONObject(jsonObject);
                jsonObject.put(Keys.MSG, langPropsService.get("categoryURITooLongLabel"));
                return;
            }

            final String desc = requestJSONObject.optString(Category.CATEGORY_DESCRIPTION);

            final JSONObject category = new JSONObject();
            category.put(Category.CATEGORY_TITLE, title);
            category.put(Category.CATEGORY_URI, uri);
            category.put(Category.CATEGORY_DESCRIPTION, desc);

            final String categoryId = categoryMgmtService.addCategory(category);

            for (final JSONObject tag : tags) {
                final JSONObject categoryTag = new JSONObject();
                categoryTag.put(Category.CATEGORY + "_" + Keys.OBJECT_ID, categoryId);
                categoryTag.put(Tag.TAG + "_" + Keys.OBJECT_ID, tag.optString(Keys.OBJECT_ID));

                categoryMgmtService.addCategoryTag(categoryTag);
            }

            ret.put(Keys.OBJECT_ID, categoryId);
            ret.put(Keys.MSG, langPropsService.get("addSuccLabel"));
            ret.put(Keys.CODE, StatusCodes.SUCC);
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("updateFailLabel"));
        }
    }

    /**
     * Gets categories by the specified request json object.
     * <p>
     * The request URI contains the pagination arguments. For example, the request URI is /console/categories/1/10/20, means
     * the current page is 1, the page size is 10, and the window size is 20.
     * </p>
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "categories": [{
     *         "oId": "",
     *         "categoryTitle": "",
     *         "categoryURI": "",
     *         ....
     *      }, ....]
     *     "code": 0
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void getCategories(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);

        try {
            final String requestURI = context.requestURI();
            final String path = requestURI.substring((Latkes.getContextPath() + "/console/categories/").length());
            final JSONObject requestJSONObject = Solos.buildPaginationRequest(path);
            final JSONObject result = categoryQueryService.getCategoris(requestJSONObject);
            result.put(Keys.CODE, StatusCodes.SUCC);
            renderer.setJSONObject(result);

            final List<JSONObject> categories = (List<JSONObject>) result.opt(Category.CATEGORIES);
            for (final JSONObject category : categories) {
                String title = category.optString(Category.CATEGORY_TITLE);
                title = StringEscapeUtils.escapeXml(title);
                category.put(Category.CATEGORY_TITLE, title);
            }
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }
}
