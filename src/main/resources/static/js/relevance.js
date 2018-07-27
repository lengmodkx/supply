
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
var arr=[];
$(function () {
    $(".close-tk").click(function () {
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });
    // //点击 任务 分享 日程 文件 切换 页面
    $(".one-level-nav li").click(function () {
        arr.splice(0,arr.length);
        $(this).addClass("now").siblings().removeClass("now");
        var i=$(this).index();
        $(".body-box>div").eq(i).show().siblings().hide();
    });
    $("html").on("click",".style-li",function () {
        $(this).addClass("selected").siblings().removeClass("selected");
        $(this).siblings("div").find(".style-li").removeClass("selected");
        $(this).parent("div").siblings(".style-li").removeClass("selected")
    });
    $("html").on("click",".a-task-li ",function () {
        arr.push($(this));
        console.log(arr);
        $(this).addClass("selected").siblings().removeClass("selected")
    });
    $("html").on("click",".wj-li ",function () {
        arr.push($(this));
        if ($(this).parent().attr("class")=='wjj'){
            $(".wj").find(".wj-li").removeClass("selected");
            $(this).addClass("selected").siblings().removeClass("selected");
            $(this).siblings().find("img").attr("src","/image/wjj-b.png");
            $(this).find("img").attr("src","/image/wjj-fff.png");
            $(this).parents(".show-next-wj-li").nextAll().remove();
        } else {
            $(".wjj").find(".wj-li").removeClass("selected");
            $('.wjj').find("img").attr("src","/image/wjj-b.png");
            if ($(this).hasClass("selected")) {
                $(this).removeClass("selected");
            }else {
                $(this).addClass("selected");
            }
        }

    });
    //点击显示 隐藏 过去 日程
    $("html").on("click",".is-show",function () {
        if ($(this).parent().siblings(".old-rc").is(":visible")){
            $(this).parent().siblings(".old-rc").hide();
            $(this).text("显示")
        } else {
            $(this).parent().siblings(".old-rc").show();
            $(this).text("隐藏")
        }
    });
    //点击新建文件夹
    $("html").on("click",".up-add>i:nth-of-type(2)",function () {
        $(this).parent().siblings(".wjj-wrap").find("input").show();
    });
    $("html").on("keyup",".create-wjjs",function (e) {
        if(e.which == 13) {
            if ($(this).val()==''){
                $(this).hide();
            }else {
                $(this).siblings("ul").prepend('<li class="wj-li over-hidden">\n' +
                    '                                        <img src="../static/image/wjj-b.png" th:src="@{/image/wjj-b.png}">\n' +
                    '                                        <span >'+$(this).val()+'</span>\n' +
                    '                                    </li>');
                $(this).hide();
            }
        }
    });

    // 任务 界面
    // 点击 ，显示后面的
    $("html").on("click",".show-next .style-li",function () {
        $(this).parent().siblings(".task-ul").show();

    });

    $("html").on("click",".show-next-li .a-task-li",function (){
        $(this).parents(".show-next-li").nextAll().remove();
        // $(this).parents(".show-next-li").after(' <li class="show-next-li">\n' +
        //     '                                <div class="title-create-style boxsizing">\n' +
        //     '                                    <i class="layui-icon layui-icon-add-circle" style="font-size: 20px; color: #3AA7F5;"></i>\n' +
        //     '                                    <span>创建子任务</span>\n' +
        //     '                                </div>\n' +
        //     '                                <ul class="a-task-box">\n' +
        //     '                                    <li class="group-name">子任务</li>\n' +
        //     '                                    <li class="a-task-li">\n' +
        //     '                                        <span></span>\n' +
        //     '                                        <img src="../static/image/person.png" th:src="@{/image/person.png}">\n' +
        //     '                                        <p class="over-hidden">这是一条任务名称</p>\n' +
        //     '                                    </li>\n' +
        //     '                                </ul>\n' +
        //     '                            </li>');



    });

    // 文件 页面
    // 点击 显示 后面 的内容
    $("html").on("click",".show-next-wj .wj-li",function () {
        $(this).parents(".show-next-wj").siblings(".task-ul").show();
        $(this).parents(".show-next-wj").siblings(".task-ul").html("");
        // $(this).parents(".show-next-wj").siblings(".task-ul").append(' <li class="show-next-wj-li">\n' +
        //     '                                <div class="paper-file">\n' +
        //     '                                    <div class="up-add">\n' +
        //     '                                        <i class="layui-icon layui-icon-upload-circle" style="font-size: 20px; color: gray;"></i>\n' +
        //     '                                        <i class="layui-icon layui-icon-add-circle" style="font-size: 20px; color: gray;"></i>\n' +
        //     '                                    </div>\n' +
        //     '                                    <div class="wjj-wrap">\n' +
        //     '                                        <p>文件夹</p>\n' +
        //     '                                        <ul>\n' +
        //     '                                            <li class="wj-li over-hidden">\n' +
        //     '                                                <img src="../static/image/wjj-b.png" th:src="@{/image/wjj-b.png}">\n' +
        //     '                                                <span >123132</span>\n' +
        //     '                                            </li>\n' +
        //     '                                        </ul>\n' +
        //     '                                    </div>\n' +
        //     '                                    <div class="wjj-wrap">\n' +
        //     '                                        <p>文件</p>\n' +
        //     '                                        <ul>\n' +
        //     '                                            <li class="wj-li over-hidden">\n' +
        //     '                                                <img src="../static/image/wjj-b.png" th:src="@{/image/wjj-b.png}">\n' +
        //     '                                                <span >123132</span>\n' +
        //     '                                            </li>\n' +
        //     '                                        </ul>\n' +
        //     '                                    </div>\n' +
        //     '                                </div>\n' +
        //     '\n' +
        //     '                            </li>')
    });
    $("html").on("click",".show-next-wj-li .wj-li",function () {
        $(this).parents(".show-next-wj-li").nextAll().remove();
        // $(this).parents(".show-next-wj-li").after(' <li class="show-next-wj-li">\n' +
        //     '                                <div class="paper-file">\n' +
        //     '                                    <div class="up-add">\n' +
        //     '                                        <i class="layui-icon layui-icon-upload-circle" style="font-size: 20px; color: gray;"></i>\n' +
        //     '                                        <i class="layui-icon layui-icon-add-circle" style="font-size: 20px; color: gray;"></i>\n' +
        //     '                                    </div>\n' +
        //     '                                    <div class="wjj-wrap">\n' +
        //     '                                        <p>文件夹</p>\n' +
        //     '                                        <ul>\n' +
        //     '                                            <li class="wj-li over-hidden">\n' +
        //     '                                                <img src="../static/image/wjj-b.png" th:src="@{/image/wjj-b.png}">\n' +
        //     '                                                <span >123132</span>\n' +
        //     '                                            </li>\n' +
        //     '                                        </ul>\n' +
        //     '                                    </div>\n' +
        //     '                                    <div class="wjj-wrap">\n' +
        //     '                                        <p>文件</p>\n' +
        //     '                                        <ul>\n' +
        //     '                                            <li class="wj-li over-hidden">\n' +
        //     '                                                <img src="../static/image/wjj-b.png" th:src="@{/image/wjj-b.png}">\n' +
        //     '                                                <span >123132</span>\n' +
        //     '                                            </li>\n' +
        //     '                                        </ul>\n' +
        //     '                                    </div>\n' +
        //     '                                </div>\n' +
        //     '                            </li>');
        $(this).parents(".scroll-box-heng").scrollLeft(parseInt( $(this).parents(".task-ul").width()));

    });


});

