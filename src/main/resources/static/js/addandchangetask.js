

layui.use('form', function() {
    var form = layui.form;
    //监听提交
    form.on('submit(createTask)', function (data) {
        //设置任务开始时间
        var beginTime = $('#beginTime').val();
        var startTime = new Date(beginTime.toString()).getTime();
        //设置任务结束时间
        var overTime = $('#overTime').val();
        var endTime = new Date(overTime.toString()).getTime();
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
        var member = new Object();
        member.id = "100001";
        member.userName = "何少华";

        var member2 = new Object();
        member2.id = "100002";
        member2.userName = "飞猪B";
        var members  = new Array();
        members[0] = member;
        members[1] = member2
        var url = "/task/saveTask";
        var args = {"startTime":startTime ,"endTime":endTime,"taskName":taskName,"repeat":repeat,"remind":remind,"priority":priority,"privacyPattern":privacyPattern,"taskMenuId":taskMenuId,"projectId" : projectId,"members":JSON.stringify(members)};
        $.post(url,args,function(data){
            layer.msg(data.msg);
        },"json");
        return false;
    });

    form.on('switch(switch-filter)', function (data) {
        console.log(data.elem.checked); //开关是否开启，true或者false
        if (data.elem.checked) {
            $(".who-can-see").text("仅自己可见")
        } else {
            $(".who-can-see").text("所有成员可见")
        }
    });
});

    layui.use('laydate', function () {
        var laydate = layui.laydate;

        //执行一个laydate实例
        laydate.render({
            elem: '#beginTime', //指定元素
            type: 'datetime',
            format: 'yyyy-MM-dd HH:mm:ss'
        });
        laydate.render({
            elem: '#overTime', //指定元素
            type: 'datetime',
            format: 'yyyy-MM-dd HH:mm:ss'
        });
    });

    //点击 认领人 的x 号， 移出认领人 ，待认领出现
    $(".remove-who-wrap").click(function () {
        $(this).parent().remove();
        if ($(".who-and-time").find(".who-wrap").length == 0) {
            $(".no-renling").show();
        } else {
            $(".no-renling").hide();
        }
    });
    //点击 待认领 出现 人员名单
    $(".no-renling").click(function (e) {
        var url = "/task/findProjectAllMember";
        var args = {"executor": executorId, "projectId": projectId};
        //异步请求项目人员名单
        $.post(url,args,function(data){
            var member = data.data;
            var content = "";
            for(var i = 0;i < member.length;i++){
                content += "<img th:src='\@{"+ member[i].userInfo.image +"}\'>";
                content += "<span>" + member[i].userName + "</span>";
                content += "<i class=\'layui-icon layui-icon-ok\' style=\'font-size: 16px; color: #D1D1D1;\'></i>";
            }
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
        },"json");
        $(".tags-search-build").show();
        $(".tag-search").show();
        $(".no-tags").hide();
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
        $(".click-add-child-task").slideUp(500)
    });
    $(".remove-work-people").click(function () {
        $(this).parent().remove()
    });


    //点击人员 出现对勾
    $(".one-people").click(function () {
        $(this).find("i").toggle();

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



    $(".add-work-people img").click(function (e) {
        var url = "/task/findProjectAllMember";
        var args = {"executor": executorId, "projectId": projectId};
        var content = "";
        $.post(url, args, function (data) {
            var member = data.data;
            if (member.length > 0) {
                for (var i = 0; i < member.length; i++) {
                    content += "<img th:src='\@{"+ member[i].userInfo.image +"}\'>";
                    content += "<span>" + member[i].userName + "</span>";
                    content += "<i class=\'layui-icon layui-icon-ok\' style=\'font-size: 16px; color: #D1D1D1;\'></i>";
                }
                $("#executor").html(content);
                $(".people").show(500);
            }
        }, "json");
        e.stopPropagation();
    });
