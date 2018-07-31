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
                    '<i class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;"></i>'+
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
    log += '<li class="combox">'+
        '<img src="' + IMAGE_SERVER+taskLogVO.userEntity.userInfo.image+ '" />'+
        '<span>'+ taskLogVO.content +'</span>'+
        '<div class="in-what-time"  >' + date + '</div>'+
        '</li>';
    $('#log').append(log);
    var scrollHeight = $('#log li:nth-last-child(1)').position().top;
    $(".scroll-box").animate({scrollTop:scrollHeight}, 400);
}