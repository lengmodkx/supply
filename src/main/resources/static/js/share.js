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
                '<li class="boxsizing cancle" data-id="' + shareId + '" data-binding-id="' + binding[i].taskId + '">'+
                '<i class="layui-icon layui-icon-about" style="font-size: 16px; color: gray;"></i>'+
                '<span>取消关联</span>'+
                '</li>'+
                '</ul>'+
                '</div>'+
                '</li>';
            $('.related-rw-wrap').each(function () {
                var that = $(this);
                if(that.attr('data-id') == $('.share-list.selected').attr('data')){
                    $(that).show();
                }
                // $('.related-fx').prepend(content);
            })
            $('.related-rw').each(function () {
                if($(this).attr('data-id') == $('.share-list.selected').attr('data')){
                    $(this).prepend(content);
                }
            })
            // $('.related-rw-wrap').show();
            // $('.related-rw').prepend(content);
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
                '<li class="boxsizing cancle" data-id="' + shareId + '" data-binding-id="' + binding[i].fileId + '">'+
                '<i class="layui-icon layui-icon-about" style="font-size: 16px; color: gray;"></i>'+
                '<span>取消关联</span>'+
                '</li>'+
                '</ul>'+
                '</div>'+
                '</li>';
            $('.related-wj-wrap').each(function () {
                var that = $(this);
                if(that.attr('data-id') == $('.share-list.selected').attr('data')){
                    $(that).show();
                }
                // $('.related-fx').prepend(content);
            })
            $('.related-wj').each(function () {
                if($(this).attr('data-id') == $('.share-list.selected').attr('data')){
                    $(this).prepend(content);
                }
            })
            // $('.related-wj-wrap').show();
            // $('.related-wj').prepend(content);
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
                '<li class="boxsizing cancle" data-id="' + shareId + '" data-binding-id="' + binding[i].scheduleId + '">'+
                '<i class="layui-icon layui-icon-about" style="font-size: 16px; color: gray;"></i>'+
                '<span>取消关联</span>'+
                '</li>'+
                '</ul>'+
                '</div>'+
                '</li>';
        }
        $('.related-rc-wrap').each(function () {
            var that = $(this);
            if(that.attr('data-id') == $('.share-list.selected').attr('data')){
                $(that).show();
            }
            // $('.related-fx').prepend(content);
        })
        $('.share-right').each(function () {
            if($(this).attr('data') == $('.share-list.selected').attr('data')){
                $(this).find('.related-rc').prepend(content);
            }
        })
        // $('.related-rc-wrap').show();
        // $('.related-rc').prepend(content);
        // var form = layui.form;
        // form.render();
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
                '<li class="boxsizing cancle" data-id="' + shareId + '" data-binding-id="' + binding[i].id + '">'+
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
            $('.related-fx-wrap').each(function () {
                var that = $(this);
                if(that.attr('data-id') == $('.share-list.selected').attr('data')){
                    $(that).show();
                }
                    // $('.related-fx').prepend(content);
            })
            $('.share-right').each(function () {
                if($(this).attr('data') == $('.share-list.selected').attr('data')){
                    $(this).find('.related-fx').prepend(content);
                }
            })
            var form = layui.form;
        }
            form.render();
    }

}
/**
 * 添加任务操作日志的方法
 * @param taskLogVO 任务日志对象
 */
function getLog(shareLog,shareId){
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

    var log = '<li class="combox">'+
        '<img src="' + IMAGE_SERVER+shareLog.userEntity.userInfo.image+ '" />'+
        '<span>'+ shareLog.content +'</span>'+
        '<div class="in-what-time"  >' + date + '</div>'+
        '</li>';
    $('.log').each(function () {
        if($(this).attr('data-id') == shareId){
            $(this).append(log);
        }
    })
}

/**
 * 更新参与者的头像
 */
function updatePeopel(share){
    $('.share-right').each(function () {
        if($(this).attr('data') == share.object.shareId){
            if(share.object.reduce1 != ''){
                for(var i = 0;i < share.object.reduce1.length;i++){
                    $(this).find(".one-work-people").each(function () {
                        if($(this).attr('data-id') == share.object.reduce1[i]){
                            $(this).remove();
                        }
                    });
                }
            }
            if(share.object.adduser != ''){
                var content = '';
                for(var i = 0;i < share.object.adduser.length;i++){
                    content +=
                        '<div class="one-work-people" data-id = '+ share.object.adduser[i].id +'>'+
                        '<img src="'+IMAGE_SERVER + share.object.adduser[i].userInfo.image + '" data = ' + share.object.adduser[i].userName +'>'+
                        '<i class="layui-icon layui-icon-close-fill remove-work-people" style="font-size: 15px; color: #3da8f5;"></i>'+
                        '</div>';
                }
                $(this).find(".add-work-people").before(content);
            }
        }
    });
    $(".people").hide(500);
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