/**
 * 点击 任务 分享 文件 日程 时候会加载项目信息
 */
$('.one-level-nav>li').click(function(){
    var j=$(this).index();
    var item = $(this).children('span').html();
    var args= {"label":2};
    var url = "/project/projectList";
    if ($(".body-box>div").eq(j).children(".style-ul").html()) {
        return false
    }else {
        $.post(url,args,function(data){
            var project = data.data;
            var content = '';
            content += '<li class="group-name"><p>' + '个人项目' + '</p></li>';
            for(var i = 0;i < project.length;i++){
                if(i == 0){
                    content += '<li class="style-li p style-li selected" data-id = "' + project[i].projectId + '"><span>' + project[i].projectName + '</span></li>';
                } else{
                    content += '<li class="style-li p" data-id = "' + project[i].projectId + '"><span>' + project[i].projectName + '</span></li>';
                }
                if(i == 0){
                    var url = "";
                    var args = {"projectId":project[0].projectId};
                    if(item == '文件'){
                        url = "/file/fileList";
                    }
                    if(item == '任务'){
                        url = "/relation/projectAllGroup";
                    }
                    if(item == '日程'){
                        url = "/schedule/scheduleList";
                    }
                    if(item == '分享'){
                        url = "/share/shareByProjectId";
                    }
                    $.post(url,args,function(data){
                        addGroups(data.data,item);
                    },"json");
                }
            }
            $(".body-box>div").eq(j).children(".style-ul ").html(content)
        },"json");
    }

});



