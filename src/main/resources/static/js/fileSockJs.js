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

        if(file.type === '删除了信息'){
            $('.one-file').each(function () {
                if($(this).attr('data') == file.id){
                    $(this).remove;
                }
            });
        }

        if(file.type === '恢复了信息'){
            var file = file.file;
            if(file.fileName.length > 15){
                file.fileName.substring(0,10) + '...' + file.ext;
            }
            var content = '';
            content += '<li class="boxsizing one-file-wrap" >\n' +
                '                            <div class="one-file boxsizing fileList" data="'+file.fileId+'">\n' +
                '                                <input class="pick-it" type="checkbox" name="fileCheck" th:value="' + file.fileId + '" title="" lay-skin="primary" lay-filter="checks">\n';
            if(file.catalog == 0 && (file.ext == '.jpg' || file.ext == '.png' || file.ext == '.jpeg')){

                content += '<img class="folderFile collect-item-touxiang" src="' + IMAGE_SERVER + file.fileUrl + '"/>';
            } else if(file.catalog == 0 && file.ext == '..doc'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/word_1.png" />';
            } else if(file.catalog == 0 && file.ext == '.xls'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/excel.png" />';
            } else if (file.catalog == 0 && file.ext == '.xlsx'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/excel.png" />';
            } else if(file.catalog == 0 && file.ext == '.pptx'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/ppt.png" />';
            } else if(file.catalog == 0 && file.ext == '.ppt'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/ppt.png" />';
            } else if(file.catalog == 0 && file.ext == '.pdf'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/pdf_1.png" />';
            } else if(file.catalog == 0 && file.ext == '.zip'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/zip.png" />';
            } else if(file.catalog == 0 && file.ext == '.rar'){

                content += '<img class="folderFile collect-item-touxiang" src="/image/rar.png" />';
            } else {
                content += '<img class="folderFile collect-item-touxiang" src="/image/defaultFile.png" />';
            }
             content +=  '                                <i class="layui-icon layui-icon-download-circle img-show-download" style="font-size: 20px; color: #ADADAD;"></i>\n' +
                '                                <div class="img-show-operate">\n' +
                '                                    <i class="layui-icon layui-icon-down" style="font-size: 12px; color: #ADADAD;"></i>\n' +
                '                                </div>\n' +
                '                            </div>\n';
            content += '                            <div class="one-file-name" th:data="' + file.fileId + '">' + file.fileName + '</div>\n' +
                '                        </li>'

            $('#fileListUl').append(content);
        }
    });
}