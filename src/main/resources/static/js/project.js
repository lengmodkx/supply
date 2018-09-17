layui.use('element', function(){
    var element = layui.element;

    //一些事件监听
    element.on('tab(demo)', function(data){
        console.log(data);
    });
});

function stopBubbling(e) {
    e = window.event || e;
    if (e.stopPropagation) {
        e.stopPropagation();      //阻止事件 冒泡传播
    } else {
        e.cancelBubble = true;   //ie兼容
    }
}
//点击项目设置的弹出框
function setting(projectId,e){
    stopBubbling(e);
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            area:['800px','600px'],
            closeBtn: 0,
            shadeClose: true, //点击遮罩关闭
            anim: 1,  //动画 0-6
            content: '/project/projectSetting?projectId='+projectId
        });
    });
}

function cancleCollect(projectId,e){
    stopBubbling(e);
    $.post("/project/collectProject",{"projectId":projectId},function (data) {
        if(data.result===1){
            layer.msg(data.msg,{icon:1,offset:'lb',anim:2});
            window.location.href = "/project/project.html";
        }else{
            layer.msg(data.msg,{icon:5,offset:'lb',anim:2});
        }
    });
}


//点击项目进入任务界面
function projectClick(projectId){
    window.location.href = "/project/task.html?projectId="+projectId;
    return false;
}

//点击收藏项目
function projectCollect(projectId,e){
    stopBubbling(e);
    console.log(projectId);
    $.post("/project/collectProject",{"projectId":projectId},function (data) {
        if(data.result===1){
            layer.msg(data.msg,{icon:1,offset:'lb',anim:2});
            $(".complete-wrap").show();
            window.location.href = "/project/project.html";
        }else{
            layer.msg(data.msg,{icon:5,offset:'lb',anim:2});
        }
    });
}

function resetProject(projectId){
    $.post("/project/updateProject",{"projectId":projectId,"projectIdDel":0},function (data) {
        if(data.result===1){
            layer.msg(data.msg,{icon:1,offset:'lb',anim:2});
            window.location.href = "/project/project.html";
        }else{
            layer.msg(data.msg,{icon:5,offset:'lb',anim:2});
        }
    });
}

function delProject(projectId){
    $.post("/project/delProject",{"projectId":projectId},function (data) {
        if(data.result===1){
            layer.msg(data.msg,{icon:1,offset:'lb',anim:2});
            window.location.href = "/project/project.html";
        }else{
            layer.msg(data.msg,{icon:5,offset:'lb',anim:2});
        }
    });
}



$(function () {

    // console.log($(".content-list li").css("width"));
    // // 高度随屏幕大小变化
    // $(".content-list li").css("height",parseInt($(".content-list li").css("width"))*0.68 +'px' );
    //点击导航条显示对应内容
    $(".content-nav li").click(function () {
        var i=$(this).index();
        $(".content-nav li a").removeClass("selected");
        $(".content-nav li a").eq(i).addClass("selected");
        $(".content-list").hide();
        $(".content-list").eq(i).show()
    });
    //点击创建新项目
    $(".special-li").click(function () {
        newWork();
    });

    // 我收藏的项目  点击添加
    if ($(".complete-wrap li").length==0){
        $(".complete-wrap").hide()
    } else {
        $(".complete-wrap").show()
    }
    //项目回收站 显示隐藏
    $(".show-recycle-bin").click(function () {
        if ($(".recycle-bin").is(':visible')) {
            $(".recycle-bin").hide();
            $(this).text("显示")
        }else {
            $(".recycle-bin").show();
            $(this).text("隐藏")
        }
    })
});

//点击创建新项目的弹出框
function newWork(){
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: ["标准模板","text-align:center"], //标题
            area:['800px','450px'],
            shadeClose: true, //点击遮罩关闭
            anim: 1,  //动画 0-6
            content: ['/project/projectTemplate.html','no']
        });
    });
}