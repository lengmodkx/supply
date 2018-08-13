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
layui.use('laydate', function(){
    var laydate = layui.laydate;
    laydate.render({
        elem: '#test1',
        type:'datetime',
        format: 'yyyy-MM-dd HH:00:00', //可任意组合
        done: function(value, date, endDate){
            console.log(value); //得到日期生成的值，如：2017-08-18
            // console.log(date); //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
            // console.log(endDate); //得结束的日期时间对象，开启范围选择（range: true）才会返回。对象成员同上。
            var url = "/schedule/updateScheduleStartAndEndTime";
            var args = {"scheduleId":scheduleId,"startTime":value};
            $.post(url,args,function(data){

            });
        }
    });
    laydate.render({
        elem: '#test2',
        type:'datetime',
        format: 'yyyy-MM-dd HH:00:00', //可任意组合
        done: function(value, date, endDate){
            // console.log(value); //得到日期生成的值，如：2017-08-18
            // console.log(date); //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
            // console.log(endDate); //得结束的日期时间对象，开启范围选择（range: true）才会返回。对象成员同上。
            var startTime = value;
            var url = "/schedule/updateScheduleStartAndEndTime";
            var args = {"scheduleId":scheduleId,"endTime":value};
            $.post(url,args,function(data){

            });
        }
    });

});


/**
 * 追加关联字符串
 */
