$(function () {
    //点击 待处理 部分 下箭头，出现内容
    $(".add-new-model").click(function () {
        $(".lbmenu").slideToggle()
    });
    $(".lbmenu-close").click(function () {
        $(".lbmenu").slideUp()
    });
})