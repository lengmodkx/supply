$(function () {

    $(".tag-search-title img").click(function () {
        $(".tag-search").hide();
        $(".build-tags").show();
    });
    $(".go-return").click(function () {
        $(".tag-search").show();
        $(".build-tags").hide();
    });
    $(".close-tag").click(function () {
        $(".tags-search-build").slideUp();
    });
    $(".has-tags>i").click(function (e) {
        $(".tags-search-build").show();
        $(".tag-search").show();

        e.stopPropagation();
    });

    // 点击某个具体标签
    $(".tags-list").click(function () {
        var tag = $(this).find(".tag-font").text();
        $(".has-tags").show()
        $(".has-tags").prepend('<span class="tag" style=" background-color: #A1D5FA;">\n' +
            '                    ' + tag + '  \n' +
            '                    <i class="layui-icon layui-icon-close-fill" style="font-size: 14px; color: #1E9FFF;"></i>\n' +
            '                </span>')
    });
    $(".revise-task").on("click", ".tag i", function () {
        $(this).parent().remove();
        //判断 有没有标签
        console.log($(".has-tags span").length);
        if ($(".has-tags span").length == 0) {
            $(".has-tags").hide();
        } else {
            $(".has-tags").show();
        }
    });
    //点击颜色，颜色出现对勾
    $(".color-pick li").click(function () {
        $(this).find("i").show();
        $(this).siblings().find("i").hide()
    });
    //点击空白区域 添加标签消失
    $(document).click(function (event) {
        var _con = $('.tags-search-build');   // 设置目标区域
        if (!_con.is(event.target) && _con.has(event.target).length === 0) { // Mark 1
            //$('#divTop').slideUp('slow');   //滑动消失
            $('.tags-search-build').hide(500);          //淡出消失
        }
    });
    //点击人员 出现对勾
    $(".one-people").click(function () {
        $(this).find("i").toggle();

    });
    //点击空白区域 添加人员消失
    $(document).click(function (event) {
        var _con = $('.people ');   // 设置目标区域
        if (!_con.is(event.target) && _con.has(event.target).length === 0) { // Mark 1
            //$('#divTop').slideUp('slow');   //滑动消失
            $('.people ').hide(500);          //淡出消失
        }
    });

    //点击 添加附件
    $(".publish-bottom img:nth-of-type(1)").click(function () {
        $(".fujian-box").slideToggle();
    });
});