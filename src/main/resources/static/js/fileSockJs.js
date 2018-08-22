// 建立连接对象（还未发起连接）
var socket = new SockJS("/webSocketServer");
// 获取 STOMP 子协议的客户端对象
var stompClient = Stomp.over(socket);

// 向服务器发起websocket连接并发送CONNECT帧
stompClient.connect({},
    function connectCallback(frame) {
        // 连接成功时（服务器响应 CONNECTED 帧）的回调方法
        console.log("连接成功");
        alert(1);
        subscribe1();
    },
    function errorCallBack(error) {
        // 连接失败时（服务器响应 ERROR 帧）的回调方法
        console.log("连接失败");
    }
);

//订阅消息
function subscribe1() {
    stompClient.subscribe('/topic/' + projectId, function (response) {
        var returnData = JSON.parse(response.body);
        var file = JSON.parse(returnData.responseMessage);
        if(file.type === '将文件移入了回收站'){
            for(var i = 0;i < file.fileIds.length;i++){
                $('.one-file').each(function () {
                    if($(this).attr('data') === file.fileIds[i]){
                        $(this).parent().remove();
                    }
                })
            }
        }
    });
}