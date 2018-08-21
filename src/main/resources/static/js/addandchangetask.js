function close() {
    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    parent.layer.close(index); //再执行关闭
}
var zxz=false;

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


    // 点击  任务菜单出现隐藏
    $(".assignment-menu-show").click(function () {
        $(".renwu-menu").slideToggle();
    });
    $(".scheduling-menu-title img").click(function () {
        $(".renwu-menu").slideUp();
    });

    //移除任务的参与者
    $("html").on("click",".remove-work-people",function () {
        $(this).parent().remove();
    });


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
    var flag = true;
    var tag = $(this).find(".tag-font").text();
    var tagId = $(this).find(".tag-font").attr("id");
    if($(".has-tags .tag").length===0){
        $(".has-tags").prepend('<span class="tag" id="'+tagId+'">' + tag + '<i class="layui-icon layui-icon-close-fill" style="font-size: 14px; color: #1E9FFF;"></i></span>');
    }else{
        $('.tag').each(function (data,item) {
            if($(item).attr('id') === tagId){
                flag = false;
                return false;
            }
        });
        if(flag){
            $(".has-tags").prepend('<span class="tag" id="'+tagId+'">' + tag + '<i class="layui-icon layui-icon-close-fill" style="font-size: 14px; color: #1E9FFF;"></i></span>');
        }else{
            layer.msg("请勿重复添加",{icon:5});
        }
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
// $(".tag-ok").click(function () {
//     if ($(".tag-name").val()==''){
//         return false
//     } else {
//         var content = '';
//         var vals=$(".tag-name").val();
//         var color='';
//         $(".color-pick li i").each(function () {
//             if ($(this).is(":visible")){
//                 color=$(this).parent().css("background-color")
//             }
//         });
//         var url = "/tag/add";
//         var args = {"tagName":vals,"bgColor":color,"projectId":projectId}
//         $.post(url,args,function (data) {
//             if(data.result > 0){
//                 $(".has-tags").show();
//                 content +=
//                     '<span class="tag" value="' + data.data.tagId + '" style="background-color:' + color + '">'+
//                     '<b style="font-weight: 400">' + vals + '</b>'+
//                     '<i class="layui-icon layui-icon-close-fill" style="font-size: 14px; color: #1E9FFF;"></i>'+
//                     '</span>';
//                 $('.no-tags').hide();
//                 $(".has-tags").prepend(content);
//             } else{
//                 layer.msg(data.msg);
//             }
//         },"json");
//
//     }
// });
/**
 * 点击 X 时 移除掉该标签
 */
// $(".tag-box").on("click", ".tag i", function () {
//     $(this).parent().remove();
// });





