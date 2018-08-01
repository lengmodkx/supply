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
$("body").on("click",".show-cancel-related",function (e) {
    $(this).parents("li").find(".related-menu").slideToggle();

    e.stopPropagation();
});
$("body").on("click",".close-related-menu",function () {
    $(this).parents(".related-menu").slideUp();
});

$('.add-guanlian').click(function (e) {
    parent.layer.open({
        type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
        title: false, //标题
        area:['800px','540px'],
        fixed: true,
        shadeClose: true,
        closeBtn: 0,
        shade:  [0.1, 'black'],
        anim: 1,  //动画 0-6
        content: ['/binding/relevance.html?taskId='+taskId,'no']
    });
});
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
        $.post(url,{"taskId":taskId,"repeat": repeat,"projectId":projectId},function (data) {
           if(data.result == 1){
               $('#oldRepeat').val(repeat);
           }
        },"json");

    });

    /**
     * 提醒模式下拉框监听
     */
    form.on('select(remind)', function(formData){
        var taskId = $('#taskId').val();
        var remind = formData.value;
        var oldremind = $('#oldRemind').val();
        if(remind == oldremind){
            return false;
        }
        var url = "/task/updateTaskRemindTime";
        $.post(url,{"taskId":taskId,"remind": remind,"projectId":projectId},function (data) {
            if(data.result === 1) {
                $('#oldRemind').val(remind);
            }
        },"json");
    });

    /**
     * 监听任务的优先级按钮
     */
    form.on('radio(priority)', function(priorityData){
        var taskId = $('#taskId').val();
        var oldPriorty = $('#oldPriority').val();
        if(oldPriorty === priorityData.value){
            return false;
        }
        //console.log(data.elem); //得到radio原始DOM对象
        //console.log(data.value); //被点击的radio的value值
        var url = '/task/updateTaskPriority';
        var args = {"taskId":taskId,"priority":priorityData.value,"projectId":projectId};
        $.post(url,args,function (data) {
           if(data.result === 1){
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

    //点击 认领人 的x 号， 移出认领人 ，待认领出现
    $('body').on('click','.remove-who-wrap',function (e) {
        var url = "/task/removeExecutor";
        var args = {"taskId":taskId};
        $.post(url,args,function (data) {
            if(data.result === 1){
            }
        });
        e.stopPropagation();
    });

    //点击 待认领 出现 人员名单
    $('html').on('click','.no-renling',function (e) {
        var url = "/task/findProjectAllMember";
        var args = {"projectId": projectId,"executorId":""};
        //异步请求项目人员名单
        $.post(url,args,function(data){
            if(data.result===1){
                $('.zx_p').html('');
                $('.tj_p').html('');
                var member = data.data;
                var div = '<div class="one-people"><img src="/image/person.png"><span>待认领</span><i class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;display: block"></i></div>';
                $('.zx_p').append(div);

                for(var i = 0;i < member.length;i++){
                    var content = '<div class="one-people" id="'+member[i].id+'"><img src="'+IMAGE_SERVER+ member[i].userInfo.image +'"><span >' + member[i].userName + '</span><i class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;"></i></div>';
                    $('.tj_p').append(content);
                }
            }
        });
        $(".zx_people").show(500);
        e.stopPropagation()
    });


    $('html').on('click','.who-wrap',function (e) {

        var url = "/task/findProjectAllMember";
        var args = {"projectId": projectId,"executorId":$('#executorId').val()};
        //异步请求项目人员名单
        $.post(url,args,function(data){
            if(data.result===1){
                $('.zx_p').html('');
                $('.tj_p').html('');
                var member = data.data;
                var user  = data.user;
                var div = '<div class="one-people" id="'+user.id+'"><img src="'+IMAGE_SERVER+user.userInfo.image+'"><span>'+user.userName+'</span><i class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;display: block"></i></div>';
                $('.zx_p').append(div);

                for(var i = 0;i < member.length;i++){
                    var content = '<div class="one-people" id="'+member[i].id+'"><img src="'+IMAGE_SERVER+ member[i].userInfo.image +'"><span >' + member[i].userName + '</span><i class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;"></i></div>';
                    $('.tj_p').append(content);
                }
            }
        });
        $(".zx_people").show(500);
        e.stopPropagation()
    });


   $('html').on('click','.zx_people .one-people',function () {
       var executor=$(this).attr('id');
       var uName=$(this).find('span').text();

       if(executor===null||executor===undefined){
           $(".zx_people").hide(500);
       }else{
           $.post('/task/updateTaskExecutor',{"taskId":taskId,"executor":executor,"uName":uName},function (data) {
               $(".zx_people").hide(500);
           });
       }
   });



    // 点击  任务菜单出现隐藏
    $(".assignment-menu-show").click(function () {
        $(".renwu-menu").slideToggle();
    });
    $(".scheduling-menu-title img").click(function () {
        $(".renwu-menu").slideUp();
    });


    $(".no-tags").click(function (e) {
        console.log(parent.window.innerHeight)
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
            content: ['/tag/tag.html?projectId='+projectId+"&taskId="+taskId,'no']
        });
        e.stopPropagation();
    });

    $('html').on('click','.remove-tag',function (e) {
        var tagId = $(this).parent().attr('id');
        var url = "/task/removeTaskTag";
        var args = {"tagId":tagId,"taskId":taskId};
        $.post(url,args,function (data) {
            console.log(data);
        },"json");

        e.stopPropagation();
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
      //参与者 确定
        var memberIds=[];
        $('#executor .one-people').each(function (index,item) {
            if($(item).find('i').is(":visible")) {
                memberIds.push($(item).attr('id'));
            }
        });

        $('#noExecutor .one-people').each(function (index,item) {
            if($(item).find('i').is(":visible")){
                memberIds.push($(item).attr('id'));
            }
        });


        var url = "/task/addAndRemoveTaskMember";
        var args = {"taskId":taskId,"memberIds":memberIds.toString()};
        $.post(url,args,function (data) {
            if(data.result === 1){
                $(".people").hide(500);
            }
        },"json");

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
        $("#executor").addClass("special-executor");
        var url = '/task/findTaskMemberInfo';
        var args = {"projectId":projectId, "taskId": taskId};

        $.post(url, args, function (data) {
           if(data.result===1){
               $('#executor').html('');
               $('#noExecutor').html('');
               for(var i=0;i<data.joinInfo.length;i++){
                   var div = '<div class="one-people" id="'+data.joinInfo[i].id+'">'+
                       '<img src="'+ IMAGE_SERVER+data.joinInfo[i].userInfo.image +'"/>'+
                       '<span>'+ data.joinInfo[i].userName +'</span>'+
                       '<i class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;"></i>'+
                       '</div>';
                    $('#executor').append(div);
               }


               for(var j=0;j<data.projectMembers.length;j++){
                   var div = '<div class="one-people" id="'+data.projectMembers[j].id+'">'+
                       '<img src="'+ IMAGE_SERVER+data.projectMembers[j].userInfo.image +'"/>'+
                       '<span>'+ data.projectMembers[j].userName +'</span>'+
                       '<i class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;"></i>'+
                       '</div>';
                   $('#noExecutor').append(div);
               }
           }

           $(".people").show(500);
        });
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
        var args = {"taskId":taskId,"remarks":remarks,"projectId":projectId};
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
        var url = "/task/updateTaskName";
        var args = {"taskId":taskId,"projectId":projectId,"taskName":taskName};
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
        var date =datey+'-'+datem+'-'+dated+' '+dateh+":"+datemin;

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















