

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


    useLayerForm();

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

    // 拖拽函数
    $(".taskList").each(function (data,item) {
        console.log($(item).attr('id'));
        var el = document.getElementById($(item).attr('id'));
        Sortable.create(el,{
            group:"words",
            animation: 150 //动画参数
        });
    });



    firefox();
//点击导航（设计任务、分享、文件、群聊、统计） 静态切换
    $(".toper-nav li").click(function () {
        $(this).addClass("now").siblings("li").removeClass("now")
    });
    $(".click-file").click(function () {
        $(".view").hide();
        $("#file").show().siblings().hide()
    });
    $(".click-task").click(function () {
        $(".view").show();
        $(".board-view>div").hide()
    });
    $(".click-share").click(function () {
        $(".view").hide();
        $("#share").show().siblings().hide()
    });
    $(".click-date").click(function () {
        $(".view").hide();
        $("#date").show().siblings().hide()
    });
    $(".click-chat").click(function () {
        $(".view").hide();
        $("#chat").show().siblings().hide()
    });


    //点击添加任务按钮
    $("html").on("click",".add-assignment",function(){
        addRenwu();
        if($(".rw-content").val()==""){
            $(".new-assignment-ok").css({"background-color":"gray","cursor":"auto"})
        }
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

        if($(".rw-content").val()==""){
            $(".rw-content").focus();
            e.preventDefault();
        }else {
            // 去掉时间前面的 年, 分钟，秒。
            var starTime=$("#beginTime").val().slice(5).substr(0,10);
            var overTime=$("#overTime").val().slice(5).substr(0,10);
            //一条新的任务
            that.siblings(".ul-wrap").children("ul").append('<li class="assignment layui-form">\n' +
                '            <div class="tags boxsizing">\n' +
                '                <span class="one-tag">'+ $(".add-bq input").val()+'</span>\n' +
                '            </div>\n' +
                '            <div class="assignment-top-box boxsizing">\n' +
                '                <input type="checkbox" name="" title="" lay-skin="primary" class="is-sure" >\n' +
                '                <span class="assignment-title">'+$(".rw-content").val()+'</span>\n' +
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

    // 点击 具体 任务 出现修改任务 弹窗
    $("html").on("click",".assignment",function(){
        changeRenwu();
        $(".publish-bottom img:nth-of-type(1)").click(function () {
           $(".fujian-box").slideDown()
        });
        $(".close-fujian").click(function () {
            $(".fujian-box").slideUp()
        })
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
            // 新 的 任务列表
            $(".creat-model").before('<div class="model boxsizing">\n' +
                '        <div class="model-title">\n' +
                '            <span> ' + modelTitle + ' </span>\n' +
                '            <i class="layui-icon layui-icon-down add-new-model xjt" style="font-size: 15px; color: #B5B5B5;" title="添加或编辑任务列表"></i>\n' +
                '            <!--点击下箭头出现的内容-->\n' +
                '            <div class="lbmenu">\n' +
                '                <div class="lbmenu-title">\n' +
                '                    <p>菜单列表</p>\n' +
                '                    <i class="layui-icon layui-icon-close lbmenu-close" style="font-size: 16px; color: #A6A6A6;"></i>\n' +
                '                </div>\n' +
                '                <div class="lbmenu-add">\n' +
                '                    <div class="lbmenu-add-content">\n' +
                '                        <i class="layui-icon layui-icon-add-1 " style="font-size: 16px; color: gray;"></i>\n' +
                '                        在此处添加新列表\n' +
                '                    </div>\n' +
                '                </div>\n' +
                '                <div class="lbmenu-remove">\n' +
                '                    <div class="lbmenu-add-content">\n' +
                '                        <i class="layui-icon layui-icon-delete " style="font-size: 16px; color: gray;"></i>\n' +
                '                        本列表所有任务移到回收站\n' +
                '                    </div>\n' +
                '                </div>\n' +
                '            </div>\n' +
                '        </div>\n' +
                '        <!--任务列表-->\n' +
                '        <div class="ul-wrap layui-form">\n' +
                '        <ul id="'+ulId+'">\n' +
                '        </ul>\n' +
                '        </div>\n' +
                '        <!--添加任务按钮-->\n' +
                '        <div class="add-assignment">\n' +
                '            <i class="layui-icon layui-icon-add-circle add-icon" style="font-size: 22px; color: #80BEE4;"></i>\n' +
                '            <span>添加任务</span>\n' +
                '        </div>\n' +
                '\n' +
                '    </div>');
            firefox();
            // 拖拽函数
            Sortable.create(document.getElementById(ulId),{
                group:"words",
                animation: 150 //动画参数
            });

        }
    });

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
//添加任务 弹框界面
function addRenwu() {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: '20px',
            area:['460px','600px'],
            fixed: false,
            shadeClose: true, //点击遮罩关闭
            anim: 1,  //动画 0-6
            content: 'addtask.html'
        });
    });
};
//修改任务 弹框界面
function changeRenwu() {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: '20px',
            area:['500px','600px'],
            fixed: false,
            shadeClose: true, //点击遮罩关闭
            closeBtn: 0,
            anim: 1,  //动画 0-6
            content: "reviserask.html"
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
