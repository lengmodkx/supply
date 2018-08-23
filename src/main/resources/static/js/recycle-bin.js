Date.prototype.format = function(format)
{
    var o = {
        "M+" : this.getMonth()+1, //month
        "d+" : this.getDate(),    //day
        "h+" : this.getHours(),   //hour
        "m+" : this.getMinutes(), //minute
        "s+" : this.getSeconds(), //second
        "q+" : Math.floor((this.getMonth()+3)/3),  //quarter
        "S" : this.getMilliseconds() //millisecond
    }
    if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
        (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in o)if(new RegExp("("+ k +")").test(format))
        format = format.replace(RegExp.$1,
            RegExp.$1.length==1 ? o[k] :
                ("00"+ o[k]).substr((""+ o[k]).length));
    return format;
}

layui.use('form', function(){
    var form = layui.form;
});
$(".left-nav .menu").click(function () {
    $(this).addClass("now").siblings().removeClass("now")
})


var oli;
$("html").on("click",".huifu-btn",function (e) {
    var type = $('.menu.now').children('span').html();
     oli=$(this).parents("li").attr("data-id");

    var top=$(this).offset().top+50+'px';
    var left=$(this).offset().left-100+'px';

    var that;
    if(type === '任务'){
        that = $(".recover-task");
    }
    if(type === '文件'){
        that = $(".recover-file");
    }
    if(type === '日程'){
        that = $(".recover-schedule");
    }
    if(type === '分享'){
        that = $(".recover-share");
    }
    layui.use('layer', function(){

        var layer = layui.layer;
        layer.open({
            type: 1,
            title: false,
            offset: [top, left],
            closeBtn: 0,
            shade: [0.1, '#fff'],
            // shade: 0,
            shadeClose:true,
            content: that
        });
    });
    e.stopPropagation();
});

/**
 * 点击彻底删除按钮
 */
$("html").on("click",".delete-btn",function (e) {
    var type = $('.menu.now').children('span').html();
    oli=$(this).parents("li").attr("data-id");
    var that;
    if(type === '任务'){
        that = $(".delete-task");
    }
    if(type === '文件'){
        that = $(".delete-file");
    }
    if(type === '日程'){
        that = $(".delete-schedule");
    }
    if(type === '分享'){
        that = $(".delete-share");
    }
    var top=$(this).offset().top+50+'px';
    var left=$(this).offset().left-180+'px';
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 1,
            title: false,
            offset: [top, left],
            closeBtn: 0,
            shade: [0.1, '#fff'],
            // shade: 0,
            shadeClose:true,
            content: that
        });
    });
    e.stopPropagation();
});


$(".close-tk").click(function () {
    layer.closeAll('page'); //关闭所有页面层
})

/**
 * 点击左侧选项卡的时候
 */
$('.menu').click(function () {
    $('#data').html('');
    var args = {"projectId" : projectId, "type" : $(this).children('span').html()};
    var url = "/project/recycleBinInfo";
    $.post(url,args,function (data) {
        var info = data.data;
        if(info == null || info.length == 0){
            $('.have-content').hide();
            $('.no-content').show();
            return false;
        } else{
            var content = '';
            for(var i = 0;i < info.length;i++){
                content += '<li class="boxsizing" data-id = "' + info[i].id + '">'+
                    '<div class="name over-hidden">' + info[i].name + '</div>'+
                    '<div class="right-time">'+
                    '<div class="time">' + new Date(info[i].updateTime).format('M月-dd日 h:mm') + '</div>'+
                    '<div class="hover-show-box">'+
                    '<div class="box huifu-btn">'+
                    '<i class="iconfont icon-iconhuifu"></i>'+
                    '<span>恢复内容</span>'+
                    '</div>'+
                    '<div class="box delete-btn">'+
                    '<i class="iconfont icon-lajixiang"></i>'+
                    '<span>彻底删除</span>'+
                    '</div>'+
                    '</div>'+
                    '</div>'+
                    '</li>';
            }
            $('.have-content').show();
            $('.no-content').hide();
        }
        $('#data').html(content);
    });
});

/**
 * 点击回收站中的其中一项的时候
 */
