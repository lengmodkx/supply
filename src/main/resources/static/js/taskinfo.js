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
var zxz=false;

layui.use('form', function() {
    var form = layui.form;
    // //监听提交
    // form.on('submit(createTask)', function (data) {
    //     //获取选中的参与者信息
    //     var members = $('#memberId').val();
    //     //设置任务的执行者
    //     var executor = $('#executorId').val();
    //     //设置任务开始时间
    //     var beginTime = $('#beginTime').val();
    //     if(beginTime != null && beginTime != ''){
    //         var startTime = new Date(beginTime.toString()).getTime();
    //     } else {
    //         startTime = null;
    //     }
    //     //设置任务结束时间
    //     var overTime = $('#overTime').val();
    //     if(overTime != null && overTime != ''){
    //         var endTime = new Date(overTime.toString()).getTime();
    //     } else{
    //         endTime = null;
    //     }
    //     //设置任务的内容
    //     var taskName = $("#taskName").val();
    //     //设置重复模式
    //     var repeat = $('#repeat').val();
    //     //设置任务提醒
    //     var remind = $('#remand').val();
    //     //设置任务优先级
    //     var priority = $('input[name="state"]:checked').val();
    //     //设置隐私模式
    //     var privacyPattern = "";
    //     if($('#privacyPattern').prop('checked')) {
    //         al
    //         privacyPattern = "1";
    //     } else{
    //         privacyPattern = "0";
    //     }
    //     var url = "/task/saveTask";
    //     var args = {"startTime":startTime ,"endTime":endTime,"taskName":taskName,"repeat":repeat,"remind":remind,"priority":priority,"privacyPattern":privacyPattern,"taskMenuId":taskMenuId,"projectId" : projectId,"members":members,"executor":executor};
    //     $.post(url,args,function(data){
    //         if(data.result == 1){
    //             //关闭遮罩层
    //             //任务数回显
    //         }
    //     },"json");
    //     return false;
    // });

    form.on('checkbox(bindTask)', function(data){
        var val=data.value
        var url = "/task/resetAndCompleteTask";
        var args = {"taskId":data.value,"taskStatus":taskStatus};
        $.post(url,args,function (data) {
            if(data.result == 0){
                layer.msg(data.msg);
                $(this).attr("checked",false);
                form.render();
                return false;
            }
        },"json")
    });

    /**
     * 完成和重做子任务
     */
    form.on('checkbox(subTask)', function(data){
        var subStatus = '';
        if(data.elem.checked == false){
            subStatus = '未完成';
        } else{
            subStatus = '完成';
        }
        var url = "/task/resetAndCompleteSubLevelTask";
        var args = {"taskId":data.value,"taskStatus":subStatus,"taskName":$('#'+data.value).val()};
        $.post(url,args,function (data) {
            if(data.result == 0){
                layer.msg(data.msg);
                $('.child-task-list').children('input[type = "checkbox"]').prop("checked",true);
                form.render();
                return false;
            }
            getLog(data.taskLog);
        },"json");
    });

    /**
     * 重做和完成任务
     */
    form.on('checkbox(taskComplete)', function(data){
        if(parentId == 0){
            var url = "/task/resetAndCompleteTask";
            var args = {"taskId":taskId,"taskName":taskName,"taskStatus":taskStatus};
            $.post(url,args,function (data) {
                if(taskStatus == '完成'){
                    $('#addSubTask').show();
                } else{
                    $('#addSubTask').hide();
                }
                if(data.result == 0){
                    layer.msg(data.msg);
                    $("#taskComplete").attr("checked",false);
                    form.render();
                    return false;
                }
                if(taskStatus == '未完成'){
                    taskStatus = '完成';
                } else if (taskStatus == '完成'){
                    taskStatus = "未完成";
                }
                getLog(data.taskLog);
            });
        } else{
            var url = "/task/resetAndCompleteSubLevelTask";
            var args = {"taskId":taskId,"taskName":taskName,"taskStatus":taskStatus};
            $.post(url,args,function (data) {
                if(taskStatus == '完成'){
                    $('.add-child-task').show();
                } else{
                    $('.add-child-task').hide();
                }
                if(data.result == 0){
                    layer.msg(data.msg);
                    $("#taskComplete").prop('checked',true);
                    form.render();
                    return false;
                }
                if(taskStatus == '未完成'){
                    taskStatus = '完成';
                } else if (taskStatus == '完成'){
                    taskStatus = "未完成";
                }
                getLog(data.taskLog);
            },"json");
        }
    });

    /**
     * 更新任务的名称
     */
    $('.task_name').blur(function(){
        var taskName = $('.task_name').val();
        var url = "/task/updateTaskName";
        var args = {"taskId":taskId,"taskName":taskName,"projectId":projectId};
        $.post(url,args,function(data){
            if(data.result == 0){
                layer.msg("系统异常,更新失败,请重试!");
            }
        },"json");
    })

    /**
     * 重复规则下拉框监听
     */
    form.on('select(repeat)', function(formData){
        // console.log(data.elem); //得到select原始DOM对象
        // console.log(data.value); //得到被选中的值
        // console.log(data.othis); //得到美化后的DOM对象
        var taskId = $('#taskId').val();
        var repeat = formData.value;
        var oldRepeat = $('#oldRepeat').val();
        if(repeat == oldRepeat){
            return false;
        }
        var url = "/task/updateTaskRepeat";
        $.post(url,{"taskId":taskId,"repeat": repeat},function (data) {
           if(data.result == 1){
               $('#oldRepeat').val(repeat);
               getLog(data.taskLog);
           }
        },"json");

    });

    /**
     * 提醒模式下拉框监听
     */
    form.on('select(remind)', function(formData){
        // console.log(data.elem); //得到select原始DOM对象
        // console.log(data.value); //得到被选中的值
        // console.log(data.othis); //得到美化后的DOM对象
        var taskId = $('#taskId').val();
        var remind = formData.value;
        var oldremind = $('#oldRemind').val();
        if(remind == oldremind){
            return false;
        }
        var url = "/task/updateTaskRemindTime";
        $.post(url,{"taskId":taskId,"remind": remind},function (data) {
            if(data.result == 1) {
                $('#oldRemind').val(remind);
                getLog(data.taskLog);
            }
        },"json");
    });

    /**
     * 监听任务的优先级按钮
     */
    form.on('radio(priority)', function(priorityData){
        var taskId = $('#taskId').val();
        var oldPriorty = $('#oldPriority').val();
        if(oldPriorty == priorityData.value){
            return false;
        }
        //console.log(data.elem); //得到radio原始DOM对象
        //console.log(data.value); //被点击的radio的value值
        var url = '/task/updateTaskPriority';
        var args = {"taskId":taskId,"priority":priorityData.value};
        $.post(url,args,function (data) {
           if(data.result == 1){
               getLog(data.taskLog);
               $('#oldPriority').val(priorityData.value);
           }
        },"json");
    });

    /**
     * 监听任务的模式
     */
    form.on('switch(switch-filter)', function (privacyData) {
        //console.log(privacyData.elem.checked); //开关是否开启，true或者false
        var url = '/task/settingUpPrivacyPatterns';
        var privacyPattern
        if(privacyData.elem.checked){
            privacyPattern = 1;
        } else{
            privacyPattern = 0;
        }
        var args = {"taskId":$('#taskId').val(),"privacyPattern":privacyPattern};
        $.post(url,args,function (data) {
            if(data.result == 1){
                if (privacyData.elem.checked) {
                    $(".who-can-see").text("仅自己可见")
                } else {
                    $(".who-can-see").text("所有成员可见")
                }
            }
        },"json");
    });

});

    layui.use('laydate', function () {
        var laydate = layui.laydate;

        laydate.render({
            elem: '#beginTime',
            type: 'datetime',
            format: 'yyyy-MM-dd'
        });

        laydate.render({
            elem: '#overTime',
            type: 'datetime',
            format: 'yyyy-MM-dd'
        });

        laydate.render({
            elem: '#beginTimes',
            type: 'datetime',
            format: 'yyyy-MM-dd'
            ,done: function(value, date, endDate){
                var taskId = $('#taskId').val();
                var startTime = new Date(value.toString()).getTime();
                var args = {"taskId":taskId,"startTime":startTime};
                if(value == ''){
                    var url = "/task/removeTaskStartTime";
                    $.post(url,{taskId:taskId},function (data) {
                        //完成
                    })
                } else{
                    var url = "/task/updateTaskStartAndEndTime";
                    $.post(url,args,function(data){
                        if(data.result == 1){
                            getLog(data.taskLog);
                        }
                    },"json");

                }
                //console.log(value); //得到日期生成的值，如：2017-08-18
                //console.log(date); //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
                //console.log(endDate); //得结束的日期时间对象，开启范围选择（range: true）才会返回。对象成员同上。
            }
        });
        laydate.render({
            elem: '#overTimes', //指定元素
            type: 'datetime',
            format: 'yyyy-MM-dd',
            done: function(value, date, endDate){
                var taskId = $('#taskId').val();
                var endTime = new Date(value.toString()).getTime();
                var args = {"taskId":taskId,"endTime":endTime};
                if(value == ''){
                    var url = "/task/removeTaskEndTime";
                    $.post(url,{taskId:taskId},function (data) {
                        //完成
                    });
                } else{
                    var url = "/task/updateTaskStartAndEndTime";
                    $.post(url,args,function(data){
                        if(data.result == 1) {
                            getLog(data.taskLog);
                        }
                    },"json");
                }
                console.log(value); //得到日期生成的值，如：2017-08-18
                console.log(date); //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
                console.log(endDate); //得结束的日期时间对象，开启范围选择（range: true）才会返回。对象成员同上。
            }
        });


    });
    if ($("#have-executor").val()){
        $(".who-wrap").show();
        $(".no-renling").hide()
    } else {
        $(".who-wrap").hide();
        $(".no-renling").show()
    }
    //点击 认领人 的x 号， 移出认领人 ，待认领出现
    $(".remove-who-wrap").click(function () {
        if($("#executorId").val() == null || $("#executorId").val() == ''){
            $(this).parent().hide();
            $(".no-renling").show();
            return false;
        }
        var url = "/task/removeExecutor";
        var args = {"taskId":taskId};
        $.post(url,args,function (data) {
            if(data.result == 1){
                getLog(data.taskLog);
                $('#executorId').val('');
                $('#executorName').html('');
                parent.clearExecutor(taskId);
            }
        },"json");
        $(this).parent().hide();
        if ($(".who-and-time").find(".who-wrap").css("display") == "none") {
            $(".no-renling").show();
        } else {
            $(".no-renling").hide();
        }
    });

    //点击 待认领 出现 人员名单
    $(".no-renling").click(function (e) {
        $('#titles').html('执行者');
        $('#executor').html(
            "<div class='one-people'>"+
            "<img value = '' src=''>"+
            "<span value = ''>" + '待认领' + "</span>"+
            "<i class=\"layui-icon layui-icon-ok\" style=\"font-size: 16px; color: #D1D1D1;\"></i>"+
            "</div>"
        );
        $('#noExecutor').html('');
        zxz=true;
        var url = "/task/findProjectAllMember";
        var args = {"executor": executorId, "projectId": projectId};
        //异步请求项目人员名单
        $.post(url,args,function(data){
            var content = "";
            var member = data.data;
            if(member == null){
                content += "<div class='one-people'>";
                content += "<img value = '' src='/static/image/add.png'>";
                content += "<span value = ''>" + 没有成员了 + "</span>";
                content += "<i class=\"layui-icon layui-icon-ok\" style=\"font-size: 16px; color: #D1D1D1;\"></i>";
                content += "</div>";
                $('#noExecutor').html(content);
                $(".people").show(500);
                $("#executor").removeClass("special-executor");
                return false;
            }
            for(var i = 0;i < member.length;i++){
                content += "<div class='one-people'>";
                content += "<img value = '"+ member[i].userInfo.image +"' src='"+IMAGE_SERVER+ member[i].userInfo.image +"'>";
                content += "<span value = '"+ member[i].id +"'>" + member[i].userName + "</span>";
                content += "<i class=\"layui-icon layui-icon-ok\" style=\"font-size: 16px; color: #D1D1D1;\"></i>";
                content += "</div>";
            }
            $('#noExecutor').html(content);
        });
        $(".people").show(500);
        $("#executor").removeClass("special-executor");
        e.stopPropagation()
    });

    // 点击  任务菜单出现隐藏
    $(".assignment-menu-show").click(function () {
        $(".renwu-menu").slideToggle();
    });
    $(".scheduling-menu-title img").click(function () {
        $(".renwu-menu").slideUp();
    });

    // 点击添加标签
