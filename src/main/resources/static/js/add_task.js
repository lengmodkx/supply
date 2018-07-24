/**
 * 点击添加任务按钮
 */
$("html").on("click",".add-assignment",function(e){
    e.stopPropagation();
    liId=$(this).siblings(".ul-wrap").children(".taskList").children(":first").attr("id");
    var addBox = $(this).siblings(".ul-wrap").find(".add-task-box");
    addRenwu(addBox,addBox.attr('data'));
    $(".add-task-box").hide();
    $(".add-assignment").show();
    $(this).siblings(".ul-wrap").find(".add-task-box").slideDown();
    $(this).hide();
    $(this).siblings(".ul-wrap").scrollTop($(this).siblings(".ul-wrap").find(".add-task-box").position().top+394);
});

/*添加任务*/
function addRenwu(addBox,taskMenuId) {
    var taskItem = '<div class="new-assignment boxsizing layui-form">\n' +
        '               <textarea placeholder="任务内容" class="layui-textarea rw-content boxsizing" id="taskName"></textarea>\n' +
        '               <div class="who-and-time">\n' +
        '               <input type="hidden" id="have-executor" value="'+member.id+'">\n' +
        '               <div class="who-wrap">\n' +
        '                   <img id="executorImg" src="'+IMAGE_SERVER+member.userInfo.image+'"/>\n' +
        '                   <input type="hidden" id="executorId" value="'+member.id+'"/>\n' +
        '                   <span id = "showExecutor">'+member.userName+'</span>\n' +
        '                   <i class="layui-icon layui-icon-close-fill remove-who-wrap" style="font-size: 16px; color: #1E9FFF;"></i>\n' +
        '               </div>\n' +
        '               <div class="no-renling" style="display: none">\n' +
        '                   <i class="layui-icon layui-icon-username no-people-img" style="font-size: 20px; color: #A6A6A6;"></i>\n' +
        '                   <span>待认领</span>\n' +
        '               </div>\n' +
        '            </div>\n' +
        '            <div class="show-develop">\n' +
        '               <i class="layui-icon layui-icon-more" style="font-size: 20px; color: #a6a6a6;"></i>\n' +
        '               <span>更多</span>\n' +
        '            </div>\n' +
        '            <div class="develop" style="display: none">\n' +
        '               <div class="begin-time abox">\n' +
        '                   <img src="/image/begintime.png"/>\n' +
        '                   <input type="text" class="layui-input" id="beginTime" name="startTime"  placeholder="开始时间">\n' +
        '               </div>\n' +
        '               <div class="over-time abox">\n' +
        '                   <img src="/image/begintime.png"/>\n' +
        '                   <input type="text" class="layui-input" id="overTime" name="endTime" placeholder="截止时间">\n' +
        '               </div>\n' +
        '               <div class="norepeat abox">\n' +
        '                   <img src="/image/norepeat.png">\n' +
        '                   <select name="repeat" lay-verify="" id = "repeat">\n' +
        '                       <option value="不重复">不重复</option>\n' +
        '                       <option value="每天重复">每天重复</option>\n' +
        '                       <option value="每周重复">每周重复</option>\n' +
        '                       <option value="每月重复">每月重复</option>\n' +
        '                       <option value="每年重复">每年重复</option>\n' +
        '                       <option value="工作日重复">工作日重复</option>\n' +
        '                   </select>\n' +
        '               </div>\n' +
        '               <div class="noremand abox">\n' +
        '                   <img src="/image/noremand.png">\n' +
        '                   <select name="remand" lay-verify="" id = "remand">\n' +
        '                       <option value="不提醒">不提醒</option>\n' +
        '                       <option value="任务开始时提醒">任务开始时提醒</option>\n' +
        '                       <option value="任务截止时提醒">任务截止时提醒</option>\n' +
        '                   </select>\n' +
        '               </div>\n' +
        '               <div class="pri layui-form">\n' +
        '                   <div class="layui-input-block">\n' +
        '                       <input type="radio" name="state" value="普通" title="普通" checked = "checked">\n' +
        '                       <input type="radio" name="state" value="紧急" title="紧急">\n' +
        '                       <input type="radio" name="state" value="非常紧急" title="非常紧急">\n' +
        '                   </div>\n' +
        '               </div>\n' +
        '            </div>\n' +
        '            <div class="add-tag tag-box clearfix">\n' +
        '               <img src="/image/biaoqian.png" />\n' +
        '               <span>标签</span>\n' +
        '               <div class="has-tags"></div>\n' +
        '               <span class="no-tags">添加标签</span>\n' +
        '            </div>' +
        '            <div class="tags-search-build" style="display: none">\n' +
        '               <div class="tag-search" >\n' +
        '                   <div class="tag-search-title boxsizing">\n' +
        '                       <input class="tag-search-input" type="text" placeholder="搜索标签">\n' +
        '                       <img src="/image/adds.png" />\n' +
        '                   </div>\n' +
        '                   <ul class="tags-ul boxsizing" id = "tags"></ul>\n' +
        '               </div>\n' +
        '               <!--新建标签-->\n' +
        '               <div class="build-tags" style="display: none">\n' +
        '                   <div class="build-tags-title boxsizing">\n' +
        '                       <i class="layui-icon layui-icon-left go-return" style="font-size: 15px; color: #a6a6a6;"></i>\n' +
        '                       <i class="layui-icon layui-icon-close close-tag" style="font-size: 15px; color: #a6a6a6;"></i>\n' +
        '                   </div>\n' +
        '                   <div class="build-tags-con boxsizing">\n' +
        '                       <input class="tag-name boxsizing" id="tag-name" value="" type="text" placeholder="标签名称">\n' +
        '                       <ul class="color-pick">\n' +
        '                           <li><i class="layui-icon layui-icon-ok" style="font-size: 14px; color: #fff;display: block"></i></li>\n' +
        '                           <li><i class="layui-icon layui-icon-ok" style="font-size: 14px; color: #fff;"></i></li>\n' +
        '                           <li><i class="layui-icon layui-icon-ok" style="font-size: 14px; color: #fff;"></i></li>\n' +
        '                           <li><i class="layui-icon layui-icon-ok" style="font-size: 14px; color: #fff;"></i></li>\n' +
        '                           <li><i class="layui-icon layui-icon-ok" style="font-size: 14px; color: #fff;"></i></li>\n' +
        '                           <li><i class="layui-icon layui-icon-ok" style="font-size: 14px; color: #fff;"></i></li>\n' +
        '                       </ul>\n' +
        '                       <button class="tag-ok">创建</button>\n' +
        '                   </div>\n' +
        '               </div>\n' +
        '           </div>\n'+
        '           <div class="heng-line"></div>\n' +
        '               <p class="cyz">参与者</p>\n' +
        '               <div class="work-people boxsizing clearfix">\n' +
        '                   <div class="one-work-people">\n' +
        '                       <img src="'+IMAGE_SERVER+member.userInfo.image+'">\n' +
        '                       <input type="hidden" value="'+member.id+'" />\n' +
        '                       <i class="layui-icon layui-icon-close-fill remove-work-people" style="font-size: 15px; color: #3da8f5;"></i>\n' +
        '                   </div>\n' +
        '                   <div class="add-work-people"><img src="/image/adds.png"></div>\n' +
        '               </div>\n'+
        '        <div>\n' +
        '        <input type="hidden" id = "memberId" />\n' +
        '        <div class="executor_dialog" style="display: none">\n'+
        '        <div class="people boxsizing">\n' +
        '            <input type="text" name="" placeholder="搜索" class="layui-input">\n' +
        '            <div class="peoples">\n' +
        '                <p>执行者</p>\n' +
        '                <div class="identity invisit-people">\n' +
        '                </div>\n' +
        '                <p>推荐</p>\n' +
        '                <div id = "executor" class="executor">\n' +
        '                    <!--项目成员列表-->\n' +
        '                </div>\n' +
        '            </div>\n' +
        '        </div></div>\n' +
        '    </div>\n'+
        '    <div class="heng-line"></div>\n' +
        '       <div class="secret abox layui-form">\n' +
        '           <img src="/image/suo.png">\n' +
        '           <span>隐私模式</span>\n' +
        '           <div class="who-can-see">所有成员可见</div>\n' +
        '               <div class="layui-btn layui-btn-normal new-assignment-ok" data="'+taskMenuId+'">创建</div>\n' +
        '           </div>\n' +
        '    </div>\n' +
        '    <div class="tk-people" style="display: none">\n'+
        '       <div class="people boxsizing" >\n' +
        '           <input type="text" name="" placeholder="搜索" class="layui-input">\n' +
        '           <div class="peoples">\n' +
        '               <p>参与者</p>\n' +
        '               <div class="invisit-people"></div>\n' +
        '               <p>推荐</p>\n' +
        '               <div class = "noExecutor"></div>\n' +
        '            </div>\n' +
        '       <div class="people-ok invite_people">确定</div>\n' +
        '   </div>\n'+
        '</div>';

    addBox.html(taskItem);
    layui.form.render();
}

