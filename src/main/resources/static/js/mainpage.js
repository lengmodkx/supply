
var liId;
// 火狐浏览器兼容
function firefox() {
    if(isFirefox=navigator.userAgent.indexOf("Firefox")>0){
        $(".xjt").css("margin-top","-18px")
    }
};
//使用layui的form表单
function useLayerForm(){
    layui.use('form', function(){
        var form = layui.form;

        //监听提交
        form.on('submit(formDemo)', function(data){
            layer.msg(JSON.stringify(data.field));
            return false;
        });

        form.on('switch(switch-filter)', function(data){
            console.log(data.elem.checked); //开关是否开启，true或者false
            if (data.elem.checked) {
                $(".who-can-see").text("仅自己可见")
            }else {
                $(".who-can-see").text("所有成员可见")
            }
        });
    });
};

$(function () {
    $(".ul-wrap").css("max-height",parseInt($(".view").css("height"))-100+'px');
    $(".content-wrap-wrap").click(function(event){
        var _con = $('.add-task-box');  // 设置目标区域
        if(!_con.is(event.target) && _con.has(event.target).length === 0){ // Mark 1
            $('.add-task-box').slideUp(200);     //淡出消失
            $(".add-assignment").show();
        }
    });


    $("html").on("click",".assignment-top-box",function (e) {
        e.stopPropagation()
        return false
    });

    var url = window.location.href;
    if(url.indexOf("task") > 0){
        $(".toper-nav ul li:nth-of-type(1)").addClass("now").siblings().removeClass("now");
    }

    useLayerForm();

    layui.use('laydate', function(){
        var laydate = layui.laydate;

        //执行一个laydate实例
        laydate.render({
            elem: '#beginTime', //指定元素
            type:'datetime',
            format:'M月d日 H:00'
        });
        laydate.render({
            elem: '#overTime', //指定元素
            type:'datetime',
            format:'M月d日 H:00'
        });
    });


    $('.sortable').sortable({
        cursor:"move",
        items:'.tile',
        axis: 'x',
        tolerance: 'pointer',
        placeholder: 'model',
        stop:function (event,ui) {
            console.log(ui);
            var ids = $('.sortable').sortable('toArray');
            $.post("/project/updateMenusOrder",{"ids":ids.toString()},function (data) {

            });
        }
    });

    $(".taskList").each(function (data,item) {
        var sortable = $(item);
        sortable.sortable({
            cursor:"move",
            items:'.task-card-mode',
            tolerance: 'pointer',
            placeholder: 'task-card-mode2',
            forcePlaceholderSize: true,
            connectWith:'.taskList',
            receive: function(event, ui) {
              console.log(ui);
              var item = ui.item;
              var menuId = ui.item.parent().attr('id');
              var taskIds = sortable.sortable('toArray').toString();
              console.log(menuId);
              var url = "/task/taskOrder";
              var params = {"taskId":item.context.id,"menuId":menuId,"taskIds":taskIds};
              $.post(url,params,function (data) {
                  console.log(data);
              });
            }
        });
    });

    firefox();


    var ulIdNum=3;   //ul列表的id 用于各列间相互拖拽
    //点击 新建任务列表
    $(".noclick-creat-model").click(function () {
        $(this).hide();
        $(".click-creat-model").show();
    });
    $(".creat-model-no").click(function () {
        $(".click-creat-model").hide();
        $(".noclick-creat-model").show();
    });

    //点击 新建任务列表的 保存按钮
    $(".creat-model-ok").click(function () {
        if ($(".creat-model-input").val()==''){
            $(".creat-model-input").focus();
            return false
        } else {
            $(".click-creat-model").hide();
            $(".noclick-creat-model").show();
            var modelTitle=$(".creat-model-input").val();
            var url = "/relation/addMenu";
            var args = {"parentId":groupId,"relationName":modelTitle};
            $.post(url,args,function (data) {
                console.log(data);
            },"json");
            // 新 的 任务列表
            firefox();
        }
    });

});