/**
 * 点击一个项目的时候 加载这个项目下的所有分组
 */
$("html").on("click",".style-ul li",function (){
   var it=$(this);
    $('.a-task-box').html('');
    var projectId = $(this).attr('data-id');
    var url = '';
    var args = {"projectId":projectId};
    var type = $('.one-level-nav .now').children('span').html();
    if(type == '任务'){
        url = "/relation/projectAllGroup";
    }
    if(type ==  '文件'){
        url = "/file/fileList";
    }
    if(type == '日程'){
        url = "/schedule/scheduleList";
    }
    if(type == '分享'){
        url = "/share/shareByProjectId";
    }
    var content = "";
    $.post(url,args,function (data) {
        var item = data.data;
        if(type == '任务'){
            var list = $('#groups>div');
          if (it.hasClass("groupId")) {
             return false
          }else {
              list.html(addGroups(item,type))
          }

        }
        if(type == '文件'){
            if(item != ''){
                addGroups(item,type);
            } else{

            }
        }
        if(type == "日程"){
            addGroups(item,type);
        }
        if(type == "分享"){
            addGroups(item,type);
        }
    },"json")
});

/**
 * 点击分组的时候,查询该分组下的所有任务
 */
$("html").on("click","#groups .groupId",function () {

    var id = $(this).attr("data-id");
    if(id == undefined){
        id = null;
    }
    var projectId = $('.project-ul .selected').attr('data-id');
    var url = "/task/findRelationTask";
    var args = {"id":id,"projectId":projectId};
    $.post(url,args,function (data) {
        var relation = data.data;
        addStr(relation,id);
    },"json");
});

//关闭弹窗
$(".close-tk").click(function () {
    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    parent.layer.close(index); //再执行关闭
});

/**
 * 点击一个任务的时候显示该任务的子任务信息
 */
$("html").on("click",".a-task-box .a-task-li",function () {

    var taskId = $(this).attr('data-id');
    var that=$(this);
    var url = "/task/findTaskByFatherTask";
    var args = {"taskId":taskId};
    $.post(url,args,function (data) {
        var subTask = data.data;
        addSubTask(subTask,that);
    },"json");
    $(".ok-btn").addClass("can-click")
});

/**
 * 点击确定关联
 */
$('.ok-btn').click(function () {
    var bindId = [];

    var publicType = $('.one-level-nav .now').children('span').html();
    if(publicType == '' || publicType == undefined){
        layer.msg("请选择关联内容的类型!");
        return false;
    }
    if(publicType == '任务'){
        bindId = arr[arr.length-1].attr("data-id");
    }
    if(publicType == '文件'){
        $('.wj .wj-li.selected').each(function (i,n) {
            bindId.push($(n).attr('data-id'));
        })
    }
    if(publicType == '日程'){
        bindId = arr[arr.length-1].attr("data-id");
    }
    if(publicType == '分享'){
        bindId = arr[arr.length-1].attr("data-id");
    }
    if(bindId == '' || bindId == undefined){
        layer.msg("必须选择关联内容!");
        return false;
    }
    var args = {};
    if(fileId != null){
        args = {"publicId":fileId,"bindId":bindId.toString(),"publicType":publicType};
    }
    if(taskId != null){
        args = {"publicId":taskId,"bindId":bindId.toString(),"publicType":publicType};
    }
    if(shareId != null){
        args = {"publicId":shareId,"bindId":bindId.toString(),"projectId":projectId,"publicType":publicType};
    }
    if(scheduleId != null){
        args = {"publicId":scheduleId,"bindId":bindId.toString(),"publicType":publicType};
    }
    var url = "/binding/saveBinding";
    $.post(url,args,function (data) {
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    },"json");
});

