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
    $("#gameInvitation").click();
}

function gameUpdate(name, id) {
    $("#gameUpdate").click();
}
