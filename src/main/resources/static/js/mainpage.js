
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
}

$(function () {
    $(".ul-wrap").css("max-height",parseInt($(".view").css("height"))-100+'px');
    $(document).click(function(event){
        var _con = $('.add-task-box');  // 设置目标区域
        if(!_con.is(event.target) && _con.has(event.target).length === 0){ // Mark 1
            $('.add-task-box').hide(200);     //淡出消失
            $(".add-assignment").show()
        }
    });
    $(".content-wrap-wrap").on("click",".add-task-box",function (e) {
       e.stopPropagation()
    });
    $(".content-wrap-wrap").on("click",".show-develop",function () {
        $(this).hide();
       $(this).siblings(".develop").slideDown();
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


    var el = document.getElementById("menuList");
    Sortable.create(el,{
        group:"menus",
        animation: 150 ,//动画参数

        onChoose:function f() {  //列表单元被选中的回调函数
            console.log('列表单元被选中的回调函数')

        },
        onUpdate: function (evt){ //拖拽更新节点位置发生该事件
            console.log('onUpdate.foo:', [evt.item, evt.newIndex]);

        },
        onEnd: function(evt){ //拖拽完毕之后发生该事件

        }
    });


    // 拖拽函数
    $(".taskList").each(function (data,item) {
        var el = document.getElementById($(item).attr('id'));
        Sortable.create(el,{
            group:"tasks",
            animation: 150 ,//动画参数

            onChoose:function f() {  //列表单元被选中的回调函数
                console.log('列表单元被选中的回调函数')

            },
            onUpdate: function (evt){ //拖拽更新节点位置发生该事件
                console.log('onUpdate.foo:', [evt.item, evt.newIndex]);

            },
            onEnd: function(evt){ //拖拽完毕之后发生该事件
                var oldMenuTaskId = [];
                var newMenuTaskId = [];
                var oldMenuId = $(evt.from).attr('id');
                var newMenuId = $(evt.to).attr('id');
                var taskId = $(evt.item).attr('id');
                $(evt.from).children('li').each(function(){
                   oldMenuTaskId.push($(this).attr('id'));
                });
                $(evt.to).children('li').each(function () {
                   newMenuTaskId.push($(this).attr('id'));
                });
                var url = "/task/taskOrder";
                var args = {oldMenuTaskId:oldMenuTaskId.toString(),newMenuTaskId:newMenuTaskId.toString(),oldMenuId:oldMenuId,newMenuId:newMenuId,taskId:taskId};
                $.post(url,args,function (data) {
                    //完成
                },"json");
            }
        });

    });

    firefox();



    //点击添加任务按钮
    $("html").on("click",".add-assignment",function(e){
        liId=$(this).siblings(".ul-wrap").children(".taskList").children(":first").attr("id");
        $(this).siblings(".ul-wrap").find(".add-task-box").show();
        $(this).hide();
        $(this).siblings(".ul-wrap").scrollTop($(this).siblings(".ul-wrap").find(".add-task-box").position().top)
        // addRenwu($('.add-assignment').attr("data"),$(this).siblings('.ul-wrap').children("ul").attr("id"),liId);
        if($(".rw-content").val()==""){
            $(".new-assignment-ok").css({"background-color":"gray","cursor":"auto"})
        }
        e.stopPropagation()
    });


    //任务内容不为空时，创建任务按钮才可用
    $(".rw-content").change(function () {
        if($(this).val()==''){
            $(".new-assignment-ok").css({"background-color":"gray","cursor":"auto"})
        }else {
            $(".new-assignment-ok").css({"background-color":"#017ECA","cursor":"pointer"})
        }
    });
    //创建任务按钮 点击事件
    $(".new-assignment-ok").click(function (e) {
        if($(this).parents(".add-task-box").find(".rw-content").val()==""){
            $(this).parents(".add-task-box").find(".rw-content").focus();
            e.preventDefault();
        }else {
            // 去掉时间前面的 年, 分钟，秒。
            var starTime=$("#beginTime").val().slice(5).substr(0,10);
            var overTime=$("#overTime").val().slice(5).substr(0,10);
            //一条新的任务
            $(this).parents(".add-task-box").siblings(".taskList").append('<li class="assignment layui-form">\n' +
                '            <div class="tags boxsizing">\n' +
                '                <span class="one-tag">'+ $(".add-bq input").val()+'</span>\n' +
                '            </div>\n' +
                '            <div class="assignment-top-box boxsizing">\n' +
                '                <input type="checkbox" name="" title="" lay-skin="primary" class="is-sure" >\n' +
                '                <span class="assignment-title">'+$(this).parents(".add-task-box").find(".rw-content").val()+'</span>\n' +
                '                <img class="assignment-tx" src="" >\n' +
                '            </div>\n' +
                '            <div class="assignment-bottom-box">\n' +
                '                <span class="timer">'+starTime+'-'+overTime+'</span>\n' +
                '                <span class="dates">dt-12</span>\n' +
                '            </div>\n' +
                '            <!--//左边框线-->\n' +
                '            <div class="line"></div>\n' +
                '        </li>');
            layer.closeAll('page');
            useLayerForm();
          if ($(".add-bq").val()==''){
              $(".tags").hide();
          }
        }
    });




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
            ulIdNum++;
            var ulId='list'+ulIdNum;
            var modelTitle=$(".creat-model-input").val();
            var url = "/relation/addMenu";
            var args = {"parentId":groupId,"relationName":modelTitle};
            $.post(url,args,function (data) {

            },"json");
            // 新 的 任务列表

            firefox();
            // 拖拽函数
            Sortable.create(document.getElementById(ulId),{
                group:"words",
                animation: 150 //动画参数
            });

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
     lbmenu(top,left)
});
function lbmenu(top,left) {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: [top,left],
            area:['250px','150px'],
            fixed: true,
            shadeClose: true,
            closeBtn: 0,
            shade: 0,
            anim: 1,  //动画 0-6
            content: ['menuList.html','no']
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
//添加任务 弹框界面
function addRenwu(projectId,taskMenuId,liId) {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: '20px',
            area:['600px','600px'],
            fixed: false,
            shadeClose: true, //点击遮罩关闭
            anim: 1,  //动画 0-6
            content: 'addtask.html?projectId='+projectId + '&taskMenuId='+ taskMenuId
        });
    });
};
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