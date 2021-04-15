function showPassword(button, num) {
    var inpt = $($('.pw')[num]);
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