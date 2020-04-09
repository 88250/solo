<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

    Solo is licensed under Mulan PSL v2.
    You can use this software according to the terms and conditions of the Mulan PSL v2.
    You may obtain a copy of Mulan PSL v2 at:
            http://license.coscl.org.cn/MulanPSL2
    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
    See the Mulan PSL v2 for more details.

-->
<textarea id="jsoneditor" rows="10" cols="110">
	${setting}
</textarea>

<input type="hidden" id="pluginId" value="${oId}">
<button class="marginRight12" id="updateSetting" onclick="updateSetting()">save</button>

<script type="text/javascript">
	
	
	function updateSetting(){
	
		var pluginId = $("#pluginId").val();
	 	var json = $("#jsoneditor").val();
	 	alert(json);
		
		$("#loadMsg").text(Label.loadingLabel);
          var requestJSONObject = {
            "oId": pluginId,
            "setting":json
        };
        
        $.ajax({
            url: Label.servePath + "/console/plugin/updateSetting",
            type: "POST",
            cache: false,
            data: JSON.stringify(requestJSONObject),
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
            }
        });
	}
</script>
