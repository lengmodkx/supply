
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
$(function () {

    layui.use('form', function(){
        var form = layui.form;

        /**
         * 监听任务的完成状态下拉框
         */
        form.on('select(task)', function (data) {
            var status = '';
            var type = '';
            var url = '';
            if ($('#status').val() == '1') {
                status = '未完成';
            } else {
                status = '完成';
            }
            var args = {"status": status, "orderType": $('#orderType').val()};
            $('.my-task-title-left').children('span').each(function () {
                if ($(this).attr("class") == 'now') {
                    type = $(this).html();
                }
            });
            if (type == '我创建的') {
                url = '/public/myAddTask';
            }
            if (type == '我执行的') {
                url = '/public/myExecutorTask';
            }
            if (type == '我参与的') {
                url = '/public/myJoinTask';
            }
            $.post(url, args, function (data) {
                var task = data.data;
                if(task != ''){
                    appendStr(task,data.orderType);
                } else{
                    notDataStr(task);
                }
            }, "json");
        });

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
         * 完成任务 监听
         */
        form.on('checkbox(complete)', function(data){
            // console.log(data.elem.value); //得到checkbox原始DOM对象
          var inputs= $("#" +data.elem.value);
console.log(inputs)

            // console.log(data.elem.checked); //是否被选中，true或者false
            // console.log(data.value); //复选框value值，也可以通过data.elem.value得到
            // console.log(data.othis); //得到美化后的DOM对象
            var status = '';
            var url = "/task/resetAndCompleteTask";
            if(data.elem.checked == true){
                status = '未完成';
            } else{
                status = '完成';
            }
            var args = {"taskId":data.value,"taskStatus":status};
            $.post(url,args,function (data) {
                if(data.result == 0){
                    inputs.siblings(".layui-form-checkbox").removeClass('layui-form-checked');
                    layer.msg(data.msg);
                } else{
                    inputs.parents(".thing-list").remove();
                }
            },"json");
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

    /**
     * 点击我的任务 触发事件
     */
    $("#click-my-task").click(function () {
        var url = '/public/myExecutorTask';
        var args = {"status":"未完成","orderType":"1"};
        var content = '';
        $.post(url,args,function (data) {
            var task = data.data;
            if(task != ''){
                appendStr(task);
            } else{
                notDataStr(task);
            }
        },"json");
        $("#my-task").show().siblings().hide()
    });
    $("#click-my-scheduling").click(function () {
        $("#my-scheduling").show().siblings().hide()
    });
    $("#click-my-file").click(function () {
        $("#my-file").show().siblings().hide()
    });

    //点击近期的事 页面  中的 紧急选择条
    $("html").on("click",".urgent-state",function () {
        var top=$(this).offset().top+46;
        var left=$(this).offset().left;
        emergency(top,left)
    });
    //紧急选择条 弹框
    function emergency(top,left) {
        layui.use('layer', function(){
            var layer = layui.layer;
            layer.open({
                type: 1,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
                title: false, //标题
                offset: [top,left],
                area:['200px','140px'],
                fixed: false,
                shade: [0.1, '#fff'],
                shadeClose: true, //点击遮罩关闭
                closeBtn: 0,
                anim: 1,  //动画 0-6
                content: $(".state-tk")
            });
        });
    }
    //点击 任务 li 出现 修改任务 弹框
    $("html").on("click",".rw-span-wrap",function () {
        changerw($(this).attr("value"))
    });
    function changerw(taskId) {
        layui.use('layer', function(){
            var layer = layui.layer;
            layer.open({
                type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
                title: false, //标题
                area:['600px','600px'],
                fixed: false,
                shadeClose: true, //点击遮罩关闭
                closeBtn: 0,
                anim: 1,  //动画 0-6
                content: "/task/initTask.html?taskId="+taskId
            });
        });
    }
    //点击 日程 li 出现 修改日程 弹框
    $("html").on("click",".rc-span-wrap",function () {
        changerc()
    });
    function changerc() {
        layui.use('layer', function(){
            var layer = layui.layer;
            layer.open({
                type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
                title: false, //标题
                area:['600px','540px'],
                fixed: false,
                shadeClose: true, //点击遮罩关闭
                closeBtn: 0,
                anim: 1,  //动画 0-6
                content: "tk-calendar-change-content.html"
            });
        });
    }




    //点击任务页面 导航
    $(".my-task-title-left>span").click(function () {
       var i=$(this).index();
       $(this).addClass("now").siblings().removeClass("now");
       //  $(".things").hide();
       // $(".things").eq(i+1).show(400)
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

    /**
     * 点击  我执行的, 我参与的,我创建的 事件
     */
    $('.my-task-title-left').click(function () {
        $(this).children('span').each(function () {
           if($(this).attr("class") == 'now'){
               var type = $(this).html();
               var status = '';
               if($('#status').val() == '1'){
                   status = '未完成';
               } else{
                   status = '完成';
               }
               var url;
               var args;
               args = {"status":status,"orderType":$('#orderType').val()};
               if(type == '我创建的'){
                   url = '/public/myAddTask';
               }
               if(type == '我执行的'){
                   url = '/public/myExecutorTask';
               }
               if(type == '我参与的'){
                   url = '/public/myJoinTask';
               }
               $.post(url,args,function (data) {
                    var task = data.data;
                    if(task != ''){
                        appendStr(task,data.orderType);
                    } else{
                        notDataStr(task);
                    }
               },"json");
           }
        });
    });

    /**
     * 拼接我的任务的字符串
     * @param task
     */
    function appendStr(task,orderType){
        var content = '';
        if(orderType == '4'){
            for (var i = 0;i < task.length;i++){
                content += '<div>'+
                    '<p class="pro-name">' + task[i].projectName + '</p>'+
                    '<ul id="executorTask">';
                for (var j = 0;j < task[i].taskList.length;j++){
                    content+= '<li class="thing-list">' +
                    '<div class="urgent-state"></div>' +
                    '<div class="things-info boxsizing">';
                    if(task[i].taskList[j].taskStatus == '完成'){
                    content += '<input  id="' + task[i].taskList[j].taskId + '"  type="checkbox" checked="checked" value="' + task[i].taskList[j].taskId + '" name="" title="" lay-skin="primary" lay-filter = "complete" class="is-sure" >';

                    } else{
                        content += '<input id="' + task[i].taskId + '" type="checkbox" value="' + task[i].taskList[j].taskId + '" name="" title="" lay-skin="primary" lay-filter = "complete" class="is-sure" >';
                    }
                    content += '<div class="rw-span-wrap" value="' + task[i].taskList[j].taskId + '">' +
                    '<span class="what-thing" >' +task[i].taskList[j].taskName + '</span>' +
                    '<span class="what-thing" >' + task[i].projectName + '</span>' +
                    '</div>';
                    if ((task[i].taskList[j].startTime == '' || task[i].taskList[j].startTime == null) && (task[i].taskList[j].endTime == '' || task[i].taskList[j].endTime == null)) {
                        content += '<span class="thing-what-date aa" ></span>' +
                            '<span class="thing-what-date"></span>' +
                            '<span class="thing-what-date"></span>' +
                            '</div>' +
                            '</li>';
                        continue;
                    }
                    if (task[i].taskList[j].startTime == '' || task[i].taskList[j].startTime == null) {
                        content += '<span class="thing-what-date" >' + new Date(task[i].taskList[j].endTime).format('yyyy-MM-dd') + '   结束' + '</span>'
                        continue;
                    }
                    if (task[i].taskList[j].endTime == '' || task[i].taskList[j].endTime == null) {
                        content += '<span class="thing-what-date" >' + new Date(task[i].taskList[j].startTime).format('yyyy-MM-dd') + '   开始' + '</span>'
                        continue;
                    }

                    content += '<span class="thing-what-date" >' + new Date(task[i].taskList[j].endTime).format('yyyy-MM-dd') + '</span>' +
                        '<span class="thing-what-date">—</span>' +
                        '<span class="thing-what-date">' + new Date(task[i].taskList[j].startTime).format('yyyy-MM-dd') + '</span>' +
                        '</div>' +
                        '</li>';
                }
            content += '</ul>'+
                '</div>';
            }
            $('#projectTask').html(content);
            var form = layui.form;
            form.render();
        } else{
            content += '<div>'+
                '<ul id="executorTask">';
            for(var i = 0;i < task.length;i++) {
                content +=
                    '<li class="thing-list" >' +
                    '<div class="urgent-state"></div>' +
                    '<div class="things-info boxsizing">';
                if(task[i].taskStatus == '完成'){
                    content += '<input id="' + task[i].taskId + '"  type="checkbox" checked="checked" name="" value="' + task[i].taskId + '" title="" lay-skin="primary" lay-filter = "complete" class="is-sure" >';

                } else{
                    content += '<input id="' + task[i].taskId + '" type="checkbox" name="" value="' + task[i].taskId + '" title="" lay-skin="primary" lay-filter = "complete" class="is-sure" >';
                }
                    content += '<div class="rw-span-wrap" value = "' + task[i].taskId + '">' +
                    '<span class="what-thing" >' + task[i].taskName + '</span>' +
                    '<span class="what-thing" >' + task[i].project.projectName + '</span>' +
                    '</div>';
                if ((task[i].startTime == '' || task[i].startTime == null) && (task[i].endTime == '' || task[i].endTime == null)) {
                    content += '<span class="thing-what-date aa" ></span>' +
                        '<span class="thing-what-date"></span>' +
                        '<span class="thing-what-date"></span>' +
                        '</div>' +
                        '</li>';
                    continue;
                }
                if (task[i].startTime == '' || task[i].startTime == null) {
                    content += '<span class="thing-what-date" >' + new Date(task[i].endTime).format('yyyy-MM-dd') + '   结束' + '</span>'
                    continue;
                }
                if (task[i].endTime == '' || task[i].endTime == null) {
                    content += '<span class="thing-what-date" >' + new Date(task[i].startTime).format('yyyy-MM-dd') + '   开始' + '</span>'
                    continue;
                }

                content += '<span class="thing-what-date" >' + new Date(task[i].endTime).format('yyyy-MM-dd') + '</span>' +
                    '<span class="thing-what-date">—</span>' +
                    '<span class="thing-what-date">' + new Date(task[i].startTime).format('yyyy-MM-dd') + '</span>' +
                    '</div>' +
                    '</li>';
            }
            $('#projectTask').html(content);
            var form = layui.form;
            form.render();
        }
    }

    function notDataStr(t){

        var content = '';
        if(t.orderType != '4') {
            content += '<li class="thing-list">' +
                '<div class="urgent-state"></div><div class="things-info boxsizing">' +
                '<span class="what-thing">' + '没有数据' + '</span><span class="what-thing"></span></div><span class="thing-what-date aa"></span>' +
                '<span class="thing-what-date"></span><span class="thing-what-date"></span>' +
                '</div>' +
                '</li>';
            $('#executorTask').html(content);
        }
    }

});