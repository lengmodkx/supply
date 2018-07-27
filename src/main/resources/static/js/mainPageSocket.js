// 建立连接对象（还未发起连接）
var socket = new SockJS("/webSocketServer");
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
        console.log(task);
        if(task.type === '创建了任务'){
            taskShow(task.task);
        }
        if(task.type === '把任务执行者指派给了'){
            changeExecutor(task.taskId,IMAGE_SERVER+task.userInfo.image);
        }
        if(task.type ==='添加菜单'){
            showMenu(task.object.menu);
        }
        if(task.type == '更新菜单名称'){
            $('#'+task.menuId+' .relationName').html(task.menuName);
        }
        if(task.type == '更新任务名称为'){
            $('#'+task.object.taskId+' .assignment-title').html(task.object.taskName);
        }
    });

    function taskShow(task) {
        var a = '<li class="task-card-mode clearfix" id="'+task.taskId+'">\n' +
            '                                  <div class="task-card">\n' +
            '                                      <!--//左边框线-->\n' +
            '                                      <div class="task-priority bg-priority-0"></div>\n' +
            '                                      <div class="task-check-box">\n' +
            '                                          <input type="checkbox" name="" title="" lay-skin="primary" class="is-sure" />\n' +
            '                                      </div>\n' +
            '                                      <div class="task-content-set" data="'+task.taskId+'" data-value="'+task.projectId+'">\n' +
            '                                          <header class="assignment-top-box boxsizing clearfix">\n' +
            '                                              <span class="assignment-title">'+task.taskName+'</span>';
        if(task.executorInfo != null) {
                a += '<img class="assignment-tx" src="' + IMAGE_SERVER + task.executorInfo.userInfo.image + '" /></header><section class="assignment-bottom-box clearfix">';
        }

        if(task.repeat!=='不重复'){
            a+='<span class="how-repeat">'+task.repeat+'</span>';
        }

        if(task.remind!=='不提醒'){
            a+=' <img src="/image/zhong.png"/>';
        }

        $('.taskList'+task.taskMenuId).append(a);
        layui.form.render();
    }

    function showMenu(menu){
        $(".creat-model").before('<li class="model tile boxsizing">\n' +
            '        <div class="model-title">\n' +
            '            <span class="relationName"> ' + menu.relationName + ' </span>\n' +
            '            <i class="layui-icon layui-icon-down add-new-model xjt" style="font-size: 15px; color: #B5B5B5;" title="添加或编辑任务列表" id="'+menu.relationId+'" data="'+menu.relationName+'"></i>\n' +
            '        </div>\n' +
            '        <!--任务列表-->\n' +
            '        <div class="ul-wrap layui-form">\n' +
            '           <ul id="'+ menu.relationId +'" class="taskList taskList'+menu.relationId+'"></ul>\n' +
            '           <div class="add-task-box boxsizing" style="display: none" data='+menu.relationId+'></div>\n'+
            '        </div>\n' +
            '        <!--添加任务按钮-->\n' +
            '        <div class="add-assignment" data = "' + menu.projectId + '">\n' +
            '            <i class="layui-icon layui-icon-add-circle add-icon" style="font-size: 22px; color: #80BEE4;"></i>\n' +
            '            <span>添加任务</span>\n' +
            '        </div>\n' +
            '    </li>');
    }
}