function addBindingStr(binding,type,bindId){
    var content = "";
    if(type == '任务'){
        for(var i = 0;i < binding.length;i++){
            content += '<li class="boxsizing data-info" data-id="' + binding[i].taskId + '">'+
                '<div class="check-box" value="' + binding[i].taskName + '">';
            content += '<input type="checkbox" value = "' + binding[i].taskId + '" lay-filter="bindTask" name="" lay-skin="primary" disabled="disabled">';
            content += '</div>'+
                '<div class="related-rw-info">';
            if(binding[i].executor == ''){
                content += '<img src="/image/add.png">';
            } else{
                content += '<img src="' + IMAGE_SERVER + binding[i].executorInfo.userInfo.image+ '">';
            }
            content += '<span>'+' '+ binding[i].taskName + '</span>'+
                '</div>'+
                '<div class="related-rw-describe over-hidden">' + binding[i].project.projectName  + '</div>'+
                '<i class="layui-icon layui-icon-down show-cancel-related" style="font-size: 16px; color: #a6a6a6;"></i>'+
                '<div class="related-menu" style="display: none">'+
                '<i class="layui-icon layui-icon-close close-related-menu" style="font-size: 20px; color: #a6a6a6;"></i>'+
                '<div class="related-menu-title">关联菜单</div>'+
                '<ul>'+
                // <!--<li class="boxsizing">-->
                // <!--<i class="layui-icon layui-icon-link" style="font-size: 16px; color: gray;"></i>-->
                // <!--<span>复制链接</span>-->
                // <!--</li>-->
                '<li class="boxsizing cancle" data-id="' + scheduleId + '" data-binding-id="' + binding[i].taskId + '">'+
                '<i class="layui-icon layui-icon-about" style="font-size: 16px; color: gray;"></i>'+
                '<span>取消关联</span>'+
                '</li>'+
                '</ul>'+
                '</div>'+
                '</li>';
            $('.related-rw-wrap').show();
            $('.related-rw').prepend(content);
            var form = layui.form;
            form.render();
        }
    }
    if(type == '文件'){
        for(var i = 0;i < binding.length;i++){
            content = '';
            content += '<li class="boxsizing data-info" data-id = "' + binding[i].fileId + '">'+
                '<div class="related-wj-info">';
            if(binding[i].catalog == 1){
                content += '<img class="folderFile" src="/image/nofile.png">';
            } else if(binding[i].catalog == 0 && (binding[i].ext == '.jpg' || binding[i].ext == '.png' || binding[i].ext == '.jpeg')){

                content += '<img class="folderFile collect-item-touxiang" src="' + IMAGE_SERVER + binding[i].fileUrl + '"/>';
            } else if(binding[i].catalog == 0 && binding[i].ext == '..doc'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/word_1.png" />';
            } else if(binding[i].catalog == 0 && binding[i].ext == '.xls'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/excel.png" />';
            } else if (binding[i].catalog == 0 && binding[i].ext == '.xlsx'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/excel.png" />';
            } else if(binding[i].catalog == 0 && binding[i].ext == '.pptx'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/ppt.png" />';
            } else if(binding[i].catalog == 0 && binding[i].ext == '.ppt'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/ppt.png" />';
            } else if(binding[i].catalog == 0 && binding[i].ext == '.pdf'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/pdf_1.png" />';
            } else if(binding[i].catalog == 0 && binding[i].ext == '.zip'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/zip.png" />';
            } else if(binding[i].catalog == 0 && binding[i].ext == '.rar'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/rar.png" />';
            } else {
                content += '<img class="folderFile collect-item-touxiang" src="/image/defaultFile.png" />';
            }
            content += '<span>' + binding[i].fileName + '</span>'+
                '</div>'+
                '<div class="related-rw-describe over-hidden">' + binding[i].project.projectName + '</div>'+
                '<i class="layui-icon layui-icon-down show-cancel-related" style="font-size: 16px; color: #a6a6a6;"></i>'+
                '<div class="related-menu"  style="display: none">'+
                '<i class="layui-icon layui-icon-close close-related-menu" style="font-size: 20px; color: #a6a6a6;"></i>'+
                '<div class="related-menu-title" >'+'关联菜单'+'</div>'+
                '<ul>'+
                // '<!--<li class="boxsizing">-->'
                // <!--<i class="layui-icon layui-icon-link" style="font-size: 16px; color: gray;"></i>-->
                // <!--<span>复制链接</span>-->
                // <!--<span>复制链接</span>-->
                // <!--</li>-->
                '<li class="boxsizing cancle" data-id="' + scheduleId + '" data-binding-id="' + binding[i].fileId + '">'+
                '<i class="layui-icon layui-icon-about" style="font-size: 16px; color: gray;"></i>'+
                '<span>取消关联</span>'+
                '</li>'+
                '</ul>'+
                '</div>'+
                '</li>';
            $('.related-wj-wrap').show();
            $('.related-wj').prepend(content);
            var form = layui.form;
            form.render();
        }
    }
    if(type == '日程'){
        for(var i = 0;i < binding.length;i++){
            content = '';
            content += '<li class="boxsizing data-info" data-id="' + binding[i].scheduleId + '">'+
                '<div class="related-rc-top">'+
                '<div class="related-rc-info">'+
                '<i class="layui-icon layui-icon-date img-i" style="font-size: 16px; color: #a6a6a6;"></i>'+
                '<span>' + binding[i].scheduleName + '</span>'+
                '</div>'+
                '<div class="related-rw-describe over-hidden">' + binding[i].project.projectName + '</div>'+
                '<i class="layui-icon layui-icon-down show-cancel-related" style="font-size: 16px; color: #a6a6a6;"></i>'+
                '</div>'+
                '<div class="related-rc-down">'+
                '<span>' + new Date(binding[i].startTime).format('yyyy-MM-dd') + '</span>'+
                '<span>—</span>'+
                '<span>' + new Date(binding[i].endTime).format('yyyy-MM-dd') + '</span>'+
                '</div>'+
                '<div class="related-menu" style="display: none">'+
                '<i class="layui-icon layui-icon-close close-related-menu" style="font-size: 20px; color: #a6a6a6;"></i>'+
                '<div class="related-menu-title">关联菜单</div>'+
                '<ul>'+
                '<li class="boxsizing cancle" data-id="' + scheduleId + '" data-binding-id="' + binding[i].scheduleId + '">'+
                '<i class="layui-icon layui-icon-about" style="font-size: 16px; color: gray;"></i>'+
                '<span>取消关联</span>'+
                '</li>'+
                '</ul>'+
                '</div>'+
                '</li>';
        }
        $('.related-rc-wrap').show();
        $('.related-rc').prepend(content);
        var form = layui.form;
        form.render();
    }
    if(type == '分享'){
        for(var i = 0;i < binding.length;i++){
            content = '';
            content += '<li class="boxsizing data-info" data-id="'+ binding[i].id +'">'+
                '<div class="related-rc-top">'+
                '<div class="related-rc-info">'+
                '<i class="layui-icon layui-icon-list img-i" style="font-size: 16px; color: #a6a6a6;"></i>'+
                '<img src="' + IMAGE_SERVER + binding[i].userEntity.userInfo.image + '">'+
                '<span>' + binding[i].title + '</span>'+
                '</div>'+
                '<div class="related-rw-describe over-hidden">' + binding[i].project.projectName + '</div>'+
                '<i class="layui-icon layui-icon-down show-cancel-related" style="font-size: 16px; color: #a6a6a6;"></i>'+
                '</div>'+
                '<div class="related-menu" style="display: none">'+
                '<i class="layui-icon layui-icon-close close-related-menu" style="font-size: 20px; color: #a6a6a6;"></i>'+
                '<div class="related-menu-title">关联菜单</div>'+
                '<ul>'+
                '<li class="boxsizing cancle" data-id="' + scheduleId + '" data-binding-id="' + binding[i].id + '">'+
                '<i class="layui-icon layui-icon-about" style="font-size: 16px; color: gray;"></i>'+
                '<span>取消关联</span>'+
                '</li>'+
                '</ul>'+
                '</div>'+
                //     '<!--<div class="related-rc-down">-->'
                //     <!--<span>2018-12-25 12:00</span>-->'
                // <!--<span>—</span>-->
                // <!--<span>2018-12-25 12:00</span>-->
                // <!--</div>-->
                '</li>';
            $('.related-fx-wrap').show();
            $('.related-fx').prepend(content);
            var form = layui.form;
            form.render();
        }
    }

}

