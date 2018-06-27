$(function () {
    // 点击添加标签
    $(".no-tags").click(function (e) {
        var url = "/task/findAllTags";
        var args = {"projectId":projectId};
        //异步请求获取项目下的所有标签
        $.post(url,args,function(data){
            var tags = data.data;
            var content = "";
            for(var i = 0;i < tags.length; i++){
                content += "<li class='tags-list'>" +
                    "<span class='dot'></span>" +
                    "<span class='tag-font' th:value='"+ tags[i].tagId +"'>"+ tags[i].tagName +"</span>"+
                    "</li>";
            }
            $('#tags').html(content);
        },"json");
        $(".tags-search-build").show();
        $(".tag-search").show();
        $(".no-tags").hide();
        e.stopPropagation();
    });
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
        $(".no-tags").show();
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
        $(".has-tags").prepend('<span class="tag">\n' +
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
            $(".no-tags").show();
        } else {
            $(".has-tags").show();
            $(".no-tags").hide();
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