if ($(".has-tags .tag").length==0){
    $(".no-tags").show();
    $(".has-tags").hide();
} else {
    $(".no-tags").hide();
    $(".has-tags").show();
}
    $(".no-tags").click(function (e) {
        var url = "/task/findAllTags";
        var args = {"projectId":projectId};
        //异步请求获取项目下的所有标签
        $.post(url,args,function(data){
            var tags = data.data;
            var content = "";
            for(var i = 0;i < tags.length; i++){
                content += "<li class='tags-list'>" +
                                "<span class='dot' style='background-color: " + tags[i].bgColor + "'></span>" +
                                "<span class='tag-font' value='"+ tags[i].tagId +"'>"+ tags[i].tagName +"</span>"+
                            "</li>";
            }
            $('#tags').html(content);
            $(".tags-search-build").show();
            $(".tag-search").show();
            $(".no-tags").hide();
        },"json");
        e.stopPropagation();
    });
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
        if ($(".has-tags .tag").length==0){
            $(".no-tags").show();
            $(".has-tags").hide();
        } else {
            $(".no-tags").hide();
            $(".has-tags").show();
        }

    });
    $(".has-tags>i").click(function (e) {
        $(".tags-search-build").show();
        $(".tag-search").show();

        e.stopPropagation();
    });

    // 点击某个具体标签