/**
 * 点击文件夹 加载该文件夹下的所有文件
 */
$("html").on("click",".wjj li",function () {
    var fileId = $(this).attr('data-id');
    var that = $(this);
    var projectId = $('.project-ul .selected').attr('data-id');
    var url = "/file/findChildFile";
    var args = {"projectId":projectId,"fileId":fileId};
    $.post(url,args,function (data) {
        var item = data.data;
        if(item != ''){
            addFileStr(item,that);
        } else{
            return false;
        }
    },"json");
});


/**
 * 拼接任务的字符串
 * @param relation
 * @param id
 */
function addStr(relation,id){
    $('.a-task-box').html("");
    var content = '';
    if(id == '今天的任务' || id == '未完成任务' || id == '已完成任务'){
        for(var i = 0;i < relation.length;i++){
            content += '<li class="a-task-li" data-id="' + relation[i].taskId + '">'+
                '<span></span>';
            if(relation[i].executorInfo == null){
                content += '<img src="/image/add.png">';
            } else{
                content += '<img src="' + IMAGE_SERVER + relation[i].executorInfo.userInfo.image + '">';
            }
            content += '<p class="over-hidden">' + relation[i].taskName + '</p>'+
                '</li>';
        }
    } else{
        for(var i = 0;i < relation.length;i++){
            if(relation[i].taskList != ''){
                content += '                                    <li class="group-name">' + relation[i].relationName + '</li>';
                //这里开始循环任务
                for(var j = 0;j < relation[i].taskList.length;j++){
                    content += '                                    <li class="a-task-li"  data-id = '+ relation[i].taskList[j].taskId +'>' +
                        '                                        <span></span>' +
                        '                                        <img src="' + IMAGE_SERVER + relation[i].taskList[j].executorInfo.userInfo.image+ '">' +
                        '                                        <p class="over-hidden">' + relation[i].taskList[j].taskName + '</p>' +
                        '                                    </li>';
                }
                content += '                                </ul>' +
                    '                            </li>';
            }
        }
    }
    $('.a-task-box').append(content);
}

/**
 * 拼接子任务字符串
 */
function addSubTask(subTask,that) {
    var content = "";
    if(subTask != ''){
        content += ' <li class="show-next-li">\n' +
                    '<div class="title-create-style boxsizing">\n' +
                    '<i class="layui-icon layui-icon-add-circle" style="font-size: 20px; color: #3AA7F5;"></i>\n' +
                    '<span>创建子任务</span>\n' +
                    '</div>\n' +
                    '<ul class="a-task-box">\n';

        for (var i = 0; i < subTask.length; i++) {
            content += '<li class="a-task-li" data-id = "' + subTask[i].taskId + '">\n' +
                       '<span></span>\n';
            if (subTask[i].taskExecutorInfo == null) {
                content += '<img src="/image/add.png">\n';
            } else {
                content += '<img src="' + IMAGE_SERVER + subTask[i].taskExecutorInfo.userInfo.image + '">\n';

            }
            content += '<p class="over-hidden">' + subTask[i].taskName + '</p>\n' +
                        '</li>\n';
        }
    } else{
        content += '<li class="show-next-li">\n' +
            '                                <div class="title-create-style boxsizing">\n' +
            '                                    <i class="layui-icon layui-icon-add-circle" style="font-size: 20px; color: #3AA7F5;"></i>\n' +
            '                                    <span>创建子任务</span>\n' +
            '                                </div>\n' +
            '                            </li>';
    }
    that.parents(".show-next-li").after(content);
    that.parents(".scroll-box-heng").scrollLeft(parseInt( that.parents(".task-ul").width()));
}

