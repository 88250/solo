<div id="tabOthers" class="sub-tabs">
    <ul>
        <li>
            <div id="tabOthers_email">
                <a class="tab-current" href="#tools/others/email">${replayEmailTemplateLabel}</a>
            </div>
        </li>
        <li>
            <div id="tabOthers_other">
                <a href="#tools/others/other">${othersLabel}</a>
            </div>
        </li>
    </ul>
</div>
<div id="tabOthersPanel" class="sub-tabs-main">
    <div id="tabOthersPanel_email" class="form form__no-table">
        <label for="replayEmailTemplateTitle">${emailSubject1Label}</label>
        <input id="replayEmailTemplateTitle" type="text"/>
        <label for="replayEmailTemplateBody">${emailContent1Label}</label>
        <textarea rows="9" id="replayEmailTemplateBody"></textarea><br><br>
        <button onclick="admin.others.update()" class="right">${updateLabel}</button>
        <div class="clear"></div>
        <div class="content-reset">
        ${replayEmailExplanationLabel}
        </div>
    </div>
    <div id="tabOthersPanel_other" class="none">
        <button class="margin12" onclick="admin.others.removeUnusedTags();">${removeUnusedTagsLabel}</button>
        <#if supportExport>
        <button class="margin12" onclick="admin.others.exportSQL();">${exportSQLLabel}</button>
        </#if>
        <button class="margin12" onclick="admin.others.exportJSON();">${exportJSONLabel}</button>
        <button class="margin12" onclick="admin.others.exportHexo();">${exportHexoLabel}</button>
    </div>
</div>
${plugins}