$("html").on("click",".tags-list",function () {
    $(".has-tags").show();
    var tagId = $(this).find(".tag-font").attr("value");
    var tags = [];
    $('.tag').each(function () {
        tags.push($(this).attr('value'));
    });
    var index = 0;
    $('.tag').each(function () {
        if(tagId == $(this).attr('value')){
            var that=$(this);
            var url = "/task/removeTaskTag";
            var args = {"tags":tags.toString(),"tagId":tagId,"taskId":taskId};
            $.post(url,args,function (data) {
                that.remove();
            },"json");
            index = 1;
        }
    });
    if(index == 1){
        return false;
    }
    var tagName = $(this).find(".tag-font").text();
    var bgColor = $(this).find(".dot").css("background-color");
    var url = "/task/addTaskTag";
    var args = {"tagId":tagId,"tagName":tagName,"taskId":taskId,"projectId":projectId};
    var content = '';
    $.post(url,args,function (data) {
        $(".no-tags").hide();
        content += '<span class="tag" value="' + tagId + '" style="background-color:' + bgColor + '">'+
            '<b style="font-weight: 400">' + tagName + '</b>'+
            '<i class="layui-icon layui-icon-close-fill" style="font-size: 14px; color: #1E9FFF;"></i>'+
            '</span>';
        $(".has-tags").prepend(content);
    },"json");

});

