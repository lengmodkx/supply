function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return "";
}

var projectId = $("#projectId").val();
var parentId = getQueryString("parentId");

$(function () {
    var selectNum = 0;

    layui.use(['form', 'upload', 'layer'], function () {
        var form = layui.form
            , upload = layui.upload
            , layer = layui.layer;

        //监听提交
        form.on('submit(formDemo)', function (data) {
            layer.msg(JSON.stringify(data.field));
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
        //选中复选框
        form.on('checkbox(checks)', function (data) {
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
                layui.use('form', function () {
                    var form = layui.form;
                    form.render();
                });
                $(".one-file>div").addClass("layui-form-checked");
                movein($(".one-file"));
                $(".one-file>div").css("opacity", "1");
                $(".file-names>span").text("已选择" + i + "项")

            } else {
                $(".file-caozuo-wrap").hide();
                $(".one-file").css("border", "1px solid #e5e5e5");
                $(".one-file-wrap input").attr('checked', false);
                layui.use('form', function () {
                    var form = layui.form;
                    form.render();
                });
                moveout($(".one-file"));
                $(".file-names>span").text("全选")
            }

        });

        /**
         * 文件上传
         */
        upload.render({
            elem: '#uploadFile' //绑定元素
            , url: '/file/uploadFile' //上传接口
            , method: 'post'
            , data: {projectId: projectId, parentId: parentId}
            , exts: '|' // 可上传所有类型的文件
            , done: function (data) {
                //上传完毕回调
                if (data.result === 1) {
                    layer.msg(data.msg, {icon: 1, time: 1000}, function () {
                        window.location.reload();
                    });
                } else {
                    layer.msg(data.msg, {icon: 2});
                }
            }
            , error: function () {
                //请求异常回调
                layer.msg('上传失败', {icon: 2});
            }
        });


        // 移到回收站
        $(".deleteFile").click(function () {
            var checks = document.getElementsByName("fileCheck");
            var fileIds = "";
            for (var k in checks) {
                if (checks[k].checked)
                    fileIds += checks[k].value + ",";
            }
            fileIds = fileIds.substring(0, fileIds.length - 1);
            $.post("/file/recoveryFile", {fileIds: fileIds}, function (data) {
                if (data.result === 1) {
                    window.location.reload();
                } else {
                    layer.msg(data.msg, {icon: 2})
                }
            });
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
            console.log(fileIds);
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
            console.log(fileIds);
            cloneFile('move', fileIds);
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
        that.find("div").css("opacity", "1");
        that.find(".img-show-operate").css("opacity", "1");
        that.find("i").css("opacity", "1");
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

    // 缩略图模式创建
    $(".new-file-wrap input").keypress(function (e) {
        var folderName = $(".new-file-wrap input").val();
        if (e.which == 13) {
            if (folderName == '') {
                $(".new-file").hide();
                $(".new-file-wrap").hide();
                return false
            } else {
                $.post("/file/createFolder", {
                    projectId: projectId,
                    parentId: parentId,
                    folderName: folderName
                }, function (data) {
                    if (data.result === 1) {
                        layer.msg(data.msg, {icon: 1, time: 1000}, function () {
                            window.location.reload();
                        });
                    }
                });
                // var mydate = new Date();
                // var str= (mydate.getMonth()+1) + "月";
                // str += mydate.getDate() + "日";
                // $(".new-file-wrap").after(' <li class="boxsizing one-file-wrap layui-form">\n' +
                //     '                    <div class="one-file boxsizing">\n' +
                //     '                        <input class="pick-it" type="checkbox" name="" title="" lay-skin="primary" lay-filter="checks">\n' +
                //     '                        <img src="../static/image/nofile.png" th:src="@{/image/nofile.png}">\n' +
                //     '<i class="layui-icon layui-icon-download-circle img-show-download" style="font-size: 20px; color: #ADADAD;"></i>' +
                //     '<div class="img-show-operate"> <i class="layui-icon layui-icon-down " style="font-size: 12px; color: #ADADAD;"></i></div>'+
                //     '                    </div>\n' +
                //     '                    <div class="one-file-name">'+$(this).val()+'</div>\n' +
                //     '                </li>');
                // $(".new-file").hide();
                // $(".new-file-wrap").hide();
                // layui.use('form', function(){
                //     var form = layui.form;
                //     form.render();
                // });
            }
        }
    });
    // 文件菜单 弹出框
    $(".show-operate").click(function () {
        var top = $(this).offset().top + 30 + 'px';
        var left = $(this).offset().left - 170 + 'px';
        headphoto(top, left)
    });

    function headPhoto(top, left, fileId) {
        layui.use('layer', function () {
            var layer = layui.layer;
            layer.open({
                type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
                title: false, //标题
                offset: [top, left],
                area: ['340px', '360px'],
                fixed: true,
                shadeClose: true,
                closeBtn: 0,
                shade: 0,
                anim: 1,  //动画 0-6
                content: ['/file/optionFile?fileId=' + fileId]
            });
        });
    }

    $(".fileList").each(function () {
        // 进入下级目录
        $(this).find(".folderFile").click(function () {
            var fileId = $(this).parent().next().attr("data");
            window.location.href = "/file/list.html?projectId=" + projectId + "&parentId=" + fileId;
        });

        // 文件点击
        $(this).find(".textFile").click(function () {
            var fileId = $(this).parent().next().attr("data");
            loadFile(fileId);
        });

        // 下载
        $(this).find(".img-show-download").click(function () {
            var fileId = $(this).parent().next().attr("data");
            location.href = "/file/downloadFile?fileId=" + fileId;
        });

        // 点击下拉箭头
        $(this).find(".img-show-operate").click(function () {
            var fileId = $(this).parent().next().attr("data");
            var top = $(this).offset().top - 365 + 'px';
            var left = $(this).offset().left - 170 + 'px';
            headPhoto(top, left, fileId)
        });
    });


    //点击文件夹
    // $(".one-file-wrap img").click(function () {
    //     $(".new-file-wrap").siblings().remove();
    //     for (var i = 0; i < 3; i++) {
    //         $(".img-show").append('<li class="boxsizing one-file-wrap fileList" th:each="file:${fileList}">\n' +
    //             '                    <div class="one-file boxsizing">\n' +
    //             '                        <input class="pick-it" type="checkbox" name="" title="" lay-skin="primary" lay-filter="checks">\n' +
    //             '                        <img src="/image/nofile.png">\n' +
    //             '                        <i class="layui-icon layui-icon-download-circle img-show-download" style="font-size: 20px; color: #ADADAD;"></i>\n' +
    //             '                        <div class="img-show-operate">\n' +
    //             '                            <i class="layui-icon layui-icon-down " style="font-size: 12px; color: #ADADAD;"></i>\n' +
    //             '                        </div>\n' +
    //             '                    </div>\n' +
    //             '                    <div class="one-file-name"  th:text="${file.fileName}">图片.zip</div>\n' +
    //             '                    <span th:data="${file.fileId}"></span>\n' +
    //             '                </li>')
    //     }
    //     // 文件库 后面 添加 “> 图片” 文字
    //     $(".file-toper").append(' <span> <i class="layui-icon layui-icon-right" style="font-size: 14px; color: #B4B4B4;"></i> 图片 </span>')
    //
    //     // 点击 “文件库 > 图片” 再把点击元素的后面的兄弟元素span移除
    //
    //
    // });

    //下载文件弹框
    function loadFile(fileId) {
        layui.use('layer', function () {
            var layer = layui.layer;
            layer.open({
                type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
                title: false, //标题
                offset: '50px',
                area: ['100%', '100%'],
                fixed: true,
                shadeClose: true,
                closeBtn: 0,
                shade: 0,
                anim: 1,  //动画 0-6
                content: ['/file/openDownloadFile?fileId=' + fileId]
            });
        });
    }

    var url = window.location.href;
    if (url.indexOf("file") > 0) {
        $(".toper-nav ul li:nth-of-type(3)").addClass("now").siblings().removeClass("now");
    }

    // 复制文件 弹框
    function cloneFile(name, fileIds) {
        layui.use('layer', function () {
            var layer = layui.layer;
            layer.open({
                type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
                title: false, //标题
                area: ['800px', '541px'],
                fixed: false,
                shadeClose: true, //点击遮罩关闭
                anim: 1,  //动画 0-6
                content: '/file/' + name + 'File.html?fileIds=' + fileIds
            });
        });
    }

});




