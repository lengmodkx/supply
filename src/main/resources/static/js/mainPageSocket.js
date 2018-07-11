// 建立连接对象（还未发起连接）
var socket = new SockJS("http://localhost:8080/webSocketServer");
// 获取 STOMP 子协议的客户端对象
var stompClient = Stomp.over(socket);

// 向服务器发起websocket连接并发送CONNECT帧
stompClient.connect({},
    function connectCallback(frame) {
        // 连接成功时（服务器响应 CONNECTED 帧）的回调方法
        console.log("连接成功");
        subscribe1();
    },
    function errorCallBack(error) {
        // 连接失败时（服务器响应 ERROR 帧）的回调方法
        console.log("连接失败");
    }
);

//发送消息
function send() {
    var message = "你好啊";
    var messageJson = JSON.stringify({ "name": message });
    stompClient.send("/app/sendTest", {}, messageJson);
    setMessageInnerHTML("/app/sendTest 你发送的消息:" + message);
}

//订阅消息
function subscribe1() {
    stompClient.subscribe('/topic/subscribe', function (response) {
        var returnData = JSON.parse(response.body);
        var task = JSON.parse(returnData.responseMessage);
        if(task.type == '创建了任务'){
            taskShow(task.object.task);
        }
        if(task.type == '把任务执行者指派给了'){
            changeExecutor(task.taskId,IMAGE_SERVER+task.userInfo.image);
        }
        if(task.type == '添加菜单'){
            showMenu(task.object.menu);
        }
    });

    function taskShow(task) {
        var a = '<li class="assignment" id = "'+ task.taskId +'" onclick="javascript:updateTask(\''+ task.taskId +'\',\''+ task.projectId+'\')">'+
            '<div class="assignment-top-box boxsizing">'+
            '<input type="checkbox" name="" title="" lay-skin="primary" class="is-sure" >'+
            '<div class="layui-unselect layui-form-checkbox" lay-skin="primary">'+
            '<i class="layui-icon layui-icon-ok"></i>'+
            '</div>'+
            '<span class="assignment-title">'+task.taskName+'</span>';
        if(task.taskMember == null){
            a += '<img class="assignment-tx" src="/image/add.png" />';
        } else{
            a += '<img class="assignment-tx" src="'+IMAGE_SERVER + task.taskMember.memberImg+'" />';
        }
        a +=   '</div>'+
            '<div class="assignment-bottom-box">'+
            // '<span class="timer">今天9:00-明天</span>'+
            '</div>'+
            '<div class="line"></div>'+
            '</li>';
        var old = $('#'+task.taskMenuId).html();
        $('#'+task.taskMenuId).html(a).append(old);
    }

    function showMenu(menu){
        $(".creat-model").before('<li class="model boxsizing">\n' +
            '        <div class="model-title">\n' +
            '            <span> ' + menu.relationName + ' </span>\n' +
            '            <i class="layui-icon layui-icon-down add-new-model xjt" style="font-size: 15px; color: #B5B5B5;" title="添加或编辑任务列表"></i>\n' +
            '            <!--点击下箭头出现的内容-->\n' +
            '            <div class="lbmenu" style="display: none">\n' +
            '                <div class="lbmenu-title">\n' +
            '                    <p>菜单列表</p>\n' +
            '                    <i class="layui-icon layui-icon-close lbmenu-close" style="font-size: 16px; color: #A6A6A6;"></i>\n' +
            '                </div>\n' +
            '                <div class="lbmenu-add">\n' +
            '                    <div class="lbmenu-add-content">\n' +
            '                        <i class="layui-icon layui-icon-add-1 " style="font-size: 16px; color: gray;"></i>\n' +
            '                        在此处添加新列表\n' +
            '                    </div>\n' +
            '                </div>\n' +
            '                <div class="lbmenu-remove">\n' +
            '                    <div class="lbmenu-add-content">\n' +
            '                        <i class="layui-icon layui-icon-delete " style="font-size: 16px; color: gray;"></i>\n' +
            '                        本列表所有任务移到回收站\n' +
            '                    </div>\n' +
            '                </div>\n' +
            '            </div>\n' +
            '        </div>\n' +
            '        <!--任务列表-->\n' +
            '        <div class="ul-wrap layui-form">\n' +
            '        <ul id="'+ menu.relationId +'">\n' +
            '        </ul>\n' +
            '        </div>\n' +
            '        <!--添加任务按钮-->\n' +
            '        <div class="add-assignment" data = "' + menu.relationId + '">\n' +
            '            <i class="layui-icon layui-icon-add-circle add-icon" style="font-size: 22px; color: #80BEE4;"></i>\n' +
            '            <span>添加任务</span>\n' +
            '        </div>\n' +
            '\n' +
            '    </li>');
    }
}