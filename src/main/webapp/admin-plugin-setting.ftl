<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-2018, b3log.org & hacpai.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

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
            url: latkeConfig.servePath + "/console/plugin/updateSetting",
            type: "POST",
            cache: false,
            data: JSON.stringify(requestJSONObject),
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
            }
        });
	}
</script>
