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

//订阅消息
function subscribe1() {
    stompClient.subscribe('/topic/chat/' + projectId, function (response) {
        var returnData = JSON.parse(response.body);
        var chat = JSON.parse(returnData.responseMessage);

        var content = '';
        //处理收到的消息
        if(chat.type === "收到消息") {

            var data = chat.data;
            if (userId === chat.userId) {
                if (data.fileList != null) {
                    content += '<!--我方文件-->\n' +
                        '                <li class="msg-li boxsizing my-msg have-file-li" data-id = ' + data.id + '>\n' +
                        '<input type="hidden" class="createTime" value="' + data.createTime + '">'+
                        '                    <div class="message-body">\n';
                        if(data.content != null){
                            content+= '<div class="message-content boxsizing"><p>' + data.content + '</p></div>';
                        }
                        content += '                        <ul class="chat-file-ul boxsizing">\n';

                    //这里开始循环文件
                    var fileList = data.fileList;
                    for (var i = 0; i < fileList.length; i++) {
                        content += '                            <li class="clearfix" data-id="' + fileList[i].fileId + '">\n' +
                            '                                <div class="file-flex-box">\n';

                        for(var j = 0;j < chat.exts.length;j++){
                            if(chat.exts[j] === fileList[i].ext){
                                content += '<img src="' + IMAGE_SERVER + fileList[i].fileUrl + '">';
                                break;
                            } else if(j === chat.exts.length - 1){
                                content += '<img src="/image/chatDefault.png">';
                            }
                        }
                        content +=
                        '                                    <p class="chat-file-name over-hidden">' + fileList[i].fileName + '</p>\n' +
                        '                                    <p class="chat-file-size">' + fileList[i].size + '</p>\n' +
                        '                                </div>\n' +
                        '                            </li>\n';
                    }

                       content +=     '                        </ul>\n' +
                            '\n' +
                            '                        <div class="msg-timer clearfix boxsizing">\n' +
                            '                            <span>' + new Date(data.createTime).format("MM月-dd日 hh:mm:ss") + '</span>\n' +
                            '                            <span class="download-chat-file">下载附件</span>\n' +
                            '                            <span class="recall">撤回</span>\n' +
                            '                        </div>\n' +
                            '                    </div>\n' +
                            '                </li>'
                } else{
                        content +=
                        '                <li class="msg-li boxsizing my-msg" data-id = ' + data.id + '>\n' +
                            '<input type="hidden" class="createTime" value="' + data.createTime + '">'+
                        '                    <div class="message-body">\n' +
                        '                        <div class="message-content boxsizing"><p>' + data.content + '</p></div>\n' +
                        '                        <div class="msg-timer clearfix boxsizing">\n' +
                        '                            <span>' + new Date(data.createTime).format('MM月-dd日 hh:mm:ss') + '</span>\n' +
                            '                            <span class="recall">撤回</span>\n' +
                        '                        </div>\n' +
                        '                    </div>\n' +
                        '                </li>'
                }
            } else {

                var fileList = data.fileList;
                if(fileList != null){
                    content += '                <li class="msg-li boxsizing their-msg have-file-li" data-id = ' + data.id + '>\n' +
                        '<input type="hidden" class="createTime" value="' + data.createTime + '">'+
                        '                    <img src="' + IMAGE_SERVER+data.userEntity.userInfo.image + '">\n' +
                        '                    <div class="message-body">\n';
                    if(data.content != null){
                        content+= '<div class="message-content boxsizing"><p>' + data.content + '</p></div>';
                    }
                        content += '<ul class="chat-file-ul boxsizing">\n';

                    for (var i = 0; i < fileList.length; i++) {

                        content += '                            <li class="clearfix" data-id="' + fileList[i].fileId + '">\n' +
                            '                                <div class="file-flex-box">\n';

                        for(var j = 0;j < chat.exts.length;j++){
                            if(chat.exts[j] === fileList[i].ext){
                                content += '<img src="' + IMAGE_SERVER + fileList[i].fileUrl + '">';
                                break;
                            } else if(j === chat.exts.length - 1){
                                content += '<img src="/image/chatDefault.png">';
                            }
                        }
                        content +=
                            '                                    <p class="chat-file-name over-hidden">' + fileList[i].fileName + '</p>\n' +
                            '                                    <p class="chat-file-size">' + fileList[i].size + '</p>\n' +
                            '                                </div>\n' +
                            '                            </li>\n';
                    }
                        content +=
                            '                        </ul>\n' +
                            '                        <div class="msg-timer clearfix boxsizing">\n' +
                            '                            <span>' + data.userEntity.userName + '</span>\n' +
                            '                            <span>' + new Date(data.createTime).format("MM月-dd日 hh:mm:ss") + '</span>\n' +
                            '                            <span class="download-chat-file">下载附件</span>\n' +
                            '                        </div>\n' +
                            '                    </div>\n' +
                            '                </li>'

                } else{
                    content +=
                        '                <li class="msg-li boxsizing their-msg" data-id = ' + data.id + '>' +
                        '                <input type="hidden" class="createTime" value="' + data.createTime + '">'+
                        '                    <img src= ' + IMAGE_SERVER + data.userEntity.userInfo.image + '>\n' +
                        '                    <div class="message-body">' +
                        '                    <div class="message-content boxsizing"><p>' + data.content + '</p></div>\n' +
                        '                        <div class="msg-timer clearfix boxsizing">\n' +
                        '                            <span>' + data.userEntity.userName + '</span>\n' +
                        '                            <span>' + new Date(data.createTime).format('MM月dd日 hh:mm:ss') + '</span>\n' +
                        '                        </div>\n' +
                        '                    </div>\n' +
                        '                </li>';
                }
            }
            $('.section-ul').append(content);
        }

        //消息撤回
        if(chat.type === '撤回了消息'){
            if(chat.id !== userId){
                $('.msg-li').each(function () {
                   if($(this).attr('data-id') === chat.logId) {
                       $(this).css("background-color","red")
                       var content = '<li class="msg-li recall-li boxsizing">\n' +
                           '                        <div class="message-recall">'+ chat.name +' 撤回了一条消息' + '</div>\n' +
                           '                    </li>'
                       $(this).after(content);
                       $(this).remove();
                   }
                });
            }
        }
    });
}




//点击撤回按钮
$('html').on('click','.recall',function () {
    var that = $(this);
    var date = $(this).parents('.msg-li').find('.createTime').val();
    if(new Date().getTime()-date > 120000){
        layer.msg("发送时间超过两分钟的消息,不能被撤回。");
        return false;
    }
    var id = $(this).parents('.msg-li').attr('data-id');
    var url = "/chat/withdrawMessage";
    var args = {"id":id,"projectId":projectId};
    $.post(url,args,function (data) {
        if(data.result === 1){
            var content = '<li class="msg-li recall-li boxsizing">\n' +
                '                        <div class="message-recall">' + '你撤回了一条消息' + '</div>\n' +
                '                    </li>';
            that.parents('.msg-li').after(content);
            that.parents('.msg-li').remove();
        } else{
            layer.msg(data.msg);
        }
    });
});

/**
 * 点击下载附件
 */
$('html').on('click','.download-chat-file',function () {
    var files = [];
    $(this).parents('.msg-timer').siblings('.chat-file-ul').children('li').each(function () {
        files.push($(this).attr('data-id'));
    });
    var args = {"fileIds":files.toString()};
    location.href='/chat/downLoadEnclosure?fileIds='+files.toString();

});