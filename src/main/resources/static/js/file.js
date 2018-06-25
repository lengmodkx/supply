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
                $(this).parent().css("border","4px solid #3da8f5")
            }else {
                $(this).parent().css("border","1px solid #e5e5e5")
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
    
    //点击 列表模式  缩略图 模式 切换
    $(".liebiao-moshi").click(function () {
        $(".img-show-wrap").hide();
        $(".liebiao-show").show();
    });
    $(".suoluetu-moshi").click(function () {

        $(".img-show-wrap").show();
        $(".liebiao-show").hide()
    });

    /**
     * 创建文件夹
     */
    $("#createFolder").click(function () {
        $(".new-file").show();
        $(".new-file-wrap").show();

    });
    // 列表模式  创建
    $(".new-file-input").keypress(function (e) {
        if (e.which==13){
            if ($(".new-file-input").val()=='') {
                $(".new-file").hide();
                $(".new-file-wrap").hide();
                return false
            }else {
                var mydate = new Date();
                var str= (mydate.getMonth()+1) + "月";
                str += mydate.getDate() + "日";
                $(".new-file").after('<li class="layui-row">\n' +
                    '            <div class="layui-col-md2 layui-col-lg-2 file-name"><input type="checkbox" name="" title="" lay-skin="primary" class="is-sure" >'+ $(this).val()+'</div>\n' +
                    '            <div class="layui-col-md2 layui-col-lg-2 file-size">--</div>\n' +
                    '            <div class="layui-col-md2 layui-col-lg-2 file-time">谁谁谁</div>\n' +
                    '            <div class="layui-col-md3 layui-col-lg-3 file-people">'+str+'</div>\n' +
                    '            <div class="layui-col-md3 layui-col-lg-3 file-operate">\n' +
                    '                <i class="layui-icon layui-icon-down show-operate" style="font-size: 18px; color: #ADADAD;"></i>\n' +
                    '                <i class="layui-icon layui-icon-download-circle download" style="font-size: 18px; color: #ADADAD;"></i>\n' +
                    '            </div>\n' +
                    '        </li>');
                $(".new-file-wrap").after(' <li class="boxsizing one-file-wrap">\n' +
                    '                    <div class="one-file boxsizing">\n' +
                    '                        <input class="pick-it" type="checkbox" name="" title="" lay-skin="primary" lay-filter="checks">\n' +
                    '                        <img src="../static/image/nofile.png" th:src="@{/image/nofile.png}">\n' +
                    '                        <i class="layui-icon layui-icon-download-circle img-show-download" style="font-size: 18px; color: #ADADAD;"></i>\n' +
                    '                        <i class="layui-icon layui-icon-down img-show-operate" style="font-size: 18px; color: #ADADAD;"></i>\n' +
                    '                    </div>\n' +
                    '                    <div class="one-file-name">'+$(this).val()+'</div>\n' +
                    '                </li>');
                $(".new-file").hide();
                $(".new-file-wrap").hide();
            }
        }
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
                $(".new-file-wrap").after(' <li class="boxsizing one-file-wrap">\n' +
                    '                    <div class="one-file boxsizing">\n' +
                    '                        <input class="pick-it" type="checkbox" name="" title="" lay-skin="primary" lay-filter="checks">\n' +
                    '                        <img src="../static/image/nofile.png" th:src="@{/image/nofile.png}">\n' +
                    '                        <i class="layui-icon layui-icon-download-circle img-show-download" style="font-size: 18px; color: #ADADAD;"></i>\n' +
                    '                        <i class="layui-icon layui-icon-down img-show-operate" style="font-size: 18px; color: #ADADAD;"></i>\n' +
                    '                    </div>\n' +
                    '                    <div class="one-file-name">'+$(this).val()+'</div>\n' +
                    '                </li>');
                $(".new-file").after('<li class="layui-row">\n' +
                    '            <div class="layui-col-md2 layui-col-lg-2 file-name"><input type="checkbox" name="" title="" lay-skin="primary" class="is-sure" >'+ $(this).val()+'</div>\n' +
                    '            <div class="layui-col-md2 layui-col-lg-2 file-size">--</div>\n' +
                    '            <div class="layui-col-md2 layui-col-lg-2 file-time">谁谁谁</div>\n' +
                    '            <div class="layui-col-md3 layui-col-lg-3 file-people">'+str+'</div>\n' +
                    '            <div class="layui-col-md3 layui-col-lg-3 file-operate">\n' +
                    '                <i class="layui-icon layui-icon-down show-operate" style="font-size: 18px; color: #ADADAD;"></i>\n' +
                    '                <i class="layui-icon layui-icon-download-circle download" style="font-size: 18px; color: #ADADAD;"></i>\n' +
                    '            </div>\n' +
                    '        </li>');
                $(".new-file").hide();
                $(".new-file-wrap").hide();
            }
        }
    });
    
    
    
});



