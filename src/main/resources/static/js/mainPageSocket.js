// 建立连接对象（还未发起连接）
var socket = new SockJS("http://192.168.31.184:8081/webSocketServer");
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
        taskShow(task);
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
            '<span class="timer">今天9:00-明天</span>'+
            '</div>'+
            '<div class="line"></div>'+
            '</li>';
        var old = $('#'+task.taskMenuId).html();
        $('#'+task.taskMenuId).html(a).append(old);
    }
}