/**
 * 点项目之后弹出来的信息
 */
function addGroups(item,type,that){
    var content = '';
    var content2 = '';
    if(type == '任务'){
        for(var i = 0;i < item.length;i++){
            content += '<li class="style-li groupId" data-id = "' + item[i].relationId + '"><span>' + item[i].relationName + '</span></li>';
        }

    }
    if(type == "文件"){
        for(var i = 0;i < item.length;i++){
            if(item[i].catalog == 1){
                content += '<li class="wj-li over-hidden" data-id = ' + item[i].fileId +  '>'+
                    '<img src="/image/wjj-b.png">'+
                    '<span >' + item[i].fileName + '</span>'+
                    '</li>';
            } else{
                content2 += '<li class="wj-li over-hidden" data-id = '+ item[i].fileId +'>'
                if(item[i].catalog == 0 && (item[i].ext == '.jpg' || item[i].ext == '.png' || item[i].ext == '.jpeg')){

                    content2 += '<img class="folderFile collect-item-touxiang" src="' + IMAGE_SERVER + item[i].fileUrl + '"/>';
                } else if(item[i].catalog == 0 && item[i].ext == '..doc'){

                    content2 += '<img class="folderFile collect-item-touxiang" src="/image/word_1.png" />';
                } else if(item[i].catalog == 0 && item[i].ext == '.xls'){

                    content2 += '<img class="folderFile collect-item-touxiang" src="/image/excel.png" />';
                } else if (item[i].catalog == 0 && item[i].ext == '.xlsx'){

                    content2 += '<img class="folderFile collect-item-touxiang" src="/image/excel.png" />';
                } else if(item[i].catalog == 0 && item[i].ext == '.pptx'){

                    content2 += '<img class="folderFile collect-item-touxiang" src="/image/ppt.png" />';
                } else if(item[i].catalog == 0 && item[i].ext == '.ppt'){

                    content2 += '<img class="folderFile collect-item-touxiang" src="/image/ppt.png" />';
                } else if(item[i].catalog == 0 && item[i].ext == '.pdf'){

                    content2 += '<img class="folderFile collect-item-touxiang" src="/image/pdf_1.png" />';
                } else if(item[i].catalog == 0 && item[i].ext == '.zip'){

                    content2 += '<img class="folderFile collect-item-touxiang" src="/image/zip.png" />';
                } else if(item[i].catalog == 0 && item[i].ext == '.rar'){

                    content2 += '<img class="folderFile collect-item-touxiang" src="/image/rar.png" />';
                } else {
                    content2 += '<img class="folderFile collect-item-touxiang" src="/image/defaultFile.png" />';
                }
                content2 +=  '<span >' + item[i].fileName + '</span>'+
                    '</li>';
            }

        }
        $('.wjj-wrap .wjj').html(content);
        $('.wj-wrap .wj').html(content2);
        that.parents(".paper-file,.show-next-wj .wjj-wrap").after(content);
        that.parents(".scroll-box-heng").scrollLeft(parseInt( that.parents(".task-ul").width()));
    }
    if(type == "日程"){
        var schedule = item.data;
        var beoforeSchedule = item.before;
        for(var i = 0;i < schedule.length;i++){
            content += '<li class="a-task-li" data-id = '+ schedule[i].scheduleId +'>'+
                '<i class="layui-icon layui-icon-date" style="font-size: 20px; color: gray;"></i>'+
                '<p>' + schedule[i].scheduleName + '</p>'+
                '<div class="over-hidden rc-time">'+
                '<span title="' + new Date(schedule[i].startTime).format('yyyy-MM-dd') + '">' + new Date(schedule[i].startTime).format('yyyy-MM-dd') + '</span>'+
            '<span>—</span>'+
            '<span title="' + new Date(schedule[i].endTime).format('yyyy-MM-dd') + '">' + new Date(schedule[i].startTime).format('yyyy-MM-dd') + '</span>'+
            '</div>'+
            '</li>';
        }
        $('.new-rc-count').html(schedule.length);
        $('.new-rc').html(content);
    }
    if(type == "分享"){
        for(var i = 0;i < item.length;i++){
               content += '<li class="a-task-li " data-id = '+ item[i].id +'>'+
                '<i class="layui-icon  layui-icon-list" style="font-size: 20px; color: gray;"></i>'+
                '<p class="over-hidden">' + item[i].title + '</p>'+
                '</li>';
        }
        $('.share-ul').html(content);
    }
    return content;
}