//群组 部分 的js
//点击 关闭按钮  关闭弹出层
$(".close-people").click(function () {
    layer.close(layer.index)
});
$(".close-tk").click(function () {
    layer.closeAll('page');
});

//点击 任务列表顶部 下箭头，弹出框
$("html").on("click",".add-new-model",function(){
    var top=$(this).offset().top +20 +'px';
    var left=$(this).offset().left -125 +'px';
    var menuId = $(this).attr('id');
    var menuName = $(this).siblings('.relationName').text();
    lbmenu(top,left,menuId,menuName);
});
function lbmenu(top,left,menuId,menuName) {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: [top,left],
            area:['250px','400px'],
            fixed: true,
            shadeClose: true,
            closeBtn: 0,
            shade: 0,
            anim: 1,  //动画 0-6
            content: ['menuList.html?menuId='+menuId+"&menuName="+menuName,'no']
        });
    });
};

//点击 具体 任务 出现修改任务 弹窗

function updateTask(taskId,projectId){
    changeRenwu(taskId,projectId);
    $(".publish-bottom img:nth-of-type(1)").click(function () {
        $(".fujian-box").slideDown()
    });
    $(".close-fujian").click(function () {
        $(".fujian-box").slideUp()
    });
}

// 点击头像 ，弹出框
$(".head-photo").click(function () {
    var top=$(this).offset().top +40 +'px';
    var left=$(this).offset().left -130 +'px';
    headphoto(top,left)
});
function headphoto(top,left) {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset:[top,left],
            area:['230px','345px'],
            fixed: true,
            shadeClose: true,
            closeBtn: 0,
            shade: 0,
            anim: 1,  //动画 0-6
            content: ['tk-click-touxiang.html','no']
        });
    });
};
//点击我的 弹出 我的页面框
$("#mine").click(function () {
    mypage();
});
function mypage() {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: '50px',
             area:['100%','100%'],
            fixed: true,
            shadeClose: true,
            closeBtn: 0,
            shade: 0,
            anim: 1,  //动画 0-6
            content: ['mypage.html','no']
        });
    });
};
//日历 弹框
$("").click(function () {
    calendar()
});
function calendar() {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: '50px',
            area:['100%','100%'],
            fixed: true,
            shadeClose: true,
            closeBtn: 0,
            shade: 0,
            anim: 1,  //动画 0-6
            content: ['tk-calendar.html','no']
        });
    });
}
// 群组 弹框
$(".team").click(function () {
    var left=$(window).width()-350;
    var top=$(window).height()-100;
    groups(top,left)
    // $(".group-box").css("height",$(window).height()-100+'px');
    // $(".group-box").addClass("now");
    // $("html,body").css("overflow","hidden")

});
// $(".group-title .close-tk").click(function () {
//     $(".group-box").removeClass("now");
//     $("html,body").css("overflow","auto")
// })
function groups(top,left) {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 1,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: ['100px',left],
            area:['350px',top+'px'],
            fixed: true,
            shadeClose: true,
            closeBtn: 0,
            shade: 0,
            anim: 0,  //动画 0-6
            content: $(".group-box")
        });
    });
}
// 群组 弹框 里面的 人员信息 弹框
// $("html").on("click",".group-people",function () {
//     var top=$(this).offset().top-100;
//     var left=$(this).offset().left-320;
//     groupPeople(top,left)
// });
// function groupPeople(top,left) {
//     layui.use('layer', function(){
//         var layer = layui.layer;
//         layer.open({
//             type: 1,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
//             title: false, //标题
//             offset: [top,left],
//             area:['320px','250px'],
//             fixed: true,
//             shadeClose: true,
//             closeBtn: 0,
//             shade: 0,
//             anim: 1,  //动画 0-6
//             content: $(".people-info")
//         });
//     });
// }
//群组 人员  下箭头 弹框
$("body").on("click",".group-people>li>i",function (e) {
    e.stopPropagation();
    var top=$(this).offset().top-110;
    var left=$(this).offset().left-280;
    var temp = $(this).attr('id');
    console.log(temp);
    groupPeopleRemove(top,left,temp);

});
function groupPeopleRemove(top,left,nodeName) {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: [top,left],
            area:['280px','185px'],
            fixed: true,
            shadeClose: true,
            closeBtn: 0,
            shade: 0,
            anim: 1,  //动画 0-6
            content: '/project/removePeople.html?nodeName='+nodeName
        });
    });
}
//群组 邀请成员 弹框
//点击邀请新成员
$(".add-new-member").click(function (e) {
    $('.invitation').html('');
    yaoqing();
    e.stopPropagation()
});
function yaoqing() {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 1,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: "邀请新成员", //标题
            offset: '150px',
            fixed: false,
            shadeClose: true, //点击遮罩关闭
            anim: 1,  //动画 0-6
            content: $(".search_prople")
        });
    });
}
//添加任务