/**
 * 取消关联 的单击事件
 */
$("body").on("click",".cancle",function (e) {
    var id = $(this).attr("data-id");
    var bindId = $(this).attr("data-binding-id");
    var url = "/binding/deleteBinding";
    var args = {"publicId":id,"bindingId":bindId};
    $.post(url,args,function (data) {
        if(data.result == 0){
            layer.msg(data.msg);
        }
    },"json");
    e.stopPropagation();
});

/**
 * 任务详情的时候显示的人员信息
 */
$(".add-work-people img").click(function (e) {
    $("#executor").addClass("special-executor");
    var url = '/schedule/findScheduleMemberInfo';
    var args = {"projectId":projectId, "scheduleId": scheduleId};
    $.post(url, args, function (data) {
        if(data.result===1){
            $('#executor').html('');
            $('#noExecutor').html('');
            for(var i=0;i<data.joinInfo.length;i++){
                var div = '<div class="one-people" id="'+data.joinInfo[i].id+'">'+
                    '<img src="'+ IMAGE_SERVER+data.joinInfo[i].userInfo.image +'"/>'+
                    '<span value = "' + data.joinInfo[i].id + '">'+ data.joinInfo[i].userName +'</span>'+
                    '<i class="layui-icon layui-icon-ok" style="font-size: 16px; color: rgb(209, 209, 209); display: inline;"></i>' +
                    '</div>';
                $('#executor').append(div);
            }


            for(var j=0;j<data.projectMembers.length;j++){
                var div = '<div class="one-people" id="'+data.projectMembers[j].id+'">'+
                    '<img src="'+ IMAGE_SERVER+data.projectMembers[j].userInfo.image +'"/>'+
                    '<span value = "' + data.projectMembers[j].id + '">'+ data.projectMembers[j].userName +'</span>'+
                    '<i class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;"></i>'+
                    '</div>';
                $('#noExecutor').append(div);
            }
        }

    $(".people").show(500);
});
e.stopPropagation();
});

//点击人员 出现对勾
$("html").on("click",".one-people",function () {
    $(this).find('i').toggle();
    var arr=[]
    for (var i=0;i<$(".one-people").length;i++){
        if ($(this).find(i).is(":visible")) {
            var value=$(this).find("span").attr("value");
        }
    }
});

/**
 * 人员弹框确定单击
 */
