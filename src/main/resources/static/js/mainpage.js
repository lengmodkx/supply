
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
        /**
         * 监听任务是否完成的复选框
         */
        form.on('checkbox(finish_task)', function(data){
            var url = "/task/resetAndCompleteTask";
            var args = {"taskId" : data.value};
            var taskId = data.value;
            $.post(url,args,function(data){
                if(data.result == 0){
                    layer.msg(data.msg);
                    $('.task-check-box input[type = "checkbox"]').each(function (){
                       if($(this).attr('value') == taskId){
                           $(this).prop("checked",false);
                           form.render();
                       }
                    });
                }
            },"json");
            console.log(data.elem); //得到checkbox原始DOM对象
            console.log(data.elem.checked); //是否被选中，true或者false
            console.log(data.value); //复选框value值，也可以通过data.elem.value得到
            console.log(data.othis); //得到美化后的DOM对象
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
        handle:'.model-title',
        axis: 'x',
        tolerance: 'pointer',
        placeholder: 'model',
        stop:function (event,ui) {
            console.log(ui);
            var ids = $('.sortable').sortable('toArray');
            $.post("/project/updateMenusOrder",{"ids":ids.toString()},function (data) {
                console.log(data);
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
              var item = ui.item;
              var menuId = ui.item.parent().attr('id');
              var taskIds = sortable.sortable('toArray').toString();
              console.log(menuId);
              var url = "/task/taskOrder";
              var params = {"taskId":item.context.id,"menuId":menuId,"taskIds":taskIds};
              $.post(url,params,function (data) {
                  console.log(data);
              });
            },
            stop:function (event,ui) {
                var taskIds = sortable.sortable('toArray').toString();
                var url = "/task/taskOrder";
                var params = {"taskId":"","menuId":"","taskIds":taskIds};
                $.post(url,params,function (data) {
                    console.log(data);
                });
            }
        });
    });


    firefox();


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
            var modelTitle=$(".creat-model-input").val();
            var url = "/relation/addMenu";
            var args = {"projectId":projectId,"parentId":groupId,"relationName":modelTitle};
            $.post(url,args,function (data) {
                if(data.result===1){
                    $(".click-creat-model").hide();
                    $(".noclick-creat-model").show();
                }
            },"json");
            // 新 的 任务列表
            firefox();
        }
    });

});



//点击 任务列表顶部 下箭头，弹出框
var menuflag = true;
$("html").on("click",".add-new-model",function(){
    if(menuflag){
        var top=$(this).offset().top +20 +'px';
        var left=$(this).offset().left -125 +'px';
        var menuId = $(this).attr('id');
        var menuName = $(this).siblings('.relationName').text();
        lbmenu(top,left,menuId,menuName);
        menuflag = false;
    }else{
        menuflag =true;
        layer.closeAll();
    }

});
function lbmenu(top,left,menuId,menuName) {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: [top,left],
            area:['250px','152px'],
            fixed: true,
            shadeClose: true,
            closeBtn: 0,
            shade: ['0.1','#fff'],
            anim: 1,  //动画 0-6
            content: ['menuList.html?menuId='+menuId+"&menuName="+menuName+"&projectId="+projectId,'no']
        });
    });
};

//点击 具体 任务 出现修改任务 弹窗
$('.ul-wrap').on('click','.task-content-set',function () {
    var taskId = $(this).attr('data');
    var projectId = $(this).attr('data-value');
    changeRenwu(taskId,projectId);
    $(".publish-bottom img:nth-of-type(1)").click(function () {
        $(".fujian-box").slideDown()
    });
    $(".close-fujian").click(function () {
        $(".fujian-box").slideUp()
    });
});


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