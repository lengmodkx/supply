

$(function () {

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
    layui.use('element', function(){
        var element = layui.element;

    });
    layui.use('laydate', function(){
        var laydate = layui.laydate;

        //执行一个laydate实例
        laydate.render({
            elem: '#beginTime', //指定元素
            type:'datetime',
            format:'yyyy年MM月dd日 HH时mm分ss秒'
        });
        laydate.render({
            elem: '#overTime', //指定元素
            type:'datetime',
            format:'yyyy年MM月dd日 HH时mm分ss秒'
        });
    });


    // 点击 导航（近期的事、任务、日程、文件、收藏） 跳转页面
    $(".my-nav-wrap li").click(function () {
        $(this).addClass("now").siblings().removeClass("now")
    });
    $("#click-near-thing").click(function () {
        $("#near-thing").show().siblings().hide()
    });
    $("#click-my-task").click(function () {
        $("#my-task").show().siblings().hide()
    });
    $("#click-my-scheduling").click(function () {
        $("#my-scheduling").show().siblings().hide()
    });
    $("#click-my-file").click(function () {
        $("#my-file").show().siblings().hide()
    });
    $("#click-my-collect").click(function () {
        $("#my-collect").show().siblings().hide()
    });

//点击x 关闭弹出层
    $("html").on("click",".my-toper>i",function () {
       parent.layer.closeAll()
    });


});