$(".people-ok").click(function (e) {
    var addUserEntity=[];
    var removeUserEntity=[];
    for (var i=0;i<$("#executor .one-people").length;i++){
        if ($("#executor .one-people").eq(i).find("i").is(":visible")) {
            var value =$("#executor .one-people").eq(i).find("span").attr("value");
            addUserEntity.push(value);
        }
    }
    for (var i=0;i<$("#noExecutor .one-people").length;i++){
        if ($("#noExecutor .one-people").eq(i).find("i").is(":visible")) {
            var value =$("#noExecutor .one-people").eq(i).find("span").attr("value");
            addUserEntity.push(value);
        }
    }
    if(addUserEntity == '' && removeUserEntity == ''){
        $(".people").hide(500);
        return false;
    }
    var url = "/schedule/addAndRemoveScheduleMember";
    var args = {"scheduleId":scheduleId,"addUserEntity":addUserEntity.toString()};
    var content = '';
    $.post(url,args,function (data) {
        $('.people').hide(500);
    },"json");
});


/**
* 追加文件操作日志的方法
* @param taskLogVO 任务日志对象
*/
function getLog(taskLogVO){
    var datey = new Date().getFullYear();
    var datem=new Date().getMonth()+1;
    if (datem <10){
        datem='0'+datem
    }
    var dated=new Date().getDay();
    if (dated <10){
        dated='0'+dated
    }
    var dateh=new Date().getHours();
    var datemin=new Date().getMinutes();
    var date =datey+'-'+datem+'-'+dated+' '+dateh+":"+datemin
    var log = '';
    log += '<li class="combox clearfix">'+
        '<img src="' + IMAGE_SERVER+taskLogVO.userEntity.userInfo.image+ '" />'+
        '<span>'+ taskLogVO.content +'</span>'+
        '<div class="in-what-time"  >' + date + '</div>'+
        '</li>';
    $('#log').append(log);
    var scrollHeight = $('#log li:nth-last-child(1)').position().top;
    $(".scroll-box").animate({scrollTop:scrollHeight}, 400);
}

/**
 * 移除参与者
 */
$('html').on('click','.remove-work-people',function () {
    var ids = [];
    $(this).parent().siblings('.one-work-people').each(function () {
        ids.push($(this).attr('id'));
    });
    var url = "/schedule/addAndRemoveScheduleMember";
    var args = {"addUserEntity":ids.toString(),"scheduleId":scheduleId};
    $.post(url,args,function (data) {
        if(data.result == 0){
            layer.msg("系统异常,操作失败!");
        }
    },"json");
});


/**
 * 点击关联的文件
 */
$("html").on("click",'.related-wj li',function () {
    window.open("/file/fileDetail.html?fileId="+$(this).attr('data-id'),"在线预览文件");
});

/**
 * 点击关联的任务
 */
$("html").on("click",'.related-rw .boxsizing .related-rw-info',function () {
    changeRenwu($(this).parent() .attr("data-id"),projectId);
});

/**
 * 点击关联的日程
 */
$("html").on("click",'.related-rc .boxsizing .related-rc-info',function () {
    changeRicheng($(this).parent().parent().attr("data-id"),projectId);
});

/**
 * 点击关联的分享
 */
