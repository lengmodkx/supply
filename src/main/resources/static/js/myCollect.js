/**
 * 点击收藏的时候的触发事件
 */
$("#click-my-collect").click(function () {
    $("#my-collect").show().siblings().hide()
});

/**
 * 取消收藏任务
 */
$("html").on("click",".collect-item-top-remove",function () {
    var that = $(this).parent().parent();
    var id = $(this).parent().parent().attr("data-id");
    $('.collect-head').children('span').each(function () {
        if($(this).attr('class') == 'now'){
            if($(this).html() == '任务'){
                var url = '/public/cancelCollectTask';
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
            }
        }
    })
});

/**
 * 在收藏下点击任务的时候的方法
 */
$('.collect-head').click(function () {
    $('.rc-collect').hide();
    $(this).children('span').each(function () {
        if($(this).attr("class") == 'now'){
            var type = $(this).html();
            if(type == '任务'){
                var url = "/public/myCollectTask";
            }
            $.post(url,function(data){
                var collectTask = data.data;
                var content = '';
                if(collectTask != ''){
                    for(var i = 0;i < collectTask.length;i++){
                        content += '<li data-id="' + collectTask[i].id + '">'+
                        '<div class="collect-item-top">'+
                            '<span class="collect-item-top-date">' + new Date(collectTask[i].createTime).format('M月d日') + '</span>'+
                        '<span class="collect-item-top-remove">取消收藏</span>'+
                            '</div>'+
                            '<div class="collect-item-middle clearfix">'+
                            '<div class="collect-item-middle-img">'+
                            '<img src="/image/rw-white.png">'+
                            '</div>'+
                            '<img class="collect-item-touxiang" src="'+IMAGE_SERVER+collectTask[i].memberImg+'">'+
                            '<p class="collect-item-name">' + collectTask[i].memberName + '</p>'+
                        '</div>'+
                        '</li>';
                    }
                    $('.rw-collect').html(content);
                } else{
                    $('.no-collect').show();
                }
            },"json");
        }
    });
});
