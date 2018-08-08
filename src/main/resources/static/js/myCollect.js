/**
 * 点击收藏的触发事件
 */
$('#click-my-collect').click(function () {
    var url = "/public/myCollect";
    $.post(url, function (data) {
        var collect = data.data;
        var content = '';
        if (collect != '') {
            $('.no-collect').hide();
            appendStr(collect);
        } else {
            $('.rw-collect').html('');
            $('.no-collect').show();
        }
    }, "json");
});
/**
 * 点击收藏的时候的触发事件
 */
$("#click-my-collect").click(function () {
    $("#my-collect").show().siblings().hide()
});

/**
 * 取消收藏
 */
$("body").on("click",".collect-item-top-remove",function (e) {
    var that = $(this).parent().parent();
    var id = $(this).parent().parent().attr("data-id");
    $('.collect-head').children('span').each(function () {
        var url = '/public/cancelCollect';
        var args = {"publicCollectId":id};
        $.post(url,args,function (data) {
            if(data.result > 0){
                that.remove();
                var count = $('.rw-collect li').length;
                if(count == 0){
                    $('.no-collect').show();
                }
            }
        },"json");
    })
    e.stopPropagation();
});

/**
 * 在收藏下点击任务的时候的方法
 */
$('.collect-head').click(function () {
    $('.rc-collect').hide();
    $(this).children('span').each(function () {
        if($(this).attr("class") == 'now'){
            var type = $(this).html();
            var args = {"type":type};
            var url = '/public/myCollect';
            $.post(url,args,function(data){
                var collect = data.data;
                var content = '';
                if(collect != ''){
                    $('.no-collect').hide();
                    appendStr(collect);
                } else{
                    $('.rw-collect').html('');
                    $('.no-collect').show();
                }
            },"json");
        }
    });
});


/**
 * 点击收藏 展示收藏详情
 */
$("html").on("click",".rw-collect li",function () {
    if($(this).attr("data-name") == '任务'){
        changeRenwu($(this).attr("data-public-id"),null);
    }
    if($(this).attr("data-name") == '文件'){
        location.href = "/file/fileDetail.html?fileId="+$(this).attr("data-public-id");
    }
});

//修改任务 弹框界面
function changeRenwu(taskId,projectId) {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: '20px',
            area:['600px','600px'],
            fixed: false,
            shadeClose: true, //点击遮罩关闭
            closeBtn: 0,
            anim: 1,  //动画 0-6
            content: "/task/initTask.html?taskId=" + taskId
        });
    });
}

/**
 * 拼接字符串
 * @param list 数据
 * @param type 查询的类型
 */
