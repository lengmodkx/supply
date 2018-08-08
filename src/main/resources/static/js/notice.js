layui.use('laydate', function () {
    var laydate = layui.laydate;
    laydate.render({
        elem: '#beginTimes',
        type: 'datetime',
        format: 'yyyy-MM-dd H点',
        value: '2018-08-01 ',
        isInitValue: true
        // ,done: function(value, date, endDate){
        //     var taskId = $('#taskId').val();
        //     var startTime = new Date(value.toString()).getTime();
        //     var args = {"taskId":taskId,"startTime":startTime};
        //     if(value == ''){
        //         var url = "/task/removeTaskStartTime";
        //         $.post(url,{taskId:taskId},function (data) {
        //             //完成
        //         })
        //     } else{
        //         var url = "/task/updateTaskStartAndEndTime";
        //         $.post(url,args,function(data){
        //             if(data.result == 1){
        //                 getLog(data.taskLog);
        //             }
        //         },"json");
        //
        //     }
        //     //console.log(value); //得到日期生成的值，如：2017-08-18
        //     //console.log(date); //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
        //     //console.log(endDate); //得结束的日期时间对象，开启范围选择（range: true）才会返回。对象成员同上。
        // }
    });
})






/**
 * 点击一条消息
 */
$("html").on("click",".notice-ul li",function () {
    var publicId =$(this).attr("data-public-id");
    var type = $('.newsType').val();
    $(this).addClass("isread");
    $(this).find('.num').remove();
    var url = "/news/updateIsRead";
    var args = {"id":$(this).attr("data-id"),"publicId":publicId,"publicType":type,"isread":$(this).find('.read').val()};
    $.post(url,args,function (data) {
        if(data.result == 0){
            layer.msg(data.msg);
        } else{
            info(publicId,type);

            if(data.newsCount === 0){
                $('.notice-num').remove();
            } else{
                $('.notice-num').html(data.newsCount);
            }
        }
    });
});

/**
 * 点击一条消息上的x
 */
$('.layui-icon-close').click(function (e) {
    var url = "/news/removeNews";
    var args = {"id":$(this).parents('li').attr('data-id')};
    var that=$(this)
    $.post(url,args,function (data) {
        if(data.result > 0){
            that.parents('li').slideUp(300,function () {
                that.parents('li').remove()
            })
            if(data.data === 0){
                $('.notice-num').remove();
            } else{
                $('.notice-num').html(data.data);
            }
        } else{
            layer.msg(data.msg);
        }
    });
    e.stopPropagation();
});

function info(publicId,publicType){
    var content = '';
    if(publicType === '任务'){
        content = "/task/initTask.html?taskId="+publicId;
    }
    if(publicType === '文件'){
        content = "/file/fileDetail.html?fileId="+publicId;
    }
    if(publicType === '日程'){
        content = "/schedule/editSchedule.html?id="+publicId;
    }
    $(".scroll-box>div").hide(10);
    $(".scroll-box>div").eq(0).show(11);

    var otop=$(".scroll-box").offset().top+'px';
    var oleft=$(".scroll-box").offset().left+'px';
    layui.use('layer',function (top,left) {
        var layer=layui.layer;
        layer.closeAll('iframe');
        layer.open({
            type:2,
            title:false,
            area:['600px','600px'],
            offset:[otop,oleft],
            shade: 0,
            anim: 5,
            closeBtn: 0,
            content:content,
            success: function(layero, index){
             var f='#'+layero.find('iframe')[0].id;
             $(f).contents().find('.close-revise-task').hide()
            }
        })
    });
}

