/**
 * 点击收藏的时候的触发事件
 */
$("#click-my-collect").click(function () {
    $("#my-collect").show().siblings().hide()
});

/**
 * 在收藏下点击任务的时候的方法
 */
$('.collect-head').click(function () {
    $(this).children('span').each(function () {
        if($(this).attr("class") == 'now'){
            var type = $(this).html();
            if(type == '任务'){
                var url = "/task/myCollectTask";
            }
            $.post(url,function(data){
                var collectTask = data.data;
                var content = '';
                if(collectTask != ''){
                    for(var i = 0;i < collectTask.length;i++){
                        content += '<li>'+
                        '<div class="collect-item-top">'+
                            '<span class="collect-item-top-date">' + new Date(collectTask[i].createTime).format('M月d日') + '</span>'+
                        '<span class="collect-item-top-remove">取消收藏</span>'+
                            '</div>'+
                            '<div class="collect-item-middle clearfix">'+
                            '<div class="collect-item-middle-img">'+
                            '<img src="/static/image/rcwhite.png">'+
                            '</div>'+
                            '<img class="collect-item-touxiang" src="'+IMAGE_SERVER+collectTask[i].memberImg+'">'+
                            '<p class="collect-item-name">' + collectTask[i].taskName + '</p>'+
                        '</div>'+
                        '</li>';
                    }
                    $('.rw-collect').html(content);
                }
            },"json");
        }
    });
});