$('html').on('click','#data li',function () {

    var id =  $(this).attr('data-id');
    var type = $('.menu.now').children('span').html();
    var args = '';
    if(type === '任务'){
        changeRenwu(id,projectId);
    }
    if(type === '文件'){
        window.open("/file/fileDetail.html?fileId="+id,"在线预览文件");
    }
    if(type === '分享'){
        changeShare(id);
    }
    if(type === '日程'){
        changeRicheng(id,projectId);
    }
});

//修改任务 弹框界面
function changeRenwu(taskId,projectId) {
    layui.use('layer', function(){
        var layer = layui.layer;
        parent.layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: '20px',
            area:['600px','600px'],
            fixed: false,
            shadeClose: true, //点击遮罩关闭
            closeBtn: 0,
            anim: 1,  //动画 0-6
            content: "/task/initTask.html?taskId="+ taskId + "&projectId=" + projectId
        });
    });
}

//修改日程 弹框界面
function changeRicheng(scheduleId,projectId) {
    layui.use('layer', function(){
        var layer = layui.layer;
        parent.layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: '20px',
            area:['600px','600px'],
            fixed: false,
            shadeClose: true, //点击遮罩关闭
            closeBtn: 0,
            anim: 1,  //动画 0-6
            content: "/schedule/editSchedule.html?id="+ scheduleId + "&projectId=" + projectId
        });
    });
}

//分享详情 弹框界面
function changeShare(shareId) {
    layui.use('layer', function(){
        var layer = layui.layer;
        parent.layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: '20px',
            area:['600px','500px'],
            fixed: false,
            shadeClose: true, //点击遮罩关闭
            closeBtn: 0,
            anim: 1,  //动画 0-6
            content: "/share/shareInfo.html?shareId="+shareId
        });
    });
}

/**
 * 恢复任务的确定按钮
 */
$('.o-task-btn').click(function () {
    var url = "/task/recoveryTask";
    var args = {"projectId":projectId,"taskId":oli,"menuId":$('select[name = "menu"]').val()};
    $.post(url,args,function (data) {
        if(data.result < 1){
            layer.msg("系统异常,操作失败!");
        } else{
            layer.close(layer.index);
        }
    });
});

/**
 * 恢复分享的确定按钮
 */
$('.o-task-btn').click(function () {
    var url = "/share/recoveryShare";
    var args = {"projectId":projectId,"taskId":oli,"menuId":$('select[name = "menu"]').val()};
    $.post(url,args,function (data) {
        if(data.result < 1){
            layer.msg("系统异常,操作失败!");
        } else{
            layer.close(layer.index);
        }
    });
});

/**
 * 恢复日程的确定按钮
 */
$('.o-schedule-btn').click(function () {
    var url = "/schedule/recoverySchedule";
    var args = {"projectId":projectId,"scheduleId":oli};
    $.post(url,args,function (data) {
        if(data.result < 1){
            layer.msg("系统异常,操作失败!");
        } else{
            layer.close(layer.index);
        }
    });
});


/**
 * 恢复文件的确定按钮
 */
$('.o-file-btn').click(function () {
    var url = "/file/recoveryFile";
    var args = {"projectId":projectId,"fileId":oli};
    $.post(url,args,function (data) {
        if(data.result < 1){
            layer.msg("系统异常,操作失败!");
        } else{
            layer.close(layer.index);
        }
    });
});

/**
 * 删除按钮
 */
$('.forever-delete-btn').click(function () {
    var type = $('.menu.now').children('span').html();
    var url = '';
    if(type === '任务'){
        url = '/task/delTask';
        args = {"taskId":oli,"projectId":projectId}
    }
    if(type === '文件'){
       url = '/file/deleteFile';
        args = {"fileId":oli,"projectId":projectId}
    }
    if(type === '分享'){
        url = '/share/shareDelete';
        args = {"shareId":oli,"projectId":projectId}
    }
    if(type === '日程'){
        url = '/schedule/deleteSchedule';
        args = {"id":oli,"projectId":projectId}
    }
    $.post(url,args,function (data) {
        if(data.result == 0){
            layer.msg(data.msg);
        } else{
            layer.close(layer.index);
        }
    });
});