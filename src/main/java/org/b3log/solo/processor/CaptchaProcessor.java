/*
 * Copyright (c) 2010-2018, b3log.org & hacpai.com
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
package org.b3log.solo.processor;

import org.b3log.latke.image.Image;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.PNGRenderer;
import org.b3log.latke.util.Strings;
import org.patchca.color.SingleColorFactory;
import org.patchca.filter.predefined.CurvesRippleFilterFactory;
import org.patchca.service.Captcha;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.word.RandomWordFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Captcha processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Apr 5, 2018
 * @since 0.3.1
 */
@RequestProcessor
public class CaptchaProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CaptchaProcessor.class);

    /**
     * Key of captcha.
     */
    public static final String CAPTCHA = "captcha";

    /**
     * Captchas.
     */
    private static final Set<String> CAPTCHAS = new HashSet<>();

    /**
     * Captcha length.
     */
    private static final int CAPTCHA_LENGTH = 4;

    /**
     * Flag of captcha is enabled.
     */
    public static boolean CAPTCHA_ON = true;

    /**
     * Gets captcha.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/captcha.do", method = HTTPRequestMethod.GET)
    public void get(final HTTPRequestContext context) {
        final PNGRenderer renderer = new PNGRenderer();
        context.setRenderer(renderer);

        try {
            final ConfigurableCaptchaService cs = new ConfigurableCaptchaService();
            cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)));
            cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));
            final RandomWordFactory randomWordFactory = new RandomWordFactory();
            randomWordFactory.setCharacters("abcdefghijklmnprstuvwxy23456789");
            randomWordFactory.setMinLength(CAPTCHA_LENGTH);
            randomWordFactory.setMaxLength(CAPTCHA_LENGTH);
            cs.setWordFactory(randomWordFactory);
            final Captcha captcha = cs.getCaptcha();
            final String challenge = captcha.getChallenge();
            final BufferedImage bufferedImage = captcha.getImage();

            if (CAPTCHAS.size() > 64) {
                CAPTCHAS.clear();
            }

            CAPTCHAS.add(challenge);

            final HttpServletResponse response = context.getResponse();
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(bufferedImage, "png", baos);
                final byte[] data = baos.toByteArray();
                final Image captchaImg = new Image();
                captchaImg.setData(data);
                renderer.setImage(captchaImg);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
        }
    }

    /**
     * Checks whether the specified captcha is invalid.
     *
     * @param captcha the specified captcha
     * @return {@code true} if it is invalid, returns {@code false} otherwise
     */
    public static boolean invalidCaptcha(final String captcha) {
        if (!CAPTCHA_ON) {
            return false;
        }

        if (Strings.isEmptyOrNull(captcha) || captcha.length() != CAPTCHA_LENGTH) {
            return true;
        }

        boolean ret = !CaptchaProcessor.CAPTCHAS.contains(captcha);
        if (!ret) {
            CaptchaProcessor.CAPTCHAS.remove(captcha);
        }

        return ret;
    }
}