// 创建 按钮 是否 能点击
$(".tag-name").keyup(function () {
    if ($(this).val()==''){
        $(".tag-ok").css({"background-color":"#ccc","cursor":"not-allowed"})
    } else {
        $(".tag-ok").css({"background-color":"#1E9FFF","cursor":"pointer"})
    }
});

//点击创建 按钮
$(".tag-ok").click(function () {
    if ($(".tag-name").val()==''){
        return false
    } else {
        var content = '';
        var vals=$(".tag-name").val();
        var color='';
        $(".color-pick li i").each(function () {
            if ($(this).is(":visible")){
                color=$(this).parent().css("background-color")
            }
        });
        var url = "/task/addTagsToTask";
        var args = {"tagName":vals,"bgColor":color,"taskId":taskId,"projectId":projectId}
        $.post(url,args,function (data) {
            if(data.result > 0){
                $(".has-tags").show();
                content +=
                            '<span class="tag" value="' + data.data + '" style="background-color:' + color + '">'+
                            '<b style="font-weight: 400">' + vals + '</b>'+
                            '<i class="layui-icon layui-icon-close-fill" style="font-size: 14px; color: #1E9FFF;"></i>'+
                            '</span>';
                $('.no-tags').hide();
                $(".has-tags").prepend(content);
            } else{
                layer.msg(data.msg);
            }
        },"json");

    }
});
$(".add-fuhao").click(function () {
    $('.no-tags').trigger("click");
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

    //点击添加子任务
    $(".add-child-task span").click(function () {
        $(".click-add-child-task").slideDown(500)
    });
    $(".common-no-style").click(function () {
        $(".click-add-child-task").slideUp(500);
    });
    $(".common-ok-style").click(function () {
        var subTaskName = $('.creat-model-input').val();
        var url = "/task/addSubLevelTask";
        var args = {"taskName":subTaskName,"parentTaskId":taskId,"projectId":projectId};
        var content = '';
        $.post(url,args,function(data){
            if(data.result == 1){
                content += '<div class="child-task-list">'+
                                '<input type="checkbox" name="" title="" lay-skin="primary" lay-filter="subTask" value = '+ data.subTaskId +'>'+
                                '<input type="hidden" value="' + subTaskName + '" id="' + data.subTaskId + '" />'+
                                '<div class="child-task-con">'+
                                '<span>'+ subTaskName +'</span>'+
                                '<div class="dt">dt-41</div>'+
                                '</div>'+
                                '<i class="layui-icon layui-icon-right go-detail" style="font-size: 16px; color: #a6a6a6;"></i>'+
                                '<img class="child-task-who" src="/image/lpb.png" th:src="@{/image/lpb.png}">'+
                            '</div>';
                $('#subTask').append(content);
                getLog(data.taskLog);
                $(".click-add-child-task").slideUp(500);
                layui.use('form', function(){
                    var form = layui.form;
                    form.render(); //更新全部
                    //各种基于事件的操作，下面会有进一步介绍
                });

            }
        });
    });

    //移除任务的参与者
   $("html").on("click",".remove-work-people",function () {
       var id = $(this).prev().attr("value");
       var args = {"taskId":taskId,"uId":id};
       var url = "/task/removeTaskMember";
       $(this).parent().remove();
       $.post(url,args,function (data) {
           getLog(data.taskLog);
           //移除完成
       },"json");
   });



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
/**
 * 点击人员 弹框 确定 按钮
 */
$('.people-ok').click(function () {
    // 执行者 确定
    if (zxz){
        var id = "";
        var name = "";
        var img = "";
        for (var i=0;i<$(".one-people").length;i++){
            if ($(".one-people").eq(i).find("i").is(":visible")) {
                id = $(".one-people").eq(i).find("span").attr("value");
                name = $(".one-people").eq(i).find("span").html();
                img = $(".one-people").eq(i).find("img").attr("value");
            }
        }
        if(id == ''){
            $(".people").hide(500);
            return false;
        }
        var url = "/task/updateTaskExecutor";
        var args = {"taskId":taskId,"id":id,"image":img,"uName":name};
        $.post(url,args,function(data){
            if(data.result == 1){
                parent.changeExecutor(taskId,IMAGE_SERVER+img);
                getLog(data.taskLog);
                $('#executorId').val(id);
                $('#executorName').html(name);
                $('#executorImg').attr("src",IMAGE_SERVER+img);
                $(".no-renling").hide();
                $(".people").hide(500);
                $(".who-wrap").show();
            }
        },"json");
    }else {  //参与者 确定
        var addUserEntity=[];
        var addUserImage = [];
        var removeUserEntity=[];
        for (var i=0;i<$("#executor .one-people").length;i++){
            if ($("#executor .one-people").eq(i).find("i").is(":hidden")) {
                var value =$("#executor .one-people").eq(i).find("span").attr("value");
                removeUserEntity.push(value);
            }
        }
        for (var i=0;i<$("#noExecutor .one-people").length;i++){
            if ($("#noExecutor .one-people").eq(i).find("i").is(":visible")) {
                var value =$("#noExecutor .one-people").eq(i).find("span").attr("value");
                var image =$("#noExecutor .one-people").eq(i).find("img").attr("src");
                addUserImage.push(image);
                addUserEntity.push(value);
            }
        }
        if(addUserEntity == '' && removeUserEntity == ''){
            $(".people").hide(500);
            return false;
        }
        var url = "/task/addAndRemoveTaskMember";
        var args = {"taskId":taskId,"addUserEntity":addUserEntity.toString(),"removeUserEntity":removeUserEntity.toString()};
        var content = '';
        $.post(url,args,function (data) {
            if(data.result > 0){
                getLog(data.taskLog);
                for(var i = 0;i < addUserEntity.length;i++){
                    content += '<div class="one-work-people">'+
                            '<img src="'+ addUserImage[i] +'" value="' + addUserEntity[i] + '">'+
                            '<i class="layui-icon layui-icon-close-fill remove-work-people " style="font-size: 15px; color: #3da8f5;"></i>'+
                        '</div>';
                }
                for (var i = 0;i<removeUserEntity.length;i++){
                    $('#'+removeUserEntity[i]).remove();
                }
                $(".add-work-people").before(content);
                $(".people").hide(500);
            }
        },"json");
    }
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

    /**
     * 任务详情的时候显示的人员信息
     */
    $(".add-work-people img").click(function (e) {
        zxz=false;
        $("#executor").addClass("special-executor");
        var url = '/task/findTaskMemberInfo';
        var args = {"projectId":projectId, "taskId": taskId};
        var executor = "";
        var noExecutor = "";
        $.post(url, args, function (data) {
            var content = "";
            var userExistTask = data.userExistTask;
            var userNotExistTask = data.userNotExistTask;
            if(userExistTask != null){
                for (var i = 0;i < data.userExistTask.length;i++){
                    executor+=
                        '<div class="one-people">'+
                        '<img src="'+ IMAGE_SERVER+userExistTask[i].userInfo.image +'"/>'+
                        '<span value = "'+ userExistTask[i].id +'">'+ userExistTask[i].userName +'</span>'+
                        '<i class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;"></i>'+
                        '</div>';
                }
            } else{
                executor+=
                    '<div class="one-people">'+
                    '<img src="/image/begintime.png" th:src="@{null}" />'+
                    '<span value = "">没有参与者</span>'+
                    '<i class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;"></i>'+
                    '</div>';
            }
            if(userNotExistTask != null){
                for (var i = 0;i < data.userNotExistTask.length;i++){
                    noExecutor += "<div class=\'one-people\'>"+
                    "<img src = '" + IMAGE_SERVER+userNotExistTask[i].userInfo.image +"'>"+
                    "<span value = '"+ userNotExistTask[i].id +"'>" + userNotExistTask[i].userName + "</span>"+
                    "<i class=\'layui-icon layui-icon-ok\' style=\'font-size: 16px; color: #D1D1D1;\'></i>"+
                    "</div>";
                }
            } else{
                noExecutor += "<div class=\'one-people\'>"+
                    "<img th:src='\@{}\'>"+
                    "<span value = ''>没有成员可邀请</span>"+
                    "<i class=\'layui-icon layui-icon-ok\' style=\'font-size: 16px; color: #D1D1D1;\'></i>"+
                    "</div>";
            }
            $('#executor').html(executor);
            $('#noExecutor').html(noExecutor);
            $(".people").show(500);
        }, "json");
        e.stopPropagation();
});
    //监听任务内容的光标离开事件
    $('#remarks').blur(function(){
        var taskId = $('#taskId').val();
        var oldRemarks = $('#oldRemarks').val();
        var remarks = $('#remarks').val();
        if(remarks == oldRemarks){
            return false;
        }
        var url = "/task/upateTaskRemarks";
        var args = {"taskId":taskId,"remarks":remarks};
        $.post(url,args,function(data){
           if(data.result == 1) {
               $('#oldRemarks').val(remarks);
           }
        },"json");
     });

//监听任务内容的光标离开事件
    $('.task_name').blur(function(){
        var taskId = $('#taskId').val();
        var taskName = $('#task_name').val();
        var url = "/task/upateTaskContent";
        var args = {"taskId":taskId,"taskName":taskName};
        $.post(url,args,function(data){
            console.log(data);
        },"json");
    });



    /**
     * 添加任务操作日志的方法
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

        var log = $('#log').html();
        log += '<li class="combox">'+
            '<img src="' + IMAGE_SERVER+taskLogVO.userEntity.userInfo.image+ '" />'+
            '<span>'+ taskLogVO.content +'</span>'+
            '<div class="in-what-time"  >' + date + '</div>'+
            '</li>';
        $('#log').html(log);
    }

    /**
     * 点击x 时关闭任务详情窗口
     */
    $('.close-revise-task').click(function () {
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });

$(".tag-search-title img").click(function () {
    $(".build-tags").show();
    $(".tag-search").hide()
});
$(".go-return").click(function () {
    $(".build-tags").hide();
    $(".tag-search").show()
});
$(".close-tag").click(function () {
    $(".tags-search-build").hide()
});

/**
 * 点击 x 从任务上移除该标签
 */
$(".revise-task").on("click", ".tag i", function () {
    var tagId = $(this).parent().attr("value");
    var tags = [];
    $('.tag').each(function () {
        tags.push($(this).attr("value"));
    });
    var url = "/task/removeTaskTag";
    var args = {"tags":tags.toString(),"tagId":tagId,"taskId":taskId};
    $.post(url,args,function (data) {
        //完成
    $(this).parent().remove();
    });
    $(this).parent().remove();
    //判断 有没有标签
    if ($(".has-tags span").length == 0) {
        $(".has-tags").hide();
        $(".no-tags").show();
    } else {
        $(".has-tags").show();
        $(".no-tags").hide();
    }
});

//点击空白区域 选择标签 框 消失
$(document).click(function(event){
    var _con = $('.tags-search-build');  // 设置目标区域
    if(!_con.is(event.target) && _con.has(event.target).length === 0){ // Mark 1
        $('.tags-search-build').hide(100);     //淡出消失
    }
    if ($(".has-tags .tag").length==0){
        $(".no-tags").show()
    }
});

//点击 选择  颜色
$("html").on("click",".color-pick li",function () {
    $(".color-pick li i").hide();
    $(this).find("i").show()
});

/**
 * 给该任务点赞
 */
$('.zan img').click(function (e) {
    var url = "/task/clickFabulous";
    var args = {"taskId":taskId};
   if(e.currentTarget.className=='nozan'){
       $.post(url,args,function (data) {
           if(data.result == 1){
               var count = $('.zan').find('span').html();
               if(count == '' || count == undefined){
                   count = 0;
               }
               count = parseInt(count) +1;
               $('.zan').find('span').html(count);
           } else{
               layer.msg(data.msg);
           }
       },"json");
       $(".nozan").hide();
       $(".cancel").show()
   }else {
       var url = "/task/cancelFabulous";
       var args = {"taskId":taskId};
       $.post(url,args,function (data) {
            if(data.result > 0) {
                var count = $('.zan').find('span').html();
                count = parseInt(count) - 1;
                if(count == 0 || count == undefined){
                    $('.zan').find('span').html('');
                } else{
                    $('.zan').find('span').html(count);
                }
            }
       },"json");
        $(".nozan").show();
        $(".cancel").hide()
   }
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
    e.stopPropagation();
});

/**
 * 点击子任务右边的箭头的时候跳转到 子任务详情页
 */
$('.go-detail').click(function () {
    var childTaskId = $(this).prev().prev().attr('id');
    location.href = '/task/initTask.html?taskId='+childTaskId+'&projectId='+ projectId;
});

/**
 * 点击关联的任务
 */
$("html").on("click",'.related-rw .boxsizing .related-rw-info',function () {
    changeRenwu($(this).parent() .attr("data-id"),projectId);
});

/**
 * 点击关联的文件
 */
$("html").on("click",'.related-wj li',function () {
    location.href = "/file/fileDetail.html?fileId="+$(this).attr('data-id');
});

/**
 * 点击关联的日程
 */
$("html").on("click",'.related-rw .boxsizing .related-rw-info',function () {
    changeRenwu($(this).parent() .attr("data-id"),projectId);
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
        location.href = "/task/initTask.html?taskId="+ taskId + "&projectId=" + projectId;
    });
}

/**
 * 单击收藏任务的事件
 */
$('.renwu-menu .sc').click(function () {
    var url = "/task/collectTask";
    var args = {"taskId":taskId};
    $.post(url,args,function(data){
        if(data.result > 0){
            $(this).children('span').html("取消收藏");
        }
    },"json");
});

/**
 * 搜索标签
 */
$('.tag-search-input').keyup(function () {
    var tagName = $(this).val();
    var url = "/tag/searchTag";
    var args = {"tagName":tagName};
    $.post(url,args,function (data) {
        var tags = data.data;
        var content  = "";
        if(data.result > 0){
            $('#tags').html('');
            for(var i = 0;i < tags.length;i++){
                content += "<li class='tags-list'>" +
                "<span class='dot' style='background-color: " + tags[i].bgColor + "'></span>" +
                "<span class='tag-font' value='"+ tags[i].tagId +"'>"+ tags[i].tagName +"</span>"+
                "</li>";
            }
        }
        $('#tags').html(content);
    },"json");
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
                '<li class="boxsizing cancle" data-id="' + taskId + '" data-binding-id="' + binding[i].taskId + '">'+
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
                '<li class="boxsizing cancle" data-id="' + taskId + '" data-binding-id="' + binding[i].fileId + '">'+
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
                '<li class="boxsizing cancle" data-id="' + taskId + '" data-binding-id="' + binding[i].scheduleId + '">'+
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
                        '<li class="boxsizing cancle" data-id="' + taskId + '" data-binding-id="' + binding[i].id + '">'+
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















