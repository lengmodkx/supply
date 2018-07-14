
$(function () {
   //点击 任务 分享 日程 文件 切换 页面
    $(".one-level-nav li").click(function () {
        $(this).addClass("now").siblings().removeClass("now");
        var i=$(this).index();
        $(".body-box>div").eq(i).show().siblings().hide();
    });
    $("html").on("click",".style-li",function () {
        $(this).addClass("selected").siblings().removeClass("selected")
    });
    $("html").on("click",".a-task-li ",function () {
        if ($(this).hasClass("selected")) {
            $(this).removeClass("selected")
        }else {
            $(this).addClass("selected")
        }
    });
    $("html").on("click",".wj-li ",function () {
        if ($(this).hasClass("selected")) {
            $(this).removeClass("selected");
            $(this).find("img").attr("src","../static/image/wjj-b.png")
        }else {
            $(this).addClass("selected");
            $(this).find("img").attr("src","../static/image/wjj-fff.png")
        }
    });
    //点击显示 隐藏 过去 日程
    $("html").on("click",".is-show",function () {
        if ($(this).parent().siblings(".old-rc").is(":visible")){
            $(this).parent().siblings(".old-rc").hide();
            $(this).text("显示")
        } else {
            $(this).parent().siblings(".old-rc").show();
            $(this).text("隐藏")
        }
    });
    //点击新建文件夹
    $("html").on("click",".up-add>i:nth-of-type(2)",function () {
        $(this).parent().siblings(".wjj-wrap").find("input").show();
    });
    $("html").on("keyup",".create-wjjs",function (e) {
        if(e.which == 13) {
            if ($(this).val()==''){
                $(this).hide();
            }else {
                $(this).siblings("ul").prepend('<li class="wj-li over-hidden">\n' +
                    '                                        <img src="../static/image/wjj-b.png" th:src="@{/image/wjj-b.png}">\n' +
                    '                                        <span >'+$(this).val()+'</span>\n' +
                    '                                    </li>');
                $(this).hide();
            }
        }
    })
});