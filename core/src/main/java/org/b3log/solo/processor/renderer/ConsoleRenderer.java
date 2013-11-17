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
package org.b3log.solo.processor.renderer;


import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.IOException;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import org.b3log.solo.SoloServletListener;


/**
 * <a href="http://freemarker.org">FreeMarker</a> HTTP response
 * renderer for administrator console and initialization rendering.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Nov 17, 2013
 * @since 0.4.1
 */
public final class ConsoleRenderer extends AbstractFreeMarkerRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ConsoleRenderer.class.getName());

    /**
     * FreeMarker configuration.
     */
    public static final Configuration TEMPLATE_CFG;

    static {
        TEMPLATE_CFG = new Configuration();
        TEMPLATE_CFG.setDefaultEncoding("UTF-8");
        try {
            final String webRootPath = SoloServletListener.getWebRoot();

            TEMPLATE_CFG.setDirectoryForTemplateLoading(new File(webRootPath));
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
        }
    }

    @Override
    protected Template getTemplate(final String templateDirName, final String templateName) {
        try {
            return TEMPLATE_CFG.getTemplate(templateName);
        } catch (final IOException e) {
            return null;
        }
    }

    @Override
    protected void beforeRender(final HTTPRequestContext context) throws Exception {}

    @Override
    protected void afterRender(final HTTPRequestContext context) throws Exception {}
}