//点击添加任务按钮
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



function addRenwu(addBox,taskMenuId) {
    var taskItem = ' <div class="new-assignment boxsizing layui-form">\n' +
        '                                    <textarea placeholder="任务内容" class="layui-textarea rw-content boxsizing" id="taskName"></textarea>\n' +
        '                                   <div class="who-and-time">\n' +
        '                                       <input type="hidden" id="have-executor" value="'+member.id+'">\n' +
        '                                       <div class="who-wrap">\n' +
        '                                           <input name="" value="" type="hidden"/>\n' +
        '                                           <img id="executorImg" src="'+IMAGE_SERVER+member.userInfo.image+'"/>\n' +
        '                                           <input type="hidden" id="executorId" value="'+member.id+'"/>\n' +
        '                                           <input type="hidden" id="executorName" value="'+member.userName+'"/>\n' +
        '                                           <span id = "showExecutor">'+member.userName+'</span>\n' +
        '                                           <i class="layui-icon layui-icon-close-fill remove-who-wrap" style="font-size: 16px; color: #1E9FFF;"></i>\n' +
        '                                       </div>\n' +
        '                                       <div class="no-renling" style="display: none">\n' +
        '                                           <i class="layui-icon layui-icon-username no-people-img" style="font-size: 20px; color: #A6A6A6;"></i>\n' +
        '                                           <span>待认领</span>\n' +
        '                                       </div>\n' +
        '                                   </div>\n' +
        '                                   <div class="show-develop">\n' +
        '                                       <i class="layui-icon layui-icon-more" style="font-size: 20px; color: #a6a6a6;"></i>\n' +
        '                                       <span>更多</span>\n' +
        '                                   </div>\n' +
        '                                   <div class="develop" style="display: none">\n' +
        '                                       <div class="begin-time abox">\n' +
        '                                           <img src="/image/begintime.png"/>\n' +
        '                                           <input type="text" class="layui-input" id="beginTime" name="startTime"  placeholder="开始时间">\n' +
        '                                       </div>\n' +
        '                                       <div class="over-time abox">\n' +
        '                                           <img src="/image/begintime.png"/>\n' +
        '                                           <input type="text" class="layui-input" id="overTime" name="endTime" placeholder="截止时间">\n' +
        '                                       </div>\n' +
        '                                       <div class="norepeat abox">\n' +
        '                                           <img src="/image/norepeat.png">\n' +
        '                                           <select name="repeat" lay-verify="" id = "repeat">\n' +
        '                                               <option value="不重复">不重复</option>\n' +
        '                                               <option value="每天重复">每天重复</option>\n' +
        '                                               <option value="每周重复">每周重复</option>\n' +
        '                                               <option value="每月重复">每月重复</option>\n' +
        '                                               <option value="每年重复">每年重复</option>\n' +
        '                                               <option value="工作日重复">工作日重复</option>\n' +
        '                                           </select>\n' +
        '                                       </div>\n' +
        '                                       <div class="noremand abox">\n' +
        '                                           <img src="/image/noremand.png">\n' +
        '                                           <select name="remand" lay-verify="" id = "remand">\n' +
        '                                               <option value="不提醒">不提醒</option>\n' +
        '                                               <option value="任务开始时提醒">任务开始时提醒</option>\n' +
        '                                               <option value="任务截止时提醒">任务截止时提醒</option>\n' +
        '                                           </select>\n' +
        '                                       </div>\n' +
        '                                       <div class="pri layui-form">\n' +
        '                                           <div class="layui-input-block">\n' +
        '                                               <input type="radio" name="state" value="普通" title="普通" checked = "checked">\n' +
        '                                               <input type="radio" name="state" value="紧急" title="紧急">\n' +
        '                                               <input type="radio" name="state" value="非常紧急" title="非常紧急">\n' +
        '                                           </div>\n' +
        '                                       </div>\n' +
        '                                   </div>\n' +
        '                                   <div class="add-tag tag-box clearfix">\n' +
        '                                           <img src="/image/biaoqian.png" />\n' +
        '                                           <span>标签</span>\n' +
        '                                           <span class="no-tags">添加标签</span>\n' +
        '                                           <div class="has-tags"></div>\n' +
        '                                   </div><div class="tags-search-build" style="display: none">\n' +
        '            <!--搜索标签-->\n' +
        '            <div class="tag-search" >\n' +
        '                <div class="tag-search-title boxsizing">\n' +
        '                    <input class="tag-search-input" type="text" placeholder="搜索标签">\n' +
        '                    <img src="/image/adds.png" />\n' +
        '                </div>\n' +
        '                <ul class="tags-ul boxsizing" id = "tags">\n' +
        '                </ul>\n' +
        '            </div>\n' +
        '            <!--新建标签-->\n' +
        '            <div class="build-tags" style="display: none">\n' +
        '                <div class="build-tags-title boxsizing">\n' +
        '                    <i class="layui-icon layui-icon-left go-return" style="font-size: 15px; color: #a6a6a6;"></i>\n' +
        '                    新建标签\n' +
        '                    <i class="layui-icon layui-icon-close close-tag" style="font-size: 15px; color: #a6a6a6;"></i>\n' +
        '                </div>\n' +
        '                <div class="build-tags-con boxsizing">\n' +
        '                    <input class="tag-name boxsizing" type="text" placeholder="标签名称">\n' +
        '                    <ul class="color-pick">\n' +
        '                        <li>\n' +
        '                            <i class="layui-icon layui-icon-ok" style="font-size: 14px; color: #fff;display: block"></i>\n' +
        '                        </li>\n' +
        '                        <li>\n' +
        '                            <i class="layui-icon layui-icon-ok" style="font-size: 14px; color: #fff;"></i>\n' +
        '                        </li>\n' +
        '                        <li>\n' +
        '                            <i class="layui-icon layui-icon-ok" style="font-size: 14px; color: #fff;"></i>\n' +
        '                        </li>\n' +
        '                        <li>\n' +
        '                            <i class="layui-icon layui-icon-ok" style="font-size: 14px; color: #fff;"></i>\n' +
        '                        </li>\n' +
        '                        <li>\n' +
        '                            <i class="layui-icon layui-icon-ok" style="font-size: 14px; color: #fff;"></i>\n' +
        '                        </li>\n' +
        '                        <li>\n' +
        '                            <i class="layui-icon layui-icon-ok" style="font-size: 14px; color: #fff;"></i>\n' +
        '                        </li>\n' +
        '                    </ul>\n' +
        '                    <div class="tag-ok" disabled>创建</div>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '        </div>\n'+
        '                                   <div class="heng-line"></div>\n' +
        '                                   <p class="cyz">参与者</p>\n' +
        '                                   <div class="work-people boxsizing clearfix">\n' +
        '                                       <div class="one-work-people" value="'+member.id+'">\n' +
        '                                           <img src="'+IMAGE_SERVER+member.userInfo.image+'">\n' +
        '                                           <input type="hidden" value="'+member.id+'" />\n' +
        '                                           <i class="layui-icon layui-icon-close-fill remove-work-people "\n' +
        '                                              style="font-size: 15px; color: #3da8f5;"></i>\n' +
        '                                       </div>\n' +
        '                                       <div class="add-work-people ">\n' +
        '                                           <img src="/image/adds.png">\n' +
        '                                       </div>\n' +
        '                                   </div>\n'+
        '        <div>\n' +
        '        <input type="hidden" id = "memberId" />\n' +
        '        <div class="people boxsizing" style="display: none" >\n' +
        '            <input type="text" name="" placeholder="搜索" class="layui-input">\n' +
        '            <div class="peoples">\n' +
        '                <p id = "identity">参与者</p>\n' +
        '                <div id = "members" >\n' +
        '                </div>\n' +
        '                <p>推荐</p>\n' +
        '                <div id = "executor">\n' +
        '                    <!--项目成员列表-->\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div id="people-ok" class="people-ok">确定</div>\n' +
        '        </div>\n' +
        '    </div>\n'+
        '                                   <div class="heng-line"></div>\n' +
        '                                   <div class="secret abox layui-form">\n' +
        '                                       <img src="/image/suo.png">\n' +
        '                                       <span>隐私模式</span>\n' +
        '                                       <div class="who-can-see">所有成员可见</div>\n' +
        '                                       <div class="layui-btn layui-btn-normal new-assignment-ok" data="'+taskMenuId+'">创建</div>\n' +
        '                                   </div>\n' +
        '                               </div>\n' +
        '<div class="tk-people" style="display: none">\n'+
        '<div class="people boxsizing" >\n' +
        '    <input type="text" name="" placeholder="搜索" class="layui-input">\n' +
        '    <div class="peoples">\n' +
        '        <div>\n' +
        '            <p>参与者</p>\n' +
        '            <div class="invisit-people">\n' +
        '            </div>\n' +
        '        </div>\n' +
        '        <p>推荐</p>\n' +
        '        <div class = "noExecutor">\n' +
        '\n' +
        '        </div>\n' +
        '    </div>\n' +
        '    <div class="people-ok">确定</div>\n' +
        '</div>\n'+
        '</div>';

        addBox.html(taskItem);
        layui.form.render();
};