var model = $('.model');

/*更多按钮*/
model.on('click','.show-develop',function (e) {
    e.stopPropagation();
    if($('.develop').is(':visible')){
        $('.develop').hide();
    }else{
        $('.develop').show();
    }
});


/*添加标签框*/
$('.ul-wrap').on('click','.no-tags',function () {
    var top=$(this).offset().top-250;
    var left=$(this).offset().left;
    layer.open({
        type: 1,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
        title: false, //标题
        offset: [top,left],
        area:['230px','250px'],
        fixed: false,
        shadeClose: true, //点击遮罩关闭
        shade:[0.1,'#fff'],
        closeBtn: 0,
        anim: 1,  //动画 0-6
        content: $('.tags-search-build').html(),
        success: function(layero, index){
            $(".tag-search").show();
            $(".build-tags").hide();
            $('.tags-ul').html('');
            $.post('/tag/findByProjectId',{"projectId":projectId},function (data) {
                if(data.result===1&&data.data!=null){
                    var tags = data.data;
                    for(var i=0;i<tags.length;i++){
                        var li = '<li class="tags-list">\n' +
                            '        <span class="dot" style = \'background-color:" + tags[i].bgColor + "\'></span>\n' +
                            '        <span class="tag-font" id="'+tags[i].tagId+'">'+tags[i].tagName+'</span>\n' +
                            '     </li>';
                        $('.tags-ul').append(li);
                    }
                }
            });
        }
    });

    $('.tag-search-title img').click(function () {
        $(".tag-search").hide();
        $(".build-tags").show();
    });

    $(".go-return").click(function () {
        $(".tag-search").show();
        $(".build-tags").hide();
    });
    $(".close-tag").click(function () {
        layer.closeAll();
    });
    $(".has-tags>i").click(function (e) {
        $(".tags-search-build").show();
        $(".tag-search").show();

        e.stopPropagation();
    });

    //点击颜色，颜色出现对勾
    $(".color-pick li").click(function () {
        $(this).find("i").show();
        $(this).siblings().find("i").hide();
    });

    var tagName="";
    // 创建 按钮 是否 能点击
    $(".tag-name").keyup(function () {
        tagName = $(this).val();
        if ($(this).val()==''){
            $(".tag-ok").css({"background-color":"#ccc","cursor":"not-allowed"});

        } else {
            $(".tag-ok").css({"background-color":"#1E9FFF","cursor":"pointer"});
        }
    });

    $('.tag-ok').click(function () {
        console.log(tagName);
        if (tagName==='') {
            console.log("xx");
            return false;
        }

        var content = '';
        var color='';
        $(".color-pick li i").each(function () {
            if ($(this).is(":visible")){
                color=$(this).parent().css("background-color")
            }
        });
        var url = "/tag/add";
        var args = {"tagName":tagName,"bgColor":color,"projectId":projectId}
        $.post(url,args,function (data) {
            if(data.result > 0){
                content +=
                    '<span class="tag" value="' + data.data.tagId + '" style="background-color:' + color + '">'+
                    '<b style="font-weight: 400">' + tagName + '</b>'+
                    '<i class="layui-icon layui-icon-close-fill" style="font-size: 14px; color: #1E9FFF;"></i>'+
                    '</span>';
                $(".has-tags").prepend(content);
            } else{
                layer.msg(data.msg);
            }
        },"json");
    });

    /**
     * 点击 X 时 移除掉该标签
     */
    $(".tag-box").on("click", ".tag i", function (e) {
        $(this).parent().remove();
        e.stopPropagation();

    });
});


