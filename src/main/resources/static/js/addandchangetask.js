function close() {
    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    parent.layer.close(index); //再执行关闭
}
var zxz=false;
layui.use('form', function() {
    var form = layui.form;
    //监听提交
    form.on('submit(createTask)', function (data) {
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
        var args = {"startTime":startTime ,"endTime":endTime,"taskName":taskName,"repeat":repeat,"remind":remind,"priority":priority,"privacyPattern":privacyPattern,"taskMenuId":taskMenuId,"projectId" : projectId,"members":members.toString(),"executor":executor,"tagId":tags.toString()};
        $.post(url,args,function(data){
            if(data.result == 1){
                layer.msg("任务创建成功!");
                close();
            } else{
                layer.msg("任务创建失败!");
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
            format: 'yyyy-MM-dd'
        });

        laydate.render({
            elem: '#overTime',
            type: 'datetime',
            format: 'yyyy-MM-dd'
        });
    });
if ($("#have-executor").val()){
    $(".who-wrap").show();
    $(".no-renling").hide()
} else {
    $(".who-wrap").hide();
    $(".no-renling").show()
}
    //点击 认领人 的x 号， 移出认领人 ，待认领出现
    $(".remove-who-wrap").click(function () {
        $(this).parent().hide();
        $('#executorId').val('');
        $('#showExecutor').html("待认领");
        if ($(".who-and-time").find(".who-wrap").css("display") == "none") {
            $(".no-renling").show();
        } else {
            $(".no-renling").hide();
        }
    });

    //点击 待认领 出现 人员名单
    $(".no-renling").click(function (e) {
        zxz=true;
        $('#identity').html("执行者");
        var url = "/task/findProjectAllMember";
        var args = {"projectId": projectId,"executor":executorId};
        //异步请求项目人员名单
        $.post(url,args,function(data){
            var member = data.data;
            var executor = "";
            var content = "";
            if(member != null){
                for(var i = 0;i < member.length;i++){
                    content += "<div class='one-people'>";
                    content += "<img src='"+IMAGE_SERVER+ member[i].userInfo.image +"'>";
                    content += "<span value = '"+ member[i].id +"'>" + member[i].userName + "</span>";
                    content += "<i class=\'layui-icon layui-icon-ok\' style=\'font-size: 16px; color: #D1D1D1;\'></i>";
                    content += "</div>";
                }
            } else{
                content +=  "<div class='one-people'>";
                content += "<img src='/static/image/add.png'>";
                content += "<span value = ''>没有成员</span>";
                content += "<i class=\'layui-icon layui-icon-ok\' style=\'font-size: 16px; color: #D1D1D1;\'></i>";
                content += "</div>";
            }
            executor+=
            "<div class='one-people'>"+
            "<img th:src='\@{}\'>"+
            "<span value = ''>待认领</span>"+
            "<i class=\'layui-icon layui-icon-ok\' style=\'font-size: 16px; color: #D1D1D1;\'></i>"+
            "</div>";
            $('#members').html(executor);
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
                                "<span class='dot' style = 'background-color:" + tags[i].bgColor + "'></span>" +
                                "<span class='tag-font' value='"+ tags[i].tagId +"'>"+ tags[i].tagName +"</span>"+
                            "</li>";
            }
            $('#tags').html(content);
            $(".tags-search-build").show();
            $(".tag-search").show();
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

    //移除任务的参与者
    $("html").on("click",".remove-work-people",function () {
        $(this).parent().remove();
    });
    // $(".revise-task").on("click", ".tag i", function () {
    //     $(this).parent().remove();
    //     //判断 有没有标签
    //     console.log($(".has-tags span").length);
    //     if ($(".has-tags span").length == 0) {
    //         $(".has-tags").hide();
    //         $(".no-tags").show();
    //     } else {
    //         $(".has-tags").show();
    //         $(".no-tags").hide();
    //     }
    // });

    //点击颜色，颜色出现对勾
    $(".color-pick li").click(function () {
        $(this).find("i").show();
        $(this).siblings().find("i").hide()
    });
    //点击空白区域 添加标签消失
    $(".new-assignment").click(function (event) {
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
        if (zxz){
            $(this).siblings().find("i").hide();
            $(this).find("i").toggle();
        } else {
            $(this).find("i").toggle();
        }
    });

    /**
     * 选中任务的参与者
     */
    $('.people-ok').click(function () {
        // 执行者 确定
        if (zxz){
            var content = "";
            var id = "";
            var name = "";
            var image = "";
            var index = 0;
            for (var i=0;i<$("#executor .one-people").length;i++){
                if ($("#executor .one-people").eq(i).find("i").is(":visible")) {
                    id = $("#executor .one-people").eq(i).find("span").attr("value");
                    name = $("#executor .one-people").eq(i).find("span").html();
                    image =  $("#executor .one-people").eq(i).find("img").attr("src");
                }
            }
            if(id == ''){
                $(".people").hide(500);
                return false;
            }
            $('.work-people .one-work-people').each(function () {
                if($(this).attr('value') == id){
                    index ++;
                }
            })
            if(index == 0){
                content = '<div value="' + id + '" class="one-work-people">'+
                    '<input type="hidden" value="' + name + '">'+
                    '<img src="' + image + '">'+
                    '<i class="layui-icon layui-icon-close-fill remove-work-people " style="font-size: 15px; color: #3da8f5;"></i>'+
                    '</div>';
                $('.work-people .add-work-people').before(content);
            }
            $(".who-wrap").css("display","block");
            $('#executorId').val(id);
            $('#showExecutor').html(name);
            $('#executorImg').attr("src",image);
            $(".no-renling").hide();
            $(".people").hide(500);
        }else {  //参与者 确定
            var arr=[];
            var img = [];
            var names = [];
            for (var i=0;i<$("#executor .one-people").length;i++){
                if ($("#executor .one-people").eq(i).find("i").is(":visible")) {
                    var value = $("#executor .one-people").eq(i).find("span").attr("data-id");
                    var image = $("#executor .one-people").eq(i).find("img").attr("src");
                    var name = $("#executor .one-people").eq(i).find("span").html();
                    arr.push(value);
                    img.push(image);
                    names.push(name);
                }
            }
            //得到要移除头像列表的成员id
            var remove = [];
            for (var i=0;i<$("#members .one-people").length;i++){
                if ($("#members .one-people").eq(i).find("i").is(":hidden")) {
                    var value =$("#members .one-people").eq(i).find("span").attr("data-id");
                    remove.push(value);
                }
            }
            if(arr == '' && remove == ''){
                return false;
            }
            var content = "";
            //在成员头像列表添加头像
            if(arr != ''){
                for(var i = 0;i < arr.length;i++){
                    content += '<div value = "' + arr[i] + '" class="one-work-people">'+
                        '<input type="hidden" value="' + names[i] + '"  />'+
                        '<img src="'+ img[i] +'">'+
                        '<i class="layui-icon layui-icon-close-fill remove-work-people " style="font-size: 15px; color: #3da8f5;"></i>'+
                        '</div>';
                }
            }
            //移除成员头像列表头像
            if(remove != ''){
                for (var i = 0;i < remove.length;i++){
                    $('.work-people .one-work-people').each(function () {
                        if($(this).attr('value') == remove[i]){
                            $(this).remove();
                        }
                    })
                }
            }
            $(".add-work-people").before(content);
            //被选中的参与者id 存储
            $(".no-renling").hide();
        }
        $(".people").hide(500);
    });

    //点击空白区域 添加人员消失
    $(".new-assignment").click(function (event) {
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

        zxz =false;
        //获取选中的人员的id
        var isExistId = [];
        var isExistName = [];
        var isExistImg = [];
        $('.work-people .one-work-people').each(function () {
           isExistId.push($(this).attr('value'));
           isExistName.push($(this).children('input').val());
           isExistImg.push($(this).children('img').attr('src'));
        });

        //异步发送请求
        var url = "/task/reverseFindUser";
        var args = {"projectId": projectId,"uId":isExistId.toString()};
        $.post(url, args, function (data) {
            var content = "";
            var cyz = "";
            var member = data.reversUser;
            if (member != null && member.length > 0) {
                for (var i = 0; i < member.length; i++) {
                    content += "<div class=\'one-people\'>";
                    content += "<img src='"+IMAGE_SERVER+ member[i].userInfo.image +"'>";
                    content += "<span data-id = '"+ member[i].id +"'>" + member[i].userName + "</span>";
                    content += "<i class=\'layui-icon layui-icon-ok\' style=\'font-size: 16px; color: #D1D1D1;\'></i>";
                    content += "</div>";
                }
                if($('#executorId').val() == ''){
                    cyz += '<div class="one-people">'+
                        '<img src="/static/image/add.png">'+
                        '<span data-id="">'+ "无参与者" +'</span>'+
                        '<i  class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;display: block"></i>'+
                        '</div>';
                } else{
                    for (var i = 0;i < isExistId.length;i++){
                        cyz += '<div class="one-people">'+
                            '<img src="' + isExistImg[i] + '">'+
                            '<span data-id="' + isExistId[i] + '">'+ isExistName[i] +'</span>'+
                            '<i  class="layui-icon layui-icon-ok" style="font-size: 16px; color: #D1D1D1;display: block"></i>'+
                            '</div>';
                    }

                }
                $('#members').html(cyz);
                $("#executor").html(content);
                $('#identity').html("参与者");
                $(".people").show(500,function () {
                   document.getElementById("people-ok").classList.add("cyz-chufa");
                    // document.getElementById("members").classList.add("yougou");
                });
            } else{
                    content += "<div class=\'one-people\'>";
                    content += "<img th:src='\@{add.png}\'>";
                    content += "<span>该项目还没有成员</span>";
                    content += "<i class=\'layui-icon layui-icon-ok\' style=\'font-size: 16px; color: #D1D1D1;\'></i>";
                    content += "</div>";
                $("#executor").html(content);
                $(".people").show(500,function () {
                    // document.getElementById("members").classList.add("yougou");
                });
            }
        }, "json");
        e.stopPropagation();
    });

// 点击某个具体标签
$("html").on("click",".tags-list",function () {
    var index = 0;
    var tagId = $(this).find(".tag-font").attr("value");
    $('.tag').each(function () {
       if($(this).attr('value') == tagId){
            index = 1;
            var that = $(this);
            that.remove();
            return false;
       }
    });
    if(index == 0){
        var tagName = $(this).find(".tag-font").html();
        var tagColor = $(this).find(".dot").css("background-color");
        var content = '';
        content += '<span class="tag" value="' + tagId + '" style="background-color:' + tagColor + '">'+
                '<b style="font-weight: 400">' + tagName + '</b>'+
                '<i class="layui-icon layui-icon-close-fill" style="font-size: 14px; color: #1E9FFF;"></i>'+
            '</span>';
        $('.has-tags').prepend(content);
    }
    if ($(".has-tags .tag").length==0){
        $(".no-tags").show()
    } else {
        $(".no-tags").hide()
    }
});

// 创建 按钮 是否 能点击
$(".tag-name").keyup(function () {
    if ($(this).val()==''){
        $(".tag-ok").css({"background-color":"#ccc","cursor":"not-allowed"})
    } else {
        $(".tag-ok").css({"background-color":"#1E9FFF","cursor":"pointer"})
    }
});

//点击创建 按钮
$(".tag-ok").click(function () {
    if ($(".tag-name").val()==''){
        return false
    } else {
        var content = '';
        var vals=$(".tag-name").val();
        var color='';
        $(".color-pick li i").each(function () {
            if ($(this).is(":visible")){
                color=$(this).parent().css("background-color")
            }
        });
        var url = "/tag/add";
        var args = {"tagName":vals,"bgColor":color,"projectId":projectId}
        $.post(url,args,function (data) {
            if(data.result > 0){
                $(".has-tags").show();
                content +=
                    '<span class="tag" value="' + data.data.tagId + '" style="background-color:' + color + '">'+
                    '<b style="font-weight: 400">' + vals + '</b>'+
                    '<i class="layui-icon layui-icon-close-fill" style="font-size: 14px; color: #1E9FFF;"></i>'+
                    '</span>';
                $('.no-tags').hide();
                $(".has-tags").prepend(content);
            } else{
                layer.msg(data.msg);
            }
        },"json");

    }
});
/**
 * 点击 X 时 移除掉该标签
 */
$(".tag-box").on("click", ".tag i", function () {
    $(this).parent().remove();
    if ($(".has-tags .tag").length==0){
        $(".no-tags").show()
    } else {
        $(".no-tags").hide()
    }
});





