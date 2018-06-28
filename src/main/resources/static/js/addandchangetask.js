

layui.use('form', function() {
    var form = layui.form;
    //监听提交
    form.on('submit(createTask)', function (data) {
        //获取选中的参与者信息
        var members = $('#memberId').val();
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
        var args = {"startTime":startTime ,"endTime":endTime,"taskName":taskName,"repeat":repeat,"remind":remind,"priority":priority,"privacyPattern":privacyPattern,"taskMenuId":taskMenuId,"projectId" : projectId,"members":members,"executor":executor};
        $.post(url,args,function(data){
            if(data.result == 1){
                layer.msg("任务创建成功!");
                //关闭遮罩层
                //任务数回显
            }
        },"json");
        return false;
    });
});

    layui.use('laydate', function () {
        var laydate = layui.laydate;

        laydate.render({
            elem: '#beginTime',
            type: 'datetime',
            format: 'yyyy-MM-dd HH:mm'
        });

        laydate.render({
            elem: '#overTime',
            type: 'datetime',
            format: 'yyyy-MM-dd HH:mm'
        });
    });
    //点击 认领人 的x 号， 移出认领人 ，待认领出现
    $(".remove-who-wrap").click(function () {
        $(this).parent().hide();
        if ($(".who-and-time").find(".who-wrap").css("display") == "none") {
            $(".no-renling").show();
        } else {
            $(".no-renling").hide();
        }
    });

    //点击 待认领 出现 人员名单
    $(".no-renling").click(function (e) {
        var url = "/task/findProjectAllMember";
        var args = {"projectId": projectId};
        //异步请求项目人员名单
        $.post(url,args,function(data){
            var member = data.data;
            var content = "";
            for(var i = 0;i < member.length;i++){
                content += "<div class='one-people'>";
                content += "<img th:src='\@{"+ member[i].userInfo.image +"}\'>";
                content += "<span value = '"+ member[i].id +"'>" + member[i].userName + "</span>";
                content += "<i class=\'layui-icon layui-icon-ok\' style=\'font-size: 16px; color: #D1D1D1;\'></i>";
                content += "</div>";
            }
            $('#executor').html(content);
        });
        $(".people").show(500)
        e.stopPropagation()
    });

    // 点击  任务菜单出现隐藏
    $(".assignment-menu-show").click(function () {
        $(".renwu-menu").slideToggle();
    });
    $(".scheduling-menu-title img").click(function () {
        $(".renwu-menu").slideUp();
    });

    // 点击添加标签
    $(".no-tags").click(function (e) {
        var url = "/task/findAllTags";
        var args = {"projectId":projectId};
        //异步请求获取项目下的所有标签
        $.post(url,args,function(data){
            var tags = data.data;
            var content = "";
            for(var i = 0;i < tags.length; i++){
                content += "<li class='tags-list'>" +
                                "<span class='dot'></span>" +
                                "<span class='tag-font' th:value='"+ tags[i].tagId +"'>"+ tags[i].tagName +"</span>"+
                            "</li>";
            }
            $('#tags').html(content);
            $(".tags-search-build").show();
            $(".tag-search").show();
            $(".no-tags").hide();
        },"json");
        e.stopPropagation();
    });
    $(".tag-search-title img").click(function () {
        $(".tag-search").hide();
        $(".build-tags").show();
    });
    $(".go-return").click(function () {
        $(".tag-search").show();
        $(".build-tags").hide();
    });
    $(".close-tag").click(function () {
        $(".tags-search-build").slideUp();
        $(".no-tags").show();
    });
    $(".has-tags>i").click(function (e) {
        $(".tags-search-build").show();
        $(".tag-search").show();

        e.stopPropagation();
    });

    // 点击某个具体标签
    $(".tags-list").click(function () {
        var tag = $(this).find(".tag-font").text();
        $(".has-tags").show()
        $(".has-tags").prepend('<span class="tag">\n' +
            '                    ' + tag + '  \n' +
            '                    <i class="layui-icon layui-icon-close-fill" style="font-size: 14px; color: #1E9FFF;"></i>\n' +
            '                </span>')
    });

    $(".revise-task").on("click", ".tag i", function () {
        $(this).parent().remove();
        //判断 有没有标签
        console.log($(".has-tags span").length);
        if ($(".has-tags span").length == 0) {
            $(".has-tags").hide();
            $(".no-tags").show();
        } else {
            $(".has-tags").show();
            $(".no-tags").hide();
        }
    });

    //点击颜色，颜色出现对勾
    $(".color-pick li").click(function () {
        $(this).find("i").show();
        $(this).siblings().find("i").hide()
    });
    //点击空白区域 添加标签消失
    $(document).click(function (event) {
        var _con = $('.tags-search-build');   // 设置目标区域
        if (!_con.is(event.target) && _con.has(event.target).length === 0) { // Mark 1
            //$('#divTop').slideUp('slow');   //滑动消失
            $('.tags-search-build').hide(500);          //淡出消失
        }
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
        var args = {"taskName":subTaskName,"parentTaskId":taskId};
        var content = $('#subTask').html();
        $.post(url,args,function(data){
            if(data.result == 1){
                content += '<div class="child-task-list">'+
                                '<input type="checkbox" name="" title="" lay-skin="primary" value = '+ data.subTaskId +'>'+
                                '<div class="child-task-con">'+
                                '<span>'+ subTaskName +'</span>'+
                                '<div class="dt">dt-41</div>'+
                                '</div>'+
                                '<i class="layui-icon layui-icon-right go-detail" style="font-size: 16px; color: #a6a6a6;"></i>'+
                                '<img class="child-task-who" src="/image/lpb.png" th:src="@{/image/lpb.png}">'+
                            '</div>';
                $('#subTask').html(content);
                getLog(data.taskLog);
                layer.msg(data.msg);
                $(".click-add-child-task").slideUp(500);
                layui.use('form', function(){
                    var form = layui.form;
                    form.render(); //更新全部
                });
            } else{
                layer.msg(data.msg);
            }
        });
    });
    $(".remove-work-people").click(function () {
        $(this).parent().remove()
    });


    //点击人员 出现对勾
    $("html").on("click",".one-people",function () {
        $(this).find("i").toggle();
        var arr=[]
        for (var i=0;i<$(".one-people").length;i++){
            if ($(this).find(i).is(":visible")) {
                var value=$(this).find("span").attr("value");
            }
        }
    });

    /**
     * 选中任务的参与者
     */
    $('.people-ok').click(function () {
      // if(document.getElementById("people-ok").classList.contains("cyz-chufa")){
      // }else {
      // }
        var arr=[];
        for (var i=0;i<$(".one-people").length;i++){
            if ($(".one-people").eq(i).find("i").is(":visible")) {
                var value =$(".one-people").eq(i).find("span").attr("value");
                arr.push(value);
            }
        }
        var identity = $('#identity').html();
        //被选中的参与者id 存储
        $('#memberId').val(arr);
        if(identity == '执行者'){
            alert($('#executorId').val(arr));
            $(".who-and-time").append('<div class="who-wrap">\n' +
                '            <img th:src="#{IMAGE_SERVER} + ${user.userInfo.image}"/>\n' +
                '            <span th:text="'+ name +'" id = "showExecutor" value = '+ arr +'></span>\n' +
                '            <i class="layui-icon layui-icon-close-fill remove-who-wrap" style="font-size: 16px; color: #1E9FFF;"></i>\n' +
                '        </div>')
            $(".no-renling").hide()
        }
        $(".people").hide(500);
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
     * 添加任务的时候显示的人员信息
     */
    $(".add-work-people img").click(function (e) {
        var url = "/task/findProjectAllMember";
        var args = {"executor": $('#executorId').val(), "projectId": projectId};
        $.post(url, args, function (data) {
            var content = "";
            var member = data.data;
            if (member != null && member.length > 0) {
                for (var i = 0; i < member.length; i++) {
                    content += "<div class=\'one-people\'>";
                    content += "<img th:src='\@{"+ member[i].userInfo.image +"}\'>";
                    content += "<span value = '"+ member[i].id +"'>" + member[i].userName + "</span>";
                    content += "<i class=\'layui-icon layui-icon-ok\' style=\'font-size: 16px; color: #D1D1D1;\'></i>";
                    content += "</div>";
                }
                $("#executor").html(content);
                $('#identity').html("参与者");
                $(".people").show(500,function () {
                   document.getElementById("people-ok").classList.add("cyz-chufa")
                });

            } else{
                    content += "<div class=\'one-people\'>";
                    content += "<img th:src='\@{add.png}\'>";
                    content += "<span>该项目还没有成员</span>";
                    content += "<i class=\'layui-icon layui-icon-ok\' style=\'font-size: 16px; color: #D1D1D1;\'></i>";
                    content += "</div>";
                $("#executor").html(content);
                $(".people").show(500);
            }
        }, "json");
        e.stopPropagation();
    });


