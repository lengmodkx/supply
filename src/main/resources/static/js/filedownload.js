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
function addBindingStr(binding,type,bindId) {
    var content = "";
    if (type == '任务') {
        for (var i = 0; i < binding.length; i++) {
            content += '<li class="boxsizing data-info" data-id="' + binding[i].taskId + '">' +
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
                '<li class="boxsizing cancle"  data-id="' + fileId + '"  data-binding-id ="' + binding[i].taskId + '">' +
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
            content += '<li class="boxsizing data-info" data-id = "' + binding[i].fileId + '">' +
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
                '<li class="boxsizing cancle" data-id="' + fileId + '" data-binding-id="' + binding[i].fileId + '">' +
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
            content += '<li class="boxsizing data-info" data-id = '+ binding[i].scheduleId +'>' +
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
                '<li class="boxsizing cancle" data-id="' + fileId + '" data-binding-id="' + binding[i].scheduleId + '">' +
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
            content += '<li class="boxsizing data-info" data-id = '+ binding[i].id +'>' +
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
                '<li class="boxsizing cancle" data-id="' + fileId + '" data-binding-id="' + binding[i].id + '">' +
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
            $('.related-fx').prepend(content);
            var form = layui.form;
            form.render();
        }
    }
}

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
        var top=$(this).offset().top+20+'px';
        var left=$(this).offset().left-120+'px';
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: [top,left],
            area: ['250px', '250px'],
            fixed: false,
            shadeClose: true, //点击遮罩关闭
            shade: [0.1, '#fff'],
            closeBtn: 0,
            anim: 1,  //动画 0-6
            content: ['/tag/tag.html?projectId='+projectId+"&publicId="+fileId+"&publicType="+'文件','no']
        });
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
    e.stopPropagation()
});

/**
 * 点击关联的任务
 */
$("html").on("click",'.related-rw .related-rw-info',function () {
    changeRenwu($(this).parent() .attr("data-id"),projectId);
});

/**
 * 点击关联的日程
 */
$("html").on("click",'.related-rc .boxsizing .related-rc-info',function () {
    changeRicheng($(this).parent().parent().attr("data-id"),projectId);
});

/**
 * 点击关联的文件
 */
$("html").on("click",'.related-wj li',function () {
    location.href = "/file/list.html?fileId="+$(this).attr('data-id');
});

/**
 * 任务详情的时候显示的人员信息
 */
$(".add-work-people img").click(function (e) {
    var content = '';
    var members = '';
    var url = "/file/findProjectMember";
    var args = {"fileId":fileId,"projectId":projectId};
    $.post(url,args,function (data) {
        var joinInfo = data.joinInfo;
        var projectMember = data.projectMember;

        //这里是循环参与者
        for (var i = 0;i < joinInfo.length;i++) {
            content +=
                '<div class="one-people">' +
                '<img src="' + IMAGE_SERVER + joinInfo[i].userInfo.image + '"/>' +
                '<span value = "' + joinInfo[i].id + '">' + joinInfo[i].userName + '</span>' +
                '<i class="layui-icon layui-icon-ok" style="font-size: 16px; color: rgb(209, 209, 209); display: inline;"></i>' +
                '</div>';
        }

        //这里是循环项目的推荐人员
        if(projectMember == ''){
            members += "<div class=\'one-people\'>"+
                "<img src = '/image/add.png'>"+
                "<span value = ''>" + '该项目没有成员了' + "</span>"+
                "<i class=\'layui-icon layui-icon-ok\' style=\'font-size: 16px; color: #D1D1D1;\'></i>"+
                "</div>";
        } else{
            for (var i = 0;i < projectMember.length;i++){
                members += "<div class=\'one-people\'>"+
                    "<img src = '" + IMAGE_SERVER+projectMember[i].userInfo.image +"'>"+
                    "<span value = '"+ projectMember[i].id +"'>" + projectMember[i].userName + "</span>"+
                    "<i class=\'layui-icon layui-icon-ok\' style=\'font-size: 16px; color: #D1D1D1;\'></i>"+
                    "</div>";
            }

        }
        $('#joinInfo').html(content);
        $('#Recommend').html(members);
        $(".people").show(500);
    },"json");
    e.stopPropagation();
});


/**
 * 点击人员 弹框 确定 按钮
 */
$('.people-ok').click(function () {
    var addUserEntity=[];
    var removeUserEntity=[];
    for (var i=0;i<$("#joinInfo .one-people").length;i++){
        if ($("#joinInfo .one-people").eq(i).find("i").is(":visible")) {
            var value =$("#joinInfo .one-people").eq(i).find("span").attr("value");
            addUserEntity.push(value);
        }
    }
    for (var i=0;i<$("#Recommend .one-people").length;i++){
        if ($("#Recommend .one-people").eq(i).find("i").is(":visible")) {
            var value =$("#Recommend .one-people").eq(i).find("span").attr("value");
            var image =$("#Recommend .one-people").eq(i).find("img").attr("src");
            addUserEntity.push(value);
        }
    }
    if(addUserEntity == '' && removeUserEntity == ''){
        $(".people").hide(500);
        return false;
    }

    var url = "/file/addAndRemoveFileJoin";
    var args = {"newJoin":addUserEntity.toString(),"fileId":fileId};
    $.post(url,args,function (data) {
        $(".people").hide(500);
        if(data.result == 0){
            layer.msg("系统异常,操作失败!");
        }
    },"json");
});


/**
 * 点击移除标签
 */
$('html').on('click','.remove-tag',function(){
   var url = "/tag/removeTag";
   var args = {"publicId":fileId,"publicType":"文件","tagId":$(this).parent().attr('id')};
   $.post(url,args,function(data){
       if(data.result > 0){
           layer.msg(data.msg);
       }
   })
});

/**
 * 移除参与者
 */
$('html').on('click','.remove-work-people',function () {
    var id = $(this).parent().attr('data-id');
    var ids = [];
    $('.one-work-people').each(function () {
        ids.push($(this).attr('data-id'));
    });
    for(var i = 0;i < ids.length;i++){
        if(ids[i] == id){
            ids.splice(i,1);
        }
    }
    alert(ids);

    var url = "/file/addAndRemoveFileJoin";
    var args = {"newJoin":ids.toString(),"fileId":fileId};
    $.post(url,args,function (data) {
        if(data.result == 0){
            layer.msg("系统异常,操作失败!");
        }
    },"json");
});


var zxz=false;
//点击人员 出现对勾
$("html").on("click",".one-people",function () {
    if (zxz){
        $(this).siblings().find("i").hide();
        $(this).find("i").toggle();
    } else {
        $(this).find("i").toggle();
    }

    var arr=[]
    for (var i=0;i<$(".one-people").length;i++){
        if ($(this).find(i).is(":visible")) {
            var value=$(this).find("span").attr("value");
        }
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

//修改任务 弹框界面
function changeRicheng(id,projectId) {
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
            content: "/schedule/editSchedule.html?id="+ id + "&projectId=" + projectId
        });
    });
}