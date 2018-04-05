<div>
    <div id="userTable"></div>
    <div id="userPagination" class="margin12 right"></div>
</div>
<div class="clear"></div>
<div class="form form__no-table">
${addUserLabel}
    <label for="userName">${commentName1Label}</label>
    <input id="userName" type="text"/>
    <label for="userEmail">${commentEmail1Label}</label>
    <input id="userEmail" type="text"/>
    <label for="userURL">${userURL1Label}</label>
    <input id="userURL" type="text"/>
    <label for="userPassword">${userPassword1Label}</label>
    <input id="userPassword" type="password" autocomplete="new-password"/>
    <label for="userAvatar">${userAvatar1Label}</label>
    <input id="userAvatar" type="text"/><br><br>
    <button onclick="admin.userList.add();" class="right">${saveLabel}</button>
    <div class="clear"></div>
</div>
<div id="userUpdate" class="none form form__table">
${updateUserLabel}
    <label for="userNameUpdate">${commentName1Label}</label>
    <input id="userNameUpdate" type="text"/>
    <label for="userEmailUpdate">${commentEmail1Label}</label>
    <input id="userEmailUpdate" type="text"/>
    <label for="userURLUpdate">${userURL1Label}</label>
    <input id="userURLUpdate" type="text"/>
    <label for="userPasswordUpdate">${userPassword1Label}</label>
    <input id="userPasswordUpdate" type="password"/>
    <label for="userAvatarUpdate">${userAvatar1Label}</label>
    <input id="userAvatarUpdate" type="text"/> <br><br>
    <button onclick="admin.userList.update();" class="right">${updateLabel}</button>
    <div class="clear"></div>
</div>
${plugins}
