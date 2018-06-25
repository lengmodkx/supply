$(function () {
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

        /**
         * 创建文件夹
         */
        $("#createFolder").click(function () {
            layer.prompt({title: '新建文件夹', resize: false, formType: 3}, function (folderName, index) {
                $.ajax({
                    url: "/file/createFolder",
                    type: "post",
                    data: {projectId: 1, parentId: 0, folderName: folderName},
                    dataType: "json",
                    success: function (data) {
                        if (data.result === 1) {
                            layer.close(index);
                            layer.msg(data.msg, {icon: 1, time: 1000}, function () {
                                window.location.reload();
                            })
                        } else {
                            layer.msg(data.msg, {icon: 2})
                        }
                    }
                });
            });
        });

        /**
         * 文件上传
         */
        upload.render({
            elem: '#uploadFile' //绑定元素
            , url: '/file/uploadFile?projectId=1' //上传接口
            , exts: '|' // 可上传所有类型的文件
            , done: function (data) {
                //上传完毕回调
                if (data.result === 1) {
                    window.location.reload();
                } else {
                    layer.msg(data.msg, 1, 8);
                }
            }
            , error: function () {
                //请求异常回调
                layer.msg('上传失败', 1, 8);
            }
        });
    });
    
    //点击 列表模式  缩略图 模式 切换
    $(".liebiao-moshi").click(function () {
        $(".img-show").hide();
        $(".liebiao-show").show();
    });
    $(".suoluetu-moshi").click(function () {

        $(".img-show").show();
        $(".liebiao-show").hide()
    })
    
    
    
});



