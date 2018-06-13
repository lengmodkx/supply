$(function () {
   // 项目菜单的高度
    var winhei=parseInt(window.innerHeight);
    $(".project-menu").css({"height":winhei-100 +'px'});
    //点击菜单，打开项目菜单区域
    $(".menu").click(function () {
        $(".project-menu").fadeToggle();
    });
    $(".close-menu").click(function () {
        $(".project-menu").fadeOut();
    })
});