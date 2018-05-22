/*
 * Solo - A beautiful, simple, stable, fast Java blogging system.
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
package org.b3log.solo.util;

import org.b3log.latke.util.MD5;

import java.util.Random;

/**
 * Generate random stuff, <p> currently only support random alpha and digital
 * string, whose length is also random between 8 and 16.
 *
 * @author <a href="mailto:dongxu.wang@acm.org">Dongxu Wang</a>
 * @version 1.0.0.1, Mar 11, 2013
 */
public class Randoms {

    /**
     * String's length should be positive.
     */
    private static final int LEN_LIM = 1;

    /**
     * String's length maximum limit.
     */
    private static final int MAX_LEN = 16;

    /**
     * String's length minimum limit.
     */
    private static final int MIN_LEN = 8;

    /**
     * String's random length.
     */
    private static final int RANDOM_LEN = new Random().nextInt(MAX_LEN - MIN_LEN + LEN_LIM) + MIN_LEN;

    /**
     * Characters set table, can be extended.
     */
    private final char[] table;

    /**
     * String's random seed.
     */
    private final Random random = new Random();

    /**
     * String's random characters buffer.
     */
    private final char[] buf;

    /**
     * Generate a random string with a random length [8, 16].
     */
    public Randoms() {
        this(RANDOM_LEN);
    }

    /**
     * Generate a random string with given length.
     *
     * @param length string's length
     */
    public Randoms(final int length) {
        if (length < LEN_LIM) {
            throw new IllegalArgumentException("length < 1: " + length);
        }
        table = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        buf = new char[length];
    }

    /**
     * Generate next random string, whose length is also random between 8 and
     * 16.
     *
     * @return next random string
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = table[random.nextInt(table.length)];
        }
        return new String(buf);
    }

    /**
     * Generate next random MD5 string.
     *
     * @return next random string with MD5
     */
    public String nextStringWithMD5() {
        return MD5.hash(nextString());
    }
}