function appendStr(list) {
    var content = '';
    for (var i = 0; i < list.length; i++) {
        if(list[i].collectType == '任务'){
            content += '<li data-id="' + list[i].id + '" data-name="' + list[i].collectType + '" data-public-id = "' + list[i].task.taskId + '">' +
                '<div class="collect-item-top">' +
                '<span class="collect-item-top-date">' + new Date(list[i].task.createTime).format('M月d日') + '</span>' +
                '<span class="collect-item-top-remove">取消收藏</span>' +
                '</div>' +
                '<div class="collect-item-middle clearfix">' +
                '<div class="collect-item-middle-img">' +
                '<img src="/image/rw-white.png">' +
                '</div>' +
                '<img class="collect-item-touxiang" src="' + IMAGE_SERVER + list[i].task.creatorInfo.userInfo.image+ '">' +
                '<p class="collect-item-name">' + list[i].task.creatorInfo.userName + '</p>' +
                '</div>' +
                '</li>';
        }
        if(list[i].collectType == '分享'){
            content += '<li data-id = "' + list[i].id + '" data-name = "' + list[i].collectType + '" data-public-id = "' + list[i].share.id + '">'+
            '<div class="collect-item-top">'+
                '<span class="collect-item-top-date">' + new Date(list[i].share.createTime).format('M月d日') + '</span>'+
            '<span class="collect-item-top-remove">取消收藏</span>'+
                '</div>'+
                '<div class="collect-item-middle clearfix">'+
                '<div class="collect-item-middle-img">'+
                '<img src="/image/rcwhite.png" src="' + list[i].share.userEntity.userInfo.image + '">'+
                '</div>'+
                '<p class="collect-item-name">' + list[i].share.title + '</p >'+
                '</div>'+
                '<div class="collect-item-bottom">'+
                '<span>' + list[i].share.content + '</span>'+
            '</div>'+
            '</li>';
        }
        if(list[i].collectType == '日程'){
            content += '<li data-id="' + list[i].id + '" data-name ="' + list[i].collectType + '" data-public-id = "' + list[i].schedule.scheduleId + '">' +
                '<div class="collect-item-top">' +
                '<span class="collect-item-top-date">' + new Date(list[i].schedule.createTime).format('M月d日') + '</span>' +
                '<span class="collect-item-top-remove">取消收藏</span>' +
                '</div>' +
                '<div class="collect-item-middle clearfix">' +
                '<div class="collect-item-middle-img">' +
                '<img src="/image/rc-white.png">' +
                '</div>' +
                '<img class="collect-item-touxiang" src="' + IMAGE_SERVER + list[i].schedule.userEntity.userInfo.image + '">' +
                '<p class="collect-item-name">' + list[i].schedule.scheduleName + '</p>' +
                '</div>' +
                '</li>';
        }
        if(list[i].collectType == '文件'){
            content += '<li data-id="' + list[i].id + '" data-name ="' + list[i].collectType + '" data-public-id = "' + list[i].file.fileId+ '">' +
                '<div class="collect-item-top">' +
                '<span class="collect-item-top-date">' + new Date(list[i].file.createTime).format('M月d日') + '</span>' +
                '<span class="collect-item-top-remove">取消收藏</span>' +
                '</div>' +
                '<div class="collect-item-middle clearfix">' +
                '<div class="collect-item-middle-img">' +
                '<img src="/image/rw-white.png">' +
                '</div>';
                if(list[i].file.catalog == 1){
                    content += '<img style="transform: scale(1.3)" class="folderFile" src="/image/nofile.png">';
                }
                if(list[i].file.catalog == 0 && (list[i].file.ext == '.jpg' || list[i].file.ext == '.png' || list[i].file.ext == '.jpeg')){
                    content += '<img style="transform: scale(1.3)" class="folderFile collect-item-touxiang" src="' + IMAGE_SERVER + list[i].file.fileUrl + '"/>';
                }
                if(list[i].file.catalog == 0 && list[i].file.ext == '..doc'){
                    content += '<img style="transform: scale(1.3)" class="folderFile collect-item-touxiang" src="/image/word_1.png" />';
                }
                if(list[i].file.catalog == 0 && list[i].file.ext == '.docx'){
                    content += '<img style="transform: scale(1.3)" class="folderFile collect-item-touxiang" src="/image/word_1.png" />';
                }
                if(list[i].file.catalog == 0 && list[i].file.ext == '.xls'){
                    content += '<img style="transform: scale(1.3)" class="folderFile collect-item-touxiang" src="/image/excel.png" />';
                }
                if(list[i].file.catalog == 0 && list[i].file.ext == '.xlsx'){
                    content += '<img style="transform: scale(1.3)" class="folderFile collect-item-touxiang" src="/image/excel.png" />';
                }
                if(list[i].file.catalog == 0 && list[i].file.ext == '.pptx'){
                    content += '<img style="transform: scale(1.3)" class="folderFile collect-item-touxiang" src="/image/ppt.png" />';
                }
                if(list[i].file.catalog == 0 && list[i].file.ext == '.ppt'){
                    content += '<img style="transform: scale(1.3)" class="folderFile collect-item-touxiang" src="/image/ppt.png" />';
                }
                if(list[i].file.catalog == 0 && list[i].file.ext == '.pdf'){
                    content += '<img style="transform: scale(1.3)" class="folderFile collect-item-touxiang" src="/image/pdf_1.png" />';
                }
                if(list[i].file.catalog == 0 && list[i].file.ext == '.zip'){
                    content += '<img style="transform: scale(1.3)" class="folderFile collect-item-touxiang" src="/image/zip.png" />';
                }
                if(list[i].file.catalog == 0 && list[i].file.ext == '.rar'){
                    content += '<img style="transform: scale(1.3)" class="folderFile collect-item-touxiang" src="/image/rar.png" />';
                }
                content +=
                '<p class="collect-item-name"><span>' + list[i].file.userEntity.userName + '</span> <span style="color: gray;">(' + list[i].file.size + ')</span></p>' +
                '</div>' +
                '</li>';
        }
        $('.rw-collect').html(content);
    }
}
