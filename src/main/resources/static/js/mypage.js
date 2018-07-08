

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
            format:'M月d日 H时:00'
        });
        laydate.render({
            elem: '#overTime', //指定元素
            type:'datetime',
            format:'M月d日 H时:00'
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
    //点击任务页面 导航
    $(".my-task-title-left>span").click(function () {
       var i=$(this).index();
       $(this).addClass("now").siblings().removeClass("now");
        $(".things").hide();
       $(".things").eq(i+1).show(400)
    });

    //点击 文件 页面 我创建的 我参与的
    $(".icreated").click(function () {
        $(".icreated").addClass("now");
        $(".ipartin").removeClass("now");
        $(".i-in").hide();
        $(".i-create").show(400);
    });
    $(".ipartin").click(function () {
        $(".icreated").removeClass("now");
        $(".ipartin").addClass("now");
        $(".i-create").hide();
        $(".i-in").show(400)
    });

    //点击收藏页面 的导航条
    $(".collect-head>span").click(function () {
        $(this).addClass("now").siblings().removeClass("now");
        $(".no-collect").fadeIn(400)

    });
    //点击日程 页面 导航
    $(".scheduling-title>span").click(function () {
        var i=$(this).index();
        $(this).addClass("now").siblings().removeClass("now");
        $(".my-scheduling").hide();
        $(".my-scheduling").eq(i).show(400)
    })

//点击x 关闭弹出层
    $("html").on("click",".my-toper>i",function () {
       parent.layer.closeAll()
    });
// 日程页面 ，编辑日程 弹框
    $(".add-scheduling .middle-box").click(function () {
        bianjiricheng()
    });
    function bianjiricheng() {
        layui.use('layer', function(){
            var layer = layui.layer;
            layer.open({
                type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
                title: false, //标题
                offset: '20px',
                area:['480px','455px'],
                fixed: false,
                shadeClose: true, //点击遮罩关闭
                closeBtn: 0,
                anim: 1,  //动画 0-6
                content: "tk-bianjiricheng.html"
            });
        });
    }
});