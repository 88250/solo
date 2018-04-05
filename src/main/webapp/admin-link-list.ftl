<div>
    <div id="linkTable"></div>
    <div id="linkPagination" class="margin12 right"></div>
</div>
<div class="clear"></div>
<div class="form form__no-table">
${addLinkLabel}
    <label>${linkTitle1Label}</label>
    <input id="linkTitle" type="text"/>
    <label>${url1Label}</label>
    <input id="linkAddress" type="text"/>
    <label>${linkDescription1Label}</label>
    <input id="linkDescription" type="text"/> <br><br>
    <button onclick="admin.linkList.add();" class="right">${saveLabel}</button>
    <div class="clear"></div>
</div>
<div id="updateLink" class="none form form__no-table">
${updateLinkLabel}
    <label>${linkTitle1Label}</label>
    <input id="linkTitleUpdate" type="text"/>
    <label>${url1Label}</label>
    <input id="linkAddressUpdate" type="text"/>
    <label>${linkDescription1Label}</label>
    <input id="linkDescriptionUpdate" type="text"/><br><br>
    <button onclick="admin.linkList.update();" class="right">${updateLabel}</button>
    <div class="clear"></div>
</div>
${plugins}