$("html").on("click",'.related-fx li',function () {
    layer.closeAll();
    parent.location.href = "/share/share.html?shareId="+ $(this).attr('data-id') + "&projectId=" + projectId;
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
/**
 * 更新任务的名称
 */
$('.scheduleName').blur(function () {
    var scheduleName = $('.scheduleName').val();
    var args = {"scheduleName":scheduleName,"scheduleId":scheduleId};
    var url = "/schedule/updateScheduleName";
    $.post(url,args,function (data) {
        if(data.result == 0){
            layer.msg(data.msg);
        }
    })
});

//开始时间的回调
layui.use('laydate', function(){
    var laydate = layui.laydate;

});

/**
 * 监听重复规则的选择
 */
layui.use('form', function(){
    var form = layui.form;
    form.on('select(repeat)', function(data){
        var url = "/schedule/updateScheduleRepeat";
        var args = {"scheduleId":scheduleId,"repeat":data.value};
        $.post(url,args,function (data) {
            if(data.result == 0){
                layer.msg(data.msg);
            }
        });
        // console.log(data.elem); //得到select原始DOM对象
        // console.log(data.value); //得到被选中的值
        // console.log(data.othis); //得到美化后的DOM对象
    });
});

/**
 * 监听提醒模式的选择
 */
layui.use('form', function(){
    var form = layui.form;
    form.on('select(remind)', function(data){
        var url = "/schedule/updateScheduleRemind";
        var args = {"scheduleId":scheduleId,"remind":data.value};
        $.post(url,args,function (data) {
            if(data.result == 0){
                layer.msg(data.msg);
            }
        });
        // console.log(data.elem); //得到select原始DOM对象
        // console.log(data.value); //得到被选中的值
        // console.log(data.othis); //得到美化后的DOM对象
    });

    /**
     * 标记为全天 或者取消全天
     */
    form.on('checkbox(isAllday)', function(data){
        var url = "/schedule/isAllday";
        var args = {"scheduleId":scheduleId,"isAllday":data.elem.checked};
        $.post(url,args,function(data){
            if(data.result == 0){
                layer.msg(data.msg);
            }
        });
        // console.log(data.elem); //得到checkbox原始DOM对象
        // console.log(data.elem.checked); //是否被选中，true或者false
        // console.log(data.value); //复选框value值，也可以通过data.elem.value得到
        // console.log(data.othis); //得到美化后的DOM对象
    });
});

/**
 * 更新日程的地点
 */
$('.sure-btn').click(function () {
    var url = "/schedule/updateScheduleAddress";
    var args = {"scheduleId":scheduleId,"address":$('.address').val()};
    $.post(url,args,function(data){
        $(".add-input-box").slideUp(200,function () {
            $(".will-add").show()
        });
        if(data.result == 0){
            layer.msg(data.msg);
        }

    });
});

/**
 * 点击添加标签按钮弹出标签页面
 */
$(".no-tags").click(function (e) {
    var width=(parent.window.innerWidth-600)/2+20;
    var height=(parent.window.innerHeight-600)/2+20;
    var top = $(this).offset().top+height+"px";
    var left = $(this).offset().left+width+"px";
    parent.layer.open({
        type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
        title: false, //标题
        offset: [top, left],
        area: ['250px', '250px'],
        fixed: false,
        shadeClose: true, //点击遮罩关闭
        shade: [0.1, '#fff'],
        closeBtn: 0,
        anim: 1,  //动画 0-6
        content: ['/tag/tag.html?projectId='+projectId+"&publicId="+scheduleId+"&publicType="+'日程','no']
    });
    e.stopPropagation();
});


/**
 * 添加标签
 */
$(".tags-list").click(function () {
    var tagId = $(this).attr('id');
    $.post("/schedule/addTag", {"scheduleId": scheduleId,tagId: tagId}, function (data) {
        if (data.result === 0) {
            layer.msg(data.msg);
        }
    })
});

/**
 * 点击 x 移除标签
 */
$('html').on('click','.remove-tag',function () {
    var id = $(this).parent().attr('id');
    var url = "/tag/removeTag";
    var args = {"publicId":scheduleId,"publicType":"日程","tagId":id};
    $.post(url,args,function (data) {
        if(data.result  === 0){
            layer.msg(data.msg);
        }
    });
});


// 点击  任务菜单出现隐藏
$(".layui-icon-down").click(function () {
    $(".schedule-menu").slideToggle();
});

$('.sc').click(function () {
    if($(this).children('span').html() === '收藏日程'){
        var that = $(this);
        var url = "/public/collectItem";
        var args = {"publicId":scheduleId,"publicType":"日程"};
        $.post(url,args,function () {
            that.children('span').html('取消收藏');
        });
    } else{
        var that = $(this);
        var url = "/public/cancelCollectByUser";
        var args = {"publicId":scheduleId};
        $.post(url,args,function () {
            that.children('span').html('收藏日程');
        });
    }
});