//标签 框
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
            $('.tags-ul').html('');
            $.post('/tag/findByProjectId',{"projectId":projectId},function (data) {
                if(data.result===1&&data.data!=null){
                    for(var i=0;i<data.data.length;i++){
                        var li = '<li class="tags-list">\n' +
                            '        <span class="dot"></span>\n' +
                            '        <span class="tag-font">'+data.data[i]+'</span>\n' +
                            '     </li>';
                        $('.tags-ul').append(li);
                    }
                }
            });
        }
    });
});


$('.model').on('click','.add-work-people ',function () {
    var top=$(this).offset().top-360;
    var left=$(this).offset().left;
    people(top,left)
});
// 人员 框
function people(top,left){
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
                            '<i class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;"></i></div>';
                        $(".noExecutor").append(div_people);
                    }
                }
            });
        }
    });
}

$('.model').on('click','.new-assignment-ok',function () {

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

//修改任务 弹框界面
function changeRenwu(taskId,projectId) {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
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
//点击菜单，打开项目菜单区域
$(".menu").click(function () {
    var top=$(this).offset().top +40 +'px';
    var right=$(window).width()-$(this).offset().left;
    var left=$(this).offset().left -380 +right+'px';
    projectMenu(top,left)
});
function projectMenu(top,left) {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: [top,left],
            area:['380px','500px'],
            fixed: true,
            shadeClose: true,
            closeBtn: 0,
            shade: 0,
            anim: 1,  //动画 0-6
            content: ['tk-xiangmucaidan.html','no']
        });
    });
};

// 点击 帮助 弹出 帮助弹框
$("#help").click(function () {
    var top=$(this).offset().top +30 +'px';
    var left=$(this).offset().left -120 +'px';
    help(top,left)
});
function help(top,left) {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset:[top,left],
            area:['230px','300px'],
            fixed: true,
            shadeClose: true,
            closeBtn: 0,
            shade: 0,
            anim: 1,  //动画 0-6
            content: ['tk-help.html','no']
        });
    });
};