
$(function () {
    var pushbar = new Pushbar({
        blur:false,
        overlay:false
    });

    // // 分组查询 框
    // $(".groups-down-select").click(function (e) {
    //     $(this).siblings(".task-select-box").toggle();
    //     e.stopPropagation()
    // });

    /**
     * 选择一个分组的时候
     */
    $(".fenzu-ul li").click(function () {
        $(this).find('h4').css('color','#3DA8F5');
        var id = $(this).attr('data-id');
        location.href = '/relation/changeGroup?groupId='+id+'&projectId='+projectId;
    });


    $(".groups-down-select").click(function (top,left) {
        layui.use('layer', function(){
            var layer = layui.layer;
            layer.open({
                type:1,
                title:false,
                btn:0,
                area:['304px','400px'],
                offset: [top,left],
                closeBtn: 0,
                shade: [0.1, '#fff'],
                shadeClose:true,
                content: $(".task-select-box")
            });
        });
    });




    /**
     * 点击创建分组按钮
     */
    $(".create-btn").click(function () {
       var groupName = $('.groupname').val();
       var args = {"relationName":groupName,"projectId":projectId};
       var url = "/relation/addRelation";
       $.post(url,args,function (data) {
           if(data.result === 1){
               location.href = '/relation/changeGroup?groupId='+data.groupId+'&projectId='+projectId;
           }
       });
    });

    $(".icon-arrow-circle-o-down").click(function (e) {
        var length = $('.fenzu-ul li').length;
        if(length === 1){
            $('.recycle-bin').remove();
        }
        $(".fzcd-box-c").show().siblings().hide()
        $(this).siblings(".fzcd-box").slideToggle();
        e.stopPropagation()
    });
    $(".fzcd-header i").click(function () {
        $(this).parents(".fzcd-box").slideUp()
    });
    $(".add-task-group").click(function () {
        $(".new-built-muban-box").toggle();
        if ($(".new-built-muban-box").is(":visible")){
            $(".task-select-box").css({"overflow-y":"auto","max-height":"550px"});
            $(".add-task-group").css("background-color","#f7f7f7")
        } else {
            $(".task-select-box").css({"overflow-y":"inherit","max-height":"initial"});
            $(".add-task-group").css("background-color","#fff")
        }
    });
    $(".new-build-btn").click(function () {
        $(this).addClass("now");
        $(".muban-btn").removeClass("now");
        $(".new-built-box").show();
        $(".muban-box").hide()
    });
    $(".muban-btn").click(function () {
        $(this).addClass("now");
        $(".new-build-btn").removeClass("now");
        $(".new-built-box").hide();
        $(".muban-box").show()
    });



    $(".dairenling-li").click(function () {
        var top=$(this).offset().top-260+'px';
        var left=$(this).offset().left+20+'px';
        layui.use('layer', function(){
            var layer = layui.layer;
            layer.open({
                type: 1,
                title:false,
                btn:0,
                area:['243px','260px'],
                offset: [top, left],
                closeBtn: 0,
                shade: [0.1, '#fff'],
                shadeClose:true,
                content: $(".sou-person")
            });
        });
    });
$(".xialakuang").click(function (e) {
    e.stopPropagation()
})
    /**
     * 点击移入回收站
     */
    $('.recycle-bin').click(function (e) {
        $(".recycle").show();
        e.stopPropagation();
    });

    $('.groupname').focus(function (e) {
        e.stopPropagation();
    });

    /**
     * 保存分组名称
     */
    $('.save-group-name').click(function (e) {
        var id = $(this).parents('li').attr('data-id');
        var name = $('#'+id+'name').val();
        if(name !== '' && name !== null){
            var args = {"relationName":name,"relationId":id};
            var url = "/relation/updateRelation";
            $.post(url,args,function(data){
               if(data.result === 1){
                   $('.fenzu-ul li').each(function () {
                      if($(this).attr('data-id') === id){
                          $(this).find('.gname').html(name);
                      }
                   });
                   $('.fzcd-box-e').hide();
               }
            });
        }
        e.stopPropagation();
    });

    /**
     * 点击回收站确定
     */
    $('.move-recycle').click(function (e) {
        var url = "/relation/moveRecycleBin";
        var that = $(this).parents('li.boxsizing');
        var id = that.attr('data-id');
        var firstId = that.siblings('li');
        // console.log()
        // console.log(firstId.attr("data-id"))

        var args = {"relationId":id,"relationDel":that.attr('isDel')};
        $.post(url,args,function (data) {
            if(data.result == 1){
                $(".renwu-menu").slideUp();
                that.remove();
                location.href = '/relation/changeGroup?groupId='+ firstId.eq(0).attr("data-id") +'&projectId='+projectId;
                // parent.layer.msg('任务分组: '+ $('.gname').html() + ' 已经移入回收站', {
                //     offset:'lb',
                //     icon:1,
                //     time: 4000 //2秒关闭（如果不配置，默认是3秒）
                // });
            } else{
                layer.msg("系统异常!");
            }
        });
        e.stopPropagation();
        return false
    });




    // 群组弹框
    $(".team").click(function () {
        pushbar.open('projectMembers');
    });
    // 群组关闭
    $('.close_members').click(function () {
        pushbar.close('projectMembers');
    });

    //点击菜单，打开项目菜单区域
    $(".menu").click(function () {

        pushbar.open("projectMenu");
    });

    //关闭菜单
    $('.close-menu').click(function () {
        pushbar.close("projectMenu");
    });

    //群组 人员  下箭头 弹框
    $("body").on("click",".group-people>li>i",function (e) {
        e.stopPropagation();
        var top=$(this).offset().top-110;
        var left=$(this).offset().left-280;
        var nodeName = $(this).attr('id');
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            offset: [top,left],
            area:['280px','185px'],
            fixed: true,
            shadeClose: true,
            closeBtn: 0,
            shade: 0,
            anim: 1,  //动画 0-6
            content: '/removePeople.html?nodeName='+nodeName
        });

    });

    //群组 邀请成员 弹框
    //点击邀请新成员
    $(".add-new-member").click(function (e) {
        $('.invitation').html('');
        yaoqing();
        e.stopPropagation()
    });
    function yaoqing() {
        layui.use('layer', function(){
            var layer = layui.layer;
            layer.open({
                type: 1,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
                title: "邀请新成员", //标题
                offset: '150px',
                fixed: false,
                shadeClose: true, //点击遮罩关闭
                anim: 1,  //动画 0-6
                content: $(".search_prople")
            });
        });
    }



    $('.user_search').click(function (e) {
        $('.invitation').html('');
        var index = layer.load(1, {shade: [0.1,'#fff']});
        $.post('/searchMember',{"keyword":$('.keyword').val(),"projectId":projectId},function (data) {
            parent.layer.close(index);
            if(data.result===1){
                for (var i=0;i<data.data.length;i++){
                    var user = data.data[i];
                    var li = '<li><img src="'+IMAGE_SERVER+user.userInfo.image+'" >\n' +
                        '<span class="people-name">'+user.userName+'</span>\n';
                    li +='<span class="people-state can-use-btn" data="'+user.id+'" >添加</span></li>';
                    $('.invitation').append(li);
                }
            }else{
                $('.invitation').append('<li><span class="people-name">'+ '该成员的信息不存在' +'</span></li>');
            }
        });
    });


    $("html").on("click",".people-box .can-use-btn",function () {
        var id = $(this).attr('data');
        $.post('/addProjectMember',{"projectId":projectId,"memberId":id},function (data) {
            if(data.result===1){
                $('.group-people').html('');
                for(var i=0;i<data.data.length;i++) {
                    var projectMember = data.data[i];
                    var li = '<li class="boxsizing">\n' +
                        '            <img src="'+IMAGE_SERVER + projectMember.memberImg+'">\n' +
                        '            <div class="group-people-info">\n' +
                        '                <p class="people-info-name">'+projectMember.memberName+'</p>\n' +
                        '                <p class="people-info-email"></p></div>';
                    if(projectMember.memberLable===0){
                        li += '<i class="layui-icon layui-icon-down" style="font-size: 20px; color: #a6a6a6;" id="'+projectMember.id+'"></i></li>';
                    }else{
                        li += '</li>';
                    }

                    $('.group-people').append(li);
                    $('.people-box .people-state').removeClass('can-use-btn');
                }
            }else{
                layer.msg(data.msg,{icon:5});
            }

        });
    });


    // 点击查看更多
    $(".menu-more").click(function () {
        $(".look-more-show").show();
        $(".project-menu-show").hide()
    });
    $(".look-more-title-line div").click(function () {
        $(".look-more-show").hide();
        $(".project-menu-show").show()
    })


    $('.project_setting').click(function () {

        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            closeBtn: 0,
            area:['800px','600px'],
            shadeClose: true, //点击遮罩关闭
            anim: 1,  //动画 0-6
            content: '/projectSetting?projectId='+$('.project_setting').attr('id')
        });
    });

    $('.menu-tag').click(function () {
        layer.open({
            type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            title: false, //标题
            area:['800px','600px'],
            fixed: true,
            shadeClose: true,
            closeBtn: 0,
            anim: 1,  //动画 0-6
            content: ['/projectTag.html?projectId='+$('.menu-tag').attr('id'),'no']
        });
    });

    $('.menu-recycle-bin').click(function () {
        recycleBinShow($(this).attr('data'));
    })

    function recycleBinShow() {
        //修改任务 弹框界面
        layui.use('layer', function(){
            var layer = layui.layer;
            parent.layer.open({
                type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
                title: false, //标题
                offset: '20px',
                area:['800px','600px'],
                fixed: false,
                shadeClose: true, //点击遮罩关闭
                closeBtn: 0,
                anim: 1,  //动画 0-6
                content: "/project/viewRecycleBin.html?projectId="+ projectId
            });
        });
    }


    $('.head-right .avatar').click(function () {
         $('.vertical-nav').slideToggle();
    });

    /**
     * 点击编辑任务分组
     */
    $('.edit-group').click(function (e) {
        $(this).parents(".fzcd-box-c").siblings(".fzcd-box-e").show().siblings().hide()
        e.stopPropagation();
    });

});