/*添加执行者*/
model.on('click','.who-wrap',function (e) {
    var top=$(this).offset().top;
    var left=$(this).offset().left;
    addExecutor(top,left,1);
    e.stopPropagation();
});

//移除执行者
model.on('click','.remove-who-wrap',function (e) {
    $(this).parent().hide();
    if ($(".who-and-time").find(".who-wrap").is(':hidden')) {
        $(".no-renling").show();
    } else {
        $(".no-renling").hide();
    }
    e.stopPropagation();
});

/*点击待认领*/
model.on('click','.no-renling',function (e) {
    var top=$(this).offset().top;
    var left=$(this).offset().left;
    addExecutor(top,left,2);
    e.stopPropagation();
});

/*添加执行者*/
function addExecutor(top,left,flag){
    layer.open({
        type: 1,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
        title: false, //标题
        offset: [top,left],
        area:['252px','360px'],
        fixed: false,
        shadeClose: true, //点击遮罩关闭
        shade:[0.1,'#fff'],
        closeBtn: 0,
        anim: 1,  //动画 0-6
        content: $('.executor_dialog').html(),
        success:function (layero, index) {
            $('.executor').html('');
            $(".identity").html('');
            $.post('/project/findProjectMember',{"projectId":projectId},function (data) {
                if(data.result===1){
                    var div = '<img src="/image/person.png"/>' +
                        '<span>待认领</span>' +
                        '<i class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;"></i>';
                    $(".identity").append(div);

                    for(var i=0;i<data.members.length;i++){
                        var div_people = '<div class="one-people executor_item"><img src="'+IMAGE_SERVER+data.members[i].memberImg+'"/>' +
                            '<span>'+data.members[i].memberName+'</span>' +
                            '<i class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;" id="'+data.members[i].memberId+'"></i></div>';
                        $(".executor").append(div_people);
                        $(".executor_item").click(function () {
                            var that = $(this)
                            peopleConfirm(that,flag);
                        })
                    }
                }
            });
        }
    });
}

