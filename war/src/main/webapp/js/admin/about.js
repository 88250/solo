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
/**
 *  about for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Jan 7, 2012
 */

/* about 相关操作 */
admin.about = {
    init: function () {
        $.ajax({
            url: "http://rhythm.b3log.org/version/solo/latest/" + Label.version,
            type: "GET",
            cache: false,
            dataType:"jsonp",
            error: function() {
            // alert("Error loading articles from Rhythm");
            },
            success: function(data, textStatus) {
                var version = data.soloVersion;
                if (version === Label.version) {
                    $("#aboutLatest").text(Label.upToDateLabel);
                } else {
                    $("#aboutLatest").html(Label.outOfDateLabel +
                        "<a href='" + data.soloDownload + "'>" + version + "</a>");
                }
                $("#loadMsg").text("");
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
    "refresh": function () {
        $("#loadMsg").text("");
    }
}