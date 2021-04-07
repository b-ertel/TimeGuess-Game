function showPassword() {
    var field = $('.pw');
    var button = $("#eyeButton").find("span");

    if (field.attr("type") === "password") {
        field.attr("type", "text");
        button.removeClass("fa fa-eye");
        button.addClass("fa fa-eye-slash")
    }
    else {
        field.attr("type", "password");
        button.removeClass("fa fa-eye-slash");
        button.addClass("fa fa-eye");
    }
}