/*点击执行者确认*/
function peopleConfirm(that,flag) {
    var content="";
    var id = that.find('i').attr('id');
    var image = that.find('img').attr("src");
    var userName = that.find('span').text();
    if(flag==1){
        content+='<img src="'+image+'"/>\n' +
            '            <input type="hidden" th:id="executorId" value="'+id+'"/>\n' +
            '            <span id = "showExecutor">'+userName+'</span>\n' +
            '            <i class="layui-icon layui-icon-close-fill remove-who-wrap" style="font-size: 16px; color: #1E9FFF;"></i>';
        $('.who-wrap').html(content);
    }else{
        content+='<div class="who-wrap"><img src="'+image+'"/>\n' +
            '            <input type="hidden" th:id="executorId" value="'+id+'"/>\n' +
            '            <span id = "showExecutor">'+userName+'</span>\n' +
            '            <i class="layui-icon layui-icon-close-fill remove-who-wrap" style="font-size: 16px; color: #1E9FFF;"></i></div>';
        $('.no-renling').before(content);
        $('.no-renling').hide();
    }

    layer.closeAll();
}

//添加参与者
$('.model').on('click','.add-work-people',function () {
    var top=$(this).offset().top-360;
    var left=$(this).offset().left;
    addInvitePeople(top,left);
    $(".invite_people").click(function () {
        peopleok()
    })
});

