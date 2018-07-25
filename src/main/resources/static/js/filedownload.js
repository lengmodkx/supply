
/**
 * 追加关联字符串
 */
function addBindingStr(binding,type,bindId) {
    var content = "";
    if (type == '任务') {
        for (var i = 0; i < binding.length; i++) {
            content += '<li class="boxsizing" data-id="' + binding[i].taskId + '">' +
                '<div class="check-box" value="' + binding[i].taskName + '">';
            content += '<input type="checkbox" value = "' + binding[i].taskId + '" lay-filter="bindTask" name="" lay-skin="primary" disabled="disabled">';
            content += '</div>' +
                '<div class="related-rw-info">';
            if (binding[i].executor == '') {
                content += '<img src="/image/add.png">';
            } else {
                content += '<img src="' + IMAGE_SERVER + binding[i].executorInfo.userInfo.image + '">';
            }
            content += '<span>' + ' ' + binding[i].taskName + '</span>' +
                '</div>' +
                '<div class="related-rw-describe over-hidden">' + binding[i].project.projectName + '</div>' +
                '<i class="layui-icon layui-icon-down show-cancel-related" style="font-size: 16px; color: #a6a6a6;"></i>' +
                '<div class="related-menu" style="display: none">' +
                '<i class="layui-icon layui-icon-close close-related-menu" style="font-size: 20px; color: #a6a6a6;"></i>' +
                '<div class="related-menu-title">关联菜单</div>' +
                '<ul>' +
                // <!--<li class="boxsizing">-->
                // <!--<i class="layui-icon layui-icon-link" style="font-size: 16px; color: gray;"></i>-->
                // <!--<span>复制链接</span>-->
                // <!--</li>-->
                '<li class="boxsizing cancle" data-id="' + bindId[i] + '">' +
                '<i class="layui-icon layui-icon-about" style="font-size: 16px; color: gray;"></i>' +
                '<span>取消关联</span>' +
                '</li>' +
                '</ul>' +
                '</div>' +
                '</li>';
            $('.related-rw-wrap').show();
            $('.related-rw').prepend(content);
            var form = layui.form;
            form.render();
        }
    }
    if (type == '文件') {
        for (var i = 0; i < binding.length; i++) {
            content = '';
            content += '<li class="boxsizing" data-id = "' + binding[i].fileId + '">' +
                '<div class="related-wj-info">';
            if (binding[i].catalog == 1) {
                content += '<img class="folderFile" src="/image/nofile.png">';
            } else if (binding[i].catalog == 0 && (binding[i].ext == '.jpg' || binding[i].ext == '.png' || binding[i].ext == '.jpeg')) {

                content += '<img class="folderFile collect-item-touxiang" src="' + IMAGE_SERVER + binding[i].fileUrl + '"/>';
            } else if (binding[i].catalog == 0 && binding[i].ext == '..doc') {

                content += '<img class="folderFile collect-item-touxiang" src="/image/word_1.png" />';
            } else if (binding[i].catalog == 0 && binding[i].ext == '.xls') {

                content += '<img class="folderFile collect-item-touxiang" src="/image/excel.png" />';
            } else if (binding[i].catalog == 0 && binding[i].ext == '.xlsx') {

                content += '<img class="folderFile collect-item-touxiang" src="/image/excel.png" />';
            } else if (binding[i].catalog == 0 && binding[i].ext == '.pptx') {

                content += '<img class="folderFile collect-item-touxiang" src="/image/ppt.png" />';
            } else if (binding[i].catalog == 0 && binding[i].ext == '.ppt') {

                content += '<img class="folderFile collect-item-touxiang" src="/image/ppt.png" />';
            } else if (binding[i].catalog == 0 && binding[i].ext == '.pdf') {

                content += '<img class="folderFile collect-item-touxiang" src="/image/pdf_1.png" />';
            } else if (binding[i].catalog == 0 && binding[i].ext == '.zip') {

                content += '<img class="folderFile collect-item-touxiang" src="/image/zip.png" />';
            } else if (binding[i].catalog == 0 && binding[i].ext == '.rar') {

                content += '<img class="folderFile collect-item-touxiang" src="/image/rar.png" />';
            } else {
                content += '<img class="folderFile collect-item-touxiang" src="/image/defaultFile.png" />';
            }
            content += '<span>' + binding[i].fileName + '</span>' +
                '</div>' +
                '<div class="related-rw-describe over-hidden">' + binding[i].project.projectName + '</div>' +
                '<i class="layui-icon layui-icon-down show-cancel-related" style="font-size: 16px; color: #a6a6a6;"></i>' +
                '<div class="related-menu"  style="display: none">' +
                '<i class="layui-icon layui-icon-close close-related-menu" style="font-size: 20px; color: #a6a6a6;"></i>' +
                '<div class="related-menu-title" >' + '关联菜单' + '</div>' +
                '<ul>' +
                // '<!--<li class="boxsizing">-->'
                // <!--<i class="layui-icon layui-icon-link" style="font-size: 16px; color: gray;"></i>-->
                // <!--<span>复制链接</span>-->
                // <!--</li>-->
                '<li class="boxsizing cancle" data-id="' + bindId[i] + '">' +
                '<i class="layui-icon layui-icon-about" style="font-size: 16px; color: gray;"></i>' +
                '<span>取消关联</span>' +
                '</li>' +
                '</ul>' +
                '</div>' +
                '</li>';
            $('.related-wj-wrap').show();
            $('.related-wj').prepend(content);
            var form = layui.form;
            form.render();
        }
    }
    if (type == '日程') {
        for (var i = 0; i < binding.length; i++) {
            content = '';
            content += '<li class="boxsizing">' +
                '<div class="related-rc-top">' +
                '<div class="related-rc-info">' +
                '<i class="layui-icon layui-icon-date img-i" style="font-size: 16px; color: #a6a6a6;"></i>' +
                '<span>' + binding[i].scheduleName + '</span>' +
                '</div>' +
                '<div class="related-rw-describe over-hidden">' + binding[i].project.projectName + '</div>' +
                '<i class="layui-icon layui-icon-down show-cancel-related" style="font-size: 16px; color: #a6a6a6;"></i>' +
                '</div>' +
                '<div class="related-rc-down">' +
                '<span>' + new Date(binding[i].startTime).format('yyyy-MM-dd') + '</span>' +
                '<span>—</span>' +
                '<span>' + new Date(binding[i].endTime).format('yyyy-MM-dd') + '</span>' +
                '</div>' +
                '<div class="related-menu" style="display: none">' +
                '<i class="layui-icon layui-icon-close close-related-menu" style="font-size: 20px; color: #a6a6a6;"></i>' +
                '<div class="related-menu-title">关联菜单</div>' +
                '<ul>' +
                '<li class="boxsizing cancle" data-id="' + bindId[i] + '">' +
                '<i class="layui-icon layui-icon-about" style="font-size: 16px; color: gray;"></i>' +
                '<span>取消关联</span>' +
                '</li>' +
                '</ul>' +
                '</div>' +
                '</li>';
        }
        $('.related-rc-wrap').show();
        $('.related-rc').prepend(content);
        var form = layui.form;
        form.render();
    }
    if (type == '分享') {
        for (var i = 0; i < binding.length; i++) {
            content = '';
            content += '<li class="boxsizing">' +
                '<div class="related-rc-top">' +
                '<div class="related-rc-info">' +
                '<i class="layui-icon layui-icon-list img-i" style="font-size: 16px; color: #a6a6a6;"></i>' +
                '<img src="' + IMAGE_SERVER + binding[i].userEntity.userInfo.image + '">' +
                '<span>' + binding[i].title + '</span>' +
                '</div>' +
                '<div class="related-rw-describe over-hidden">' + binding[i].project.projectName + '</div>' +
                '<i class="layui-icon layui-icon-down show-cancel-related" style="font-size: 16px; color: #a6a6a6;"></i>' +
                '</div>' +
                '<div class="related-menu" style="display: none">' +
                '<i class="layui-icon layui-icon-close close-related-menu" style="font-size: 20px; color: #a6a6a6;"></i>' +
                '<div class="related-menu-title">关联菜单</div>' +
                '<ul>' +
                '<li class="boxsizing cancle" data-id="' + bindId[i] + '">' +
                '<i class="layui-icon layui-icon-about" style="font-size: 16px; color: gray;"></i>' +
                '<span>取消关联</span>' +
                '</li>' +
                '</ul>' +
                '</div>' +
                //     '<!--<div class="related-rc-down">-->'
                //     <!--<span>2018-12-25 12:00</span>-->'
                // <!--<span>—</span>-->
                // <!--<span>2018-12-25 12:00</span>-->
                // <!--</div>-->
                '</li>';
            $('.related-fx-wrap').show();
            $('.related-fw').prepend(content);
            var form = layui.form;
            form.render();
        }
    }
}

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
    $(".add-fuhao").click(function (e) {
        $(".tags-search-build").show();
        $(".tag-search").show();

        e.stopPropagation();
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