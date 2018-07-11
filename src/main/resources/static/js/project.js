// 建立连接对象（还未发起连接）
var socket = new SockJs("http://localhost:8080/webSocketServer");
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


//订阅消息
function subscribe1() {
    stompClient.subscribe('/topic/subscribe', function (response) {
        var returnData = JSON.parse(response.body);
        dealMessage(JSON.parse(returnData.responseMessage));
    });
}



//将消息显示在网页上
function dealMessage(project) {

    var li = '<li class="layui-col-md3 layui-anim layui-anim-scaleSpring"\n' +
        '                    style="background-image:url('+IMAGE_SERVER + project.projectCover+');"\n' +
        '                    onclick="javascript:projectClick(\''+project.projectId+'\')"\n' +
        '                    id="'+project.projectId+'">\n' +
        '                    <div class="describe boxsizing">\n' +
        '                        <div>\n' +
        '                            <div class="describe-title">'+project.projectName+'</div>\n' +
        '                            <span class="tools">\n' +
        '                                <i class="layui-icon layui-icon-edit " style="font-size: 18px; color: #eeeeee;" title="打开项目设置"\n' +
        '                                   onclick="javascript:setting(\''+project.projectId+'\')"></i>\n' +
        '                                <i class="layui-icon layui-icon-rate-solid star" style="font-size: 18px; color: #eeeeee;" title="收藏项目"\n' +
        '                                   onclick="javascript:projectCollect(\''+project.projectId+'\')"></i>\n' +
        '                            </span>\n' +
        '                        </div>\n' +
        '                        <p>'+project.projectDes+'</p>\n' +
        '                    </div>\n' +
        '                </li>';

    $(".partake ul").append(li);
}