function addInvitePeople(top,left){
    layer.open({
        type: 1,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
        title: false, //标题
        offset: [top,left],
        area:['252px','360px'],
        fixed: false,
        shadeClose: true, //点击遮罩关闭
        shade:[0.1,'#fff'],
        closeBtn: 0,
        anim: 1,  //动画 0-6
        content: $('.tk-people').html(),
        success:function (layero, index) {
            $(".invisit-people").html('');
            $(".noExecutor").html('');
            $.post('/project/findProjectMember',{"projectId":projectId},function (data) {
                if(data.result===1){
                    var div = '<img src="'+IMAGE_SERVER+data.user.userInfo.image+'"/>' +
                        '<span>'+data.user.userName+'</span>' +
                        '<i class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;"></i>';
                    $(".invisit-people").append(div);

                    for(var i=0;i<data.members.length;i++){
                        var div_people = '<div class="one-people"><img src="'+IMAGE_SERVER+data.members[i].memberImg+'"/>' +
                            '<span>'+data.members[i].memberName+'</span>' +
                            '<i class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;" id="'+data.members[i].memberId+'"></i></div>';
                        $(".noExecutor").append(div_people);
                    }
                }
            });
        }
    });
}
//添加参与者
function peopleok() {
    var content="";
    $('.noExecutor').find('.layui-icon-ok').each(function (data,item) {

        if($(item).is(":visible")){
            var id = $(item).attr('id');
            var image = $(item).siblings('img').attr("src");
            content+='<div class="one-work-people">\n' +
                '                                           <img src="'+image+'">\n' +
                '                                           <input type="hidden" value="'+id+'">\n' +
                '                                           <i class="layui-icon layui-icon-close-fill remove-work-people " style="font-size: 15px; color: #3da8f5;"></i>\n' +
                '                                       </div>';
        }
    });
    $('.add-work-people').before(content);
    layer.closeAll();
}

/**
 * 添加任务创建按钮
 */
model.on('click','.new-assignment-ok',function () {

    var taskName = $("#taskName").val();
    if(taskName===null||taskName===''||taskName===undefined){
        layer.msg("请输入任务名称",{icon:5});
        return false;
    }
    addTask($(this).attr('data'));
});

function addTask(taskMenuId) {
    //获取选中的参与者信息
    var members = [];
    $('.work-people .one-work-people').each(function () {
        members.push($(this).attr('value'));
    });

    //获取标签信息
    var tags = [];
    $('.tag').each(function () {
        tags.push($(this).attr('value'));
    });
    //设置任务的执行者
    var executor = $('#executorId').val();
    //设置任务开始时间
    var beginTime = $('#beginTime').val();
    if(beginTime != null && beginTime != ''){
        var startTime = new Date(beginTime.toString()).getTime();
    } else {
        startTime = null;
    }
    //设置任务结束时间
    var overTime = $('#overTime').val();
    if(overTime != null && overTime != ''){
        var endTime = new Date(overTime.toString()).getTime();
    } else{
        endTime = null;
    }
    //设置任务的内容
    var taskName = $("#taskName").val();
    //设置重复模式
    var repeat = $('#repeat').val();
    //设置任务提醒
    var remind = $('#remand').val();
    //设置任务优先级
    var priority = $('input[name="state"]:checked').val();
    //设置隐私模式
    var privacyPattern = "";
    if($('#privacyPattern').prop('checked')) {
        privacyPattern = "1";
    } else{
        privacyPattern = "0";
    }

    var url = "/task/saveTask";
    var args = {"startTime":startTime ,"endTime":endTime,"taskName":taskName,"repeat":repeat,"remind":remind,"priority":priority,"privacyPattern":privacyPattern,"taskMenuId":taskMenuId,"projectId" : projectId,"taskUIds":members.toString(),"executor":executor,"tagId":tags.toString()};
    $.post(url,args,function(data){
        if(data.result === 1){
            layer.msg("任务创建成功!",{icon:1});
            $("#taskName").val('');
        } else{
            layer.msg("任务创建失败!",{icon:5});
        }
    },"json");
}












