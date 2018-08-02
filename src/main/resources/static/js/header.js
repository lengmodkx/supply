
$(function () {
    var pushbar = new Pushbar({
        blur:false,
        overlay:false
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
        $.post('/searchMember',{"keyword":$('.keyword').val()},function (data) {
            parent.layer.close(index);
            if(data.result===1){
                for(var i=0;i<data.data.length;i++){
                    var user = data.data[i];
                    var li = '<li>\n' +
                        '            <img src="'+IMAGE_SERVER+user.userInfo.image+'" >\n' +
                        '            <span class="people-name">'+user.userName+'</span>\n' +
                        '            <span class="people-state no-join" data="'+user.id+'">添加</span>\n' +
                        '        </li>';
                    $('.invitation').append(li);
                }
            }else{

            }
        });
    });


    $("html").on("click",".people-box .people-state",function () {
        var id = $(this).attr('data');
        $.post('/addProjectMember',{"projectId":projectId,"memberIds":id},function (data) {
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
                }
            }
            $(this).removeClass("no-join")
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
            content: ['projectTag.html?projectId='+$('.menu-tag').attr('id'),'no']
        });
    });


    $('.head-right .avatar').click(function () {
         $('.vertical-nav').slideToggle();
    });
});