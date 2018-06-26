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
        form.on('checkbox(checks)', function(data){
            if(data.elem.checked){
                $(this).parent().css("border","4px solid #3da8f5");
                movein($(this).parent())
            }else {
                $(this).parent().css("border","1px solid #e5e5e5");
                moveout($(this).parent())
            }

        });
        // 点击全选
        form.on('checkbox(check-all)', function(data){
            var i=$(".one-file").length-1;
            if(data.elem.checked){
                $(".one-file").css("border","4px solid #3da8f5");
                $(".one-file input").attr('checked',true);
                layui.use('form', function(){
                    var form = layui.form;
                    form.render();
                });
                $(".one-file>div").addClass("layui-form-checked");
                movein($(".one-file"));
                $(".one-file>div").css("opacity","1");
                $(".file-names>span").text("已选择" + i+ "项")

            }else {
                $(".one-file").css("border","1px solid #e5e5e5");
                $(".one-file-wrap input").attr('checked',false);
                layui.use('form', function(){
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
    // 鼠标移入移出，显示隐藏 按钮 和 选择框
    $("html").on("mouseover",".one-file",function () {
        movein($(this))
    });
    $("html").on("mouseleave",".one-file",function () {
        if ($(this).find("input").is(':checked')) {
            return false
        }else if ($(this).find("input").attr("checked")=="checked") {
            return false
        }else {
            moveout($(this))
        }
    });
    // 鼠标移入 函数
    function movein(that){
    that.find("div").css("opacity","1");
    that.find(".img-show-operate").css("opacity","1");
    that.find("i").css("opacity","1");
    }
    function moveout(that){
        that.find("div").css("opacity","0");
        that.find(".img-show-operate").css("opacity","0");
        that.find("i").css("opacity","0");
    }
    /**
     * 创建文件夹
     */
    $("#createFolder").click(function () {
        $(".new-file").show();
        $(".new-file-wrap").show();

    });
    // 缩略图模式创建
    $(".new-file-wrap input").keypress(function (e){
        if (e.which==13){
            if ($(".new-file-wrap input").val()=='') {
                $(".new-file").hide();
                $(".new-file-wrap").hide();
                return false
            }else {
                var mydate = new Date();
                var str= (mydate.getMonth()+1) + "月";
                str += mydate.getDate() + "日";
                $(".new-file-wrap").after(' <li class="boxsizing one-file-wrap layui-form">\n' +
                    '                    <div class="one-file boxsizing">\n' +
                    '                        <input class="pick-it" type="checkbox" name="" title="" lay-skin="primary" lay-filter="checks">\n' +
                    '                        <img src="../static/image/nofile.png" th:src="@{/image/nofile.png}">\n' +
                    '<i class="layui-icon layui-icon-download-circle img-show-download" style="font-size: 20px; color: #ADADAD;"></i>' +
                    '<div class="img-show-operate"> <i class="layui-icon layui-icon-down " style="font-size: 12px; color: #ADADAD;"></i></div>'+
                    '                    </div>\n' +
                    '                    <div class="one-file-name">'+$(this).val()+'</div>\n' +
                    '                </li>');
                $(".new-file").hide();
                $(".new-file-wrap").hide();
                layui.use('form', function(){
                    var form = layui.form;
                    form.render();
                });
            }
        }
    });
    // 文件菜单 弹出框
    $(".show-operate").click(function () {
        var top=$(this).offset().top +30 +'px';
        var left=$(this).offset().left -170 +'px';
        headphoto(top,left)
    });
    $(".img-show-operate").click(function () {
        var top=$(this).offset().top -350 +'px';
        var left=$(this).offset().left -170 +'px';
        headphoto(top,left)
    });
    function headphoto(top,left) {
        layui.use('layer', function(){
            var layer = layui.layer;
            layer.open({
                type: 2,  //0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
                title: false, //标题
                offset:[top,left],
                area:['340px','360px'],
                fixed: true,
                shadeClose: true,
                closeBtn: 0,
                shade: 0,
                anim: 1,  //动画 0-6
                content: ['tk-filemenu.html','no']
            });
        });
    }
    
});



