
var projectId = $("#projectId").val();
var parentId = $("#parentId").val();

$(function () {
    var selectNum = 0;

    layui.use(['form', 'upload', 'layer'], function () {
            var form = layui.form, layer = layui.layer;

            form.on('switch(switch-filter)', function (data) {
                console.log(data.elem.checked); //开关是否开启，true或者false
                if (data.elem.checked) {
                    $(".who-can-see").text("仅自己可见")
                } else {
                    $(".who-can-see").text("所有成员可见")
                }
            });
            //选中复选框
            form.on('checkbox(checks)', function (data) {
                layui.stope();
                if (data.elem.checked) {
                    selectNum += 1;
                    $(this).parent().css("border", "4px solid #3da8f5");
                    movein($(this).parent());
                    $(".file-caozuo-wrap").show();
                    $(".file-names>span").text("已选择" + selectNum + "项")
                } else {
                    selectNum -= 1;
                    $(this).parent().css("border", "1px solid #e5e5e5");
                    moveout($(this).parent());
                    if (selectNum <= 0) {
                        $(".file-caozuo-wrap").hide();
                        $(".file-names>span").text("全选");
                    } else {
                        $(".file-names>span").text("已选择" + selectNum + "项");
                    }
                }

            });
            // 点击全选
            form.on('checkbox(check-all)', function (data) {
                var i = $(".one-file").length - 1;
                if (data.elem.checked) {
                    $(".file-caozuo-wrap").show();
                    $(".one-file").css("border", "4px solid #3da8f5");
                    $(".one-file input").attr('checked', true);
                    $(".one-file>div").addClass("layui-form-checked");
                    movein($(".one-file"));
                    $(".one-file>div").css("opacity", "1");
                    $(".file-names>span").text("已选择" + i + "项")

                } else {
                    $(".file-caozuo-wrap").hide();
                    $(".one-file").css("border", "1px solid #e5e5e5");
                    $(".one-file-wrap input").attr('checked', false);
                    moveout($(".one-file"));
                    $(".file-names>span").text("全选")
                }
            });

            /**
             * 文件上传
             */
            $('#selectfiles').click(function () {
                var top=$(this).offset().top+60+'px';
                var left=$(this).offset().left-50+'px';
                tk_up_type(top,left,projectId,parentId);
            });

        // 复制文件
        $(".copyFile").click(function () {
            var checks = document.getElementsByName("fileCheck");
            var fileIds = "";
            for (var k in checks) {
                if (checks[k].checked)
                    fileIds += checks[k].value + ",";
            }
            fileIds = fileIds.substring(0, fileIds.length - 1);
            cloneFile('copy', fileIds);
        });

        // 移动文件
        $(".moveFile").click(function () {
            var checks = document.getElementsByName("fileCheck");
            var fileIds = "";
            for (var k in checks) {
                if (checks[k].checked)
                    fileIds += checks[k].value + ",";
            }
            fileIds = fileIds.substring(0, fileIds.length - 1);
            cloneFile('move', fileIds);
        });

        // 缩略图模式创建
        $(".new-file-wrap input").keypress(function (e) {
            var folderName = $(".new-file-wrap input").val();
            if (e.which == 13) {
                if (folderName == '') {
                    $(".new-file").hide();
                    $(".new-file-wrap").hide();
                    return false
                } else {
                    layer.load();
                    $.post("/file/createFolder", {
                        projectId: projectId,
                        parentId: parentId,
                        folderName: folderName
                    }, function (data) {
                        if (data.result === 1) {
                            layer.closeAll('loading');
                            window.location.reload();
                        } else {
                            layer.closeAll('loading');
                            layer.msg(data.msg, {icon: 2, time: 1000});
                        }
                    });

                }
            }
        });

    });
    // 鼠标移入移出，显示隐藏 按钮 和 选择框
    $("html").on("mouseover", ".one-file", function () {
        movein($(this))
    });
    $("html").on("mouseleave", ".one-file", function () {
        if ($(this).find("input").is(':checked')) {
            return false
        } else if ($(this).find("input").attr("checked") == "checked") {
            return false
        } else {
            moveout($(this))
        }
    });

    // 鼠标移入 函数
    function movein(that) {
        that.find("i").css("opacity", "1");
        that.find(".img-show-operate").css("opacity", "1");
        if (that.find(".img-show-download").is(":visible")){
            that.css("cursor","crosshair")
        }else {
            that.css("cursor","not-allowed")
        }
    }

    function moveout(that) {
        that.find("div").css("opacity", "0");
        that.find(".img-show-operate").css("opacity", "0");
        that.find("i").css("opacity", "0");
    }

    /**
     * 创建文件夹
     */
    $("#createFolder").click(function (e) {
        $(".new-file-wrap").show();
        $(".new-file-wrap input").focus();
        e.stopPropagation();
    });

    //点击空白区域， 创建文件夹消失
    $(document).click(function (event) {
        var _con = $('.new-file-wrap');  // 设置目标区域
        if (!_con.is(event.target) && _con.has(event.target).length === 0) { // Mark 1
            //$('#divTop').slideUp('slow');  //滑动消失
            $('.new-file-wrap').hide(500);     //淡出消失
        }
    });

    // 点击下拉箭头
    $('html').on('click','.img-show-operate',function (e) {
        e.stopPropagation();
        var fileId = $(this).parent().next().attr("data");
        var top = $(this).offset().top-200;
        var left = $(this).offset().left+10;
        headPhoto(top, left, fileId)
    });

    function headPhoto(top, left, fileId) {
        layui.use('layer', function () {
            var layer = layui.layer;
            layer.open({
                type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
                title: false, //标题
                offset: [top, left],
                area: ['220px', '245px'],
                fixed: true,
                shadeClose: true,
                closeBtn: 0,
                anim: 1,  //动画 0-6
                content: ['/file/optionFile?fileId=' + fileId]
            });
        });
    }

    function tk_up_type(top, left) {
        layui.use('layer', function () {
            var layer = layui.layer;
            layer.open({
                type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
                title: false, //标题
                offset: [top, left],
                area:['200px','148px'],
                fixed: true,
                shadeClose: true,
                shade: [0.1, '#fff'],
                closeBtn: 0,
                anim: 1,  //动画 0-6
                content: '/file/selectUpType?projectId='+projectId+'&parentId='+parentId
            });
        });
    }

    // 浏览器后退 事件
    window.onload=function(){
        addHistoryEntity();
        detectBack();
    }
//新增一条浏览器浏览历史记录
    function addHistoryEntity(){
        if(history.pushState){ //现代浏览器
            history.pushState('state1',null,'#foo');
        }else{                 //ie9及以下
            document.getElementById("goFoo").click();
        }
    }
//监听浏览器回退按钮
    function detectBack(){
        if(history.pushState){ //现代浏览器
            window.onpopstate=function(){
                logout();
            }
        }
        else{                  //ie9及以下
            $(window).on("hashchange",function(){
                if(location.hash=="#foo"){
                    return;
                }
                logout();
            });
        }
    }
//回退前事件
    function logout(){
        urls.pop();
        var lujing={"url":urls,"id":projectId};
        sessionStorage.setItem("lujing",JSON.stringify(lujing));
        console.log(sessionStorage.getItem('lujing'));
        history.back();
    }

if (JSON.parse(sessionStorage.getItem("lujing"))==null || "" || undefined) {
    var lujing={"url":'文件库',"id":projectId};
    sessionStorage.setItem("lujing",JSON.stringify(lujing));
}else if (JSON.parse(sessionStorage.getItem("lujing")).url==[]){

    var lujing={"url":'文件库',"id":projectId};
    sessionStorage.setItem("lujing",JSON.stringify(lujing));
}
    $(".file-toper").html("");
    var urls = [];
    var id= [];
    urls=urls.concat(JSON.parse(sessionStorage.getItem("lujing")).url);
    id=id.concat(JSON.parse(sessionStorage.getItem("lujing")).id);
    console.log(JSON.parse(sessionStorage.getItem("lujing")))
    $.each(urls,function (i,item) {
        var list ='<span><a href="javascript: void(0)" onclick="location.href=\'/file/list.html?projectId=\' + '+id[i]+'">'+item+' &gt; </a></span>'
        $(".file-toper").append(list);
    });



    // 进入下级目录
    $('html').on('click','.one-file',function (e) {
        var wjjName=$(this).siblings(".one-file-name").text();
        e.stopPropagation();
        var fileId = $(this).attr("data");
        var catalog = $(this).attr("data-id");
        if(catalog==="1"){
            var lujing={"url":urls.concat(wjjName),"id":id.concat(projectId)};
            sessionStorage.setItem("lujing",JSON.stringify(lujing));
            window.location.href = "/file/list.html?projectId=" + projectId + "&fileId=" + fileId+"&currentGroup="+groupId;
        }else{
            var lujing={"url":urls.concat(wjjName),"id":id.concat(projectId)};
            sessionStorage.setItem("lujing",JSON.stringify(lujing));
            $.post('/file/hasPermission',{"fileId":fileId},function (data) {
                if(data.result===1&&data.hasPermission){
                    window.location.href = "/file/fileDetail.html?fileId="+fileId;
                }else{
                    layer.msg("无权访问",{icon:5});
                }
            });
        }
    });

    // 下载
    $('html').on('click','.img-show-download',function (e) {
        e.stopPropagation();
        var fileId = $(this).parent().attr("data");
        var args = {"fileId":fileId};
        if(fileName === '公共模型库'){
            args = {"fileId":fileId,"isPublic":true};
            location.href = "/file/downloadFile?fileId=" + fileId+"&isPublic="+true;
            return false;
        }
        $.post('/file/hasPermission',args,function (data) {
            if(data.result===1&&data.hasPermission){
                location.href = "/file/downloadFile?fileId=" + fileId;
            }else{
                layer.msg("无权访问",{icon:5});
            }
        });
    });


    var url = window.location.href;
    if (url.indexOf("file") > 0) {
        $(".toper-nav ul li:nth-of-type(3)").addClass("now").siblings().removeClass("now");
    }

    // 复制文件 弹框
    function cloneFile(name, fileIds) {
        var url = window.location.href;
        layui.use('layer', function () {
            var layer = layui.layer;
            layer.open({
                type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
                title: false, //标题
                area: ['800px', '541px'],
                fixed: false,
                shadeClose: true, //点击遮罩关闭
                anim: 1,  //动画 0-6
                content: '/file/' + name + 'File.html?fileIds=' + fileIds + '&url=' + url
            });
        });
    }

});




