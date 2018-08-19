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
/**
 *  about for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.4, Feb 18, 2017
 */

/* about 相关操作 */
admin.about = {
    init: function() {
        $.ajax({
            url: "https://rhythm.b3log.org/version/solo/latest/" + Label.version,
            type: "GET",
            cache: false,
            dataType: "jsonp",
            success: function(data, textStatus) {
                var version = data.soloVersion;
                if (version === Label.version) {
                    $("#aboutLatest").text(Label.upToDateLabel);
                } else {
                    $("#aboutLatest").html(Label.outOfDateLabel +
                            "<a href='" + data.soloDownload + "'>" + version + "</a>");
                }
            },
            complete: function(XHR, TS) {
                admin.clearTip();
            }
        });
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["about"] = {
    "obj": admin.about,
    "init": admin.about.init,
    "refresh": function() {
        admin.clearTip();
    }
};