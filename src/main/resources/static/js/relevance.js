
$(function () {
    $(".close-tk").click(function () {
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });
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
    });

    // 任务 界面
    // 点击 ，显示后面的
    $("html").on("click",".show-next .style-li",function () {
        $(this).parent().siblings(".task-ul").show();
        $(this).parent().siblings(".task-ul").append(' <li class="show-next-li">\n' +
            '                                <div class="title-create-style boxsizing">\n' +
            '                                    <i class="layui-icon layui-icon-add-circle" style="font-size: 20px; color: #3AA7F5;"></i>\n' +
            '                                    <span>创建新任务</span>\n' +
            '                                </div>\n' +
            '                                <ul class="a-task-box ">\n' +
            '                                    <li class="group-name">快速入门</li>\n' +
            '                                    <li class="a-task-li selected">\n' +
            '                                        <span></span>\n' +
            '                                        <img src="../static/image/person.png" th:src="@{/image/person.png}">\n' +
            '                                        <p class="over-hidden">这是一条任务名称</p>\n' +
            '                                    </li>\n' +
            '                                    <li class="a-task-li">\n' +
            '                                        <span></span>\n' +
            '                                        <img src="../static/image/person.png" th:src="@{/image/person.png}">\n' +
            '                                        <p class="over-hidden">这是一条任务名称</p>\n' +
            '                                    </li>\n' +
            '                                </ul>\n' +
            '                            </li>')
    });
    $("html").on("click",".show-next-li .a-task-li",function (){
        $(this).parents(".show-next-li").nextAll().remove();
        $(this).parents(".show-next-li").after(' <li class="show-next-li">\n' +
            '                                <div class="title-create-style boxsizing">\n' +
            '                                    <i class="layui-icon layui-icon-add-circle" style="font-size: 20px; color: #3AA7F5;"></i>\n' +
            '                                    <span>创建子任务</span>\n' +
            '                                </div>\n' +
            '                                <ul class="a-task-box">\n' +
            '                                    <li class="group-name">子任务</li>\n' +
            '                                    <li class="a-task-li">\n' +
            '                                        <span></span>\n' +
            '                                        <img src="../static/image/person.png" th:src="@{/image/person.png}">\n' +
            '                                        <p class="over-hidden">这是一条任务名称</p>\n' +
            '                                    </li>\n' +
            '                                </ul>\n' +
            '                            </li>');
        $(this).parents(".scroll-box-heng").scrollLeft(parseInt( $(this).parents(".task-ul").width()));

    });

    // 文件 页面
    // 点击 显示 后面 的内容
    $("html").on("click",".show-next-wj .wj-li",function () {
        $(this).parents(".show-next-wj").siblings(".task-ul").show();
        $(this).parents(".show-next-wj").siblings(".task-ul").html("");
        $(this).parents(".show-next-wj").siblings(".task-ul").append(' <li class="show-next-wj-li">\n' +
            '                                <div class="paper-file">\n' +
            '                                    <div class="up-add">\n' +
            '                                        <i class="layui-icon layui-icon-upload-circle" style="font-size: 20px; color: gray;"></i>\n' +
            '                                        <i class="layui-icon layui-icon-add-circle" style="font-size: 20px; color: gray;"></i>\n' +
            '                                    </div>\n' +
            '                                    <div class="wjj-wrap">\n' +
            '                                        <p>文件夹</p>\n' +
            '                                        <ul>\n' +
            '                                            <li class="wj-li over-hidden">\n' +
            '                                                <img src="../static/image/wjj-b.png" th:src="@{/image/wjj-b.png}">\n' +
            '                                                <span >123132</span>\n' +
            '                                            </li>\n' +
            '                                        </ul>\n' +
            '                                    </div>\n' +
            '                                    <div class="wjj-wrap">\n' +
            '                                        <p>文件</p>\n' +
            '                                        <ul>\n' +
            '                                            <li class="wj-li over-hidden">\n' +
            '                                                <img src="../static/image/wjj-b.png" th:src="@{/image/wjj-b.png}">\n' +
            '                                                <span >123132</span>\n' +
            '                                            </li>\n' +
            '                                        </ul>\n' +
            '                                    </div>\n' +
            '                                </div>\n' +
            '\n' +
            '                            </li>')
    });
    $("html").on("click",".show-next-wj-li .wj-li",function () {
        $(this).parents(".show-next-wj-li").nextAll().remove();
        $(this).parents(".show-next-wj-li").after(' <li class="show-next-wj-li">\n' +
            '                                <div class="paper-file">\n' +
            '                                    <div class="up-add">\n' +
            '                                        <i class="layui-icon layui-icon-upload-circle" style="font-size: 20px; color: gray;"></i>\n' +
            '                                        <i class="layui-icon layui-icon-add-circle" style="font-size: 20px; color: gray;"></i>\n' +
            '                                    </div>\n' +
            '                                    <div class="wjj-wrap">\n' +
            '                                        <p>文件夹</p>\n' +
            '                                        <ul>\n' +
            '                                            <li class="wj-li over-hidden">\n' +
            '                                                <img src="../static/image/wjj-b.png" th:src="@{/image/wjj-b.png}">\n' +
            '                                                <span >123132</span>\n' +
            '                                            </li>\n' +
            '                                        </ul>\n' +
            '                                    </div>\n' +
            '                                    <div class="wjj-wrap">\n' +
            '                                        <p>文件</p>\n' +
            '                                        <ul>\n' +
            '                                            <li class="wj-li over-hidden">\n' +
            '                                                <img src="../static/image/wjj-b.png" th:src="@{/image/wjj-b.png}">\n' +
            '                                                <span >123132</span>\n' +
            '                                            </li>\n' +
            '                                        </ul>\n' +
            '                                    </div>\n' +
            '                                </div>\n' +
            '                            </li>');
        $(this).parents(".scroll-box-heng").scrollLeft(parseInt( $(this).parents(".task-ul").width()));

    });


});