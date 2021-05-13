function showPassword(button, id = '') {
    var inpt = $('#password' + id);
    var span = $(button).find("span");
    
    if (inpt.attr("type") === "password") {
        inpt.attr("type", "text");
        span.removeClass("fa fa-eye");
        span.addClass("fa fa-eye-slash")
    }
    else {
        inpt.attr("type", "password");
        span.removeClass("fa fa-eye-slash");
        span.addClass("fa fa-eye");
    }
}

function socketListener(message, channel, event) {
    window[message.type](message.name, message.id);
    console.log(message);
}

function gameInvitation(name, id) {
    clickIfExists("gameInvitation");
}

function gameUpdate(name, id) {
    clickIfExists("lobbyUpdate");
    clickIfExists("profileUpdate");
}

function teamUpdate(name, id) {
    clickIfExists("lobbyUpdate");
    clickIfExists("profileUpdate");
}

function termUpdate(name, id) {
    clickIfExists("lobbyUpdate");
    clickIfExists("profileUpdate");
    clickIfExists("termsUpdate");
    clickIfExists("topicsUpdate");
}

function topicUpdate(name, id) {
    clickIfExists("lobbyUpdate");
    clickIfExists("profileUpdate");
    clickIfExists("termsUpdate");
    clickIfExists("topicsUpdate");
}

function userUpdate(name, id) {
    clickIfExists("lobbyUpdate");
    clickIfExists("profileUpdate");
    clickIfExists("usersUpdate");
}

function connectionCubeUpdate(name, id) {
    clickIfExists("lobbyUpdate");
}

function clickIfExists(id) {
    var btn = $('#' + id);
    if (btn != null) btn.click();
}