var li;
function addFileStr(item,that){
    var content = '';
    var content2 = '';
    for(var i = 0;i < item.length;i++){
        if(item[i].catalog == 1){
            content += '<li class="wj-li over-hidden" data-id = ' + item[i].fileId +  '>'+
                '<img src="/image/wjj-b.png">'+
                '<span >' + item[i].fileName + '</span>'+
                '</li>';
        } else{
            content2 += '<li class="wj-li over-hidden" data-id = ' + item[i].fileId + '>';
            if(item[i].catalog == 0 && (item[i].ext == '.jpg' || item[i].ext == '.png' || item[i].ext == '.jpeg')){

                content2 += '<img class="folderFile collect-item-touxiang" src="' + IMAGE_SERVER + item[i].fileUrl + '"/>';
            } else if(item[i].catalog == 0 && item[i].ext == '..doc'){

                content2 += '<img class="folderFile collect-item-touxiang" src="/image/word_1.png" />';
            } else if(item[i].catalog == 0 && item[i].ext == '.xls'){

                content2 += '<img class="folderFile collect-item-touxiang" src="/image/excel.png" />';
            } else if (item[i].catalog == 0 && item[i].ext == '.xlsx'){

                content2 += '<img class="folderFile collect-item-touxiang" src="/image/excel.png" />';
            } else if(item[i].catalog == 0 && item[i].ext == '.pptx'){

                content2 += '<img class="folderFile collect-item-touxiang" src="/image/ppt.png" />';
            } else if(item[i].catalog == 0 && item[i].ext == '.ppt'){

                content2 += '<img class="folderFile collect-item-touxiang" src="/image/ppt.png" />';
            } else if(item[i].catalog == 0 && item[i].ext == '.pdf'){

                content2 += '<img class="folderFile collect-item-touxiang" src="/image/pdf_1.png" />';
            } else if(item[i].catalog == 0 && item[i].ext == '.zip'){

                content2 += '<img class="folderFile collect-item-touxiang" src="/image/zip.png" />';
            } else if(item[i].catalog == 0 && item[i].ext == '.rar'){

                content2 += '<img class="folderFile collect-item-touxiang" src="/image/rar.png" />';
            } else {
                content2 += '<img class="folderFile collect-item-touxiang" src="/image/defaultFile.png" />';
            }
            content2 +=  '<span >' + item[i].fileName + '</span>'+
                '</li>';
        }
        li='<li class="show-next-wj-li">\n' +
            '                                <div class="paper-file">\n' +
            '                                    <div class="up-add">\n' +
            '                                        <i class="layui-icon layui-icon-upload-circle" style="font-size: 20px; color: gray;"></i>\n' +
            '                                        <i class="layui-icon layui-icon-add-circle" style="font-size: 20px; color: gray;"></i>\n' +
            '                                    </div>\n' +
            '                                    <div class="wjj-wrap">\n' +
            '                                        <p>文件夹</p>\n' +
            '                                        <ul class="wjj">\n'+

            content +

            '                                        </ul>\n' +
            '                                    </div>\n' +
            '                                    <div class="wjj-wrap">\n' +
            '                                        <p>文件</p>\n' +
            '                                        <ul class = "wj">\n' +
            content2+
            '                                        </ul>\n' +
            '                                    </div>\n' +
            '                                </div>\n' +
            '\n' +
            '                            </li>'
    }
 if (that.parents(".paper-file").parent().attr("class")=='show-next-wj-li') {
        that.parents(".show-next-wj-li").after(li)
 }else {
     that.parents(".show-next-wj").siblings(".task-ul").html(li);
 }

    // that.parents(".paper-file,.show-next-wj .wjj-wrap").after(content);
    that.parents(".scroll-box-heng").scrollLeft(parseInt( that.parents(".task-ul").width()));
}