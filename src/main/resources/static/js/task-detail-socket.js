// 建立连接对象（还未发起连接）
var socket = new SockJS('/webSocketServer');
// 获取 STOMP 子协议的客户端对象
var stompClient = Stomp.over(socket);

// 向服务器发起websocket连接并发送CONNECT帧
stompClient.connect({},
    function connectCallback(frame) {
        // 连接成功时（服务器响应 CONNECTED 帧）的回调方法
        console.log("连接成功");
        subscribe();
    },
    function errorCallBack(error) {
        // 连接失败时（服务器响应 ERROR 帧）的回调方法
        console.log("连接失败");
    }
);
//发送消息
$('.publish-btn').click(function (e) {
    var chat = $('#chat').val();
    var args = {"publicId":taskId,"content":chat}
    $.post("/task/chat",args,function (data) {
    },"json");
    var messageJson = JSON.stringify({ "name": chat });
    stompClient.send("/app/sendTest",{"taskId":taskId}, messageJson);
    $('#chat').val('');
    $('#chat').focus();
    var scrollHeight = $('.revise-task').prop("scrollHeight");
    $('.revise-task').animate({scrollTop:scrollHeight}, 400);
});



//订阅消息
function subscribe() {
    stompClient.subscribe('/topic/'+taskId, function (response) {
        var returnData = JSON.parse(response.body);
        var taskLog = JSON.parse(returnData.responseMessage);
        if(taskLog.type === '把任务执行者指派给了'){
            getLog(taskLog.object.taskLog);
            var executorInfo = taskLog.object.executorInfo;
            $('.who-wrap').remove();
            $('.no-renling').remove();
            $('.who-and-time').prepend('<div class="who-wrap">\n' +
                '                <input type="hidden" value="'+executorInfo.id+'" id = "executorId"/>\n' +
                '                <img src="'+IMAGE_SERVER+executorInfo.userInfo.image+'" id = "executorImg" />\n' +
                '                <span id = "executorName">'+executorInfo.userName+'</span>\n' +
                '                <i class="layui-icon layui-icon-close-fill remove-who-wrap" style="font-size: 16px; color: #1E9FFF;"></i>\n' +
                '            </div>');

        }
        if(taskLog.type === '发送消息'){
            getLog(taskLog.object.taskLog);
            var scrollHeight = $('.revise-task').prop("scrollHeight");
            $('.revise-task').animate({scrollTop:scrollHeight}, 400);
        }
        if(taskLog.type === '关联'){
            addBindingStr(taskLog.object.bindingInfo,taskLog.object.publicType,taskLog.object.bId);
        }
        if(taskLog.type === '更新任务名称为'){
            $('.task_name').val(taskLog.object.taskName);
            getLog(taskLog.object.taskLog);
        }

        if(taskLog.type === '更新任务优先级为'){
            getLog(taskLog.object.taskLog);
        }
        if(taskLog.type === '删除了标签'){
            $('#'+taskLog.object.tagId).remove();
        }

        if(taskLog.type==='更新提醒模式为'){
            if(taskLog.object.task.remind==='不提醒'){
                $('.no-remand i').css('color','#a6a6a6');
            }else{
                $('.no-remand i').css('color','#3DA8F5');
            }
        }

        if(taskLog.type==='更新任务的重复'){
            if(taskLog.object.task.remind==='不重复') {
                $('.no-repeat i').css('color','#a6a6a6');
            }else{
                $('.no-repeat i').css('color','#3DA8F5');
            }
        }

        if(taskLog.object.type === 12){
            var tag = taskLog.object.tag;
            var content = '<span class="tag" id="'+tag.tagId+'"  style="background-color:'+tag.bgColor+'">\n' +
                '                    <b style="font-weight: 400">'+tag.tagName+'</b>\n' +
                '                    <i class="layui-icon layui-icon-close-fill remove-tag" style="font-size: 14px; color: #1E9FFF;"></i>\n' +
                '                </span>';
            $(".has-tags").prepend(content);
        }

        //移除标签
        if(taskLog.object.type === 13){
            $('.has-tags span').each(function () {
                if($(this).attr('id') === taskLog.object.tag){
                    $(this).remove();
                }
            });
        }


        if(taskLog.type==='更新了参与者'){
            getLog(taskLog.object.taskLog);
            var members = taskLog.object.members;
            $('.work-people .add-work-people').siblings().remove();
            for(var i=0;i<members.length;i++){
                var div = '<div class="one-work-people" data-id="'+members[i].id+'">\n' +
                    '           <img src="'+IMAGE_SERVER+members[i].userInfo.image+'" />\n' +
                    '           <i class="layui-icon layui-icon-close-fill remove-work-people" style="font-size: 15px; color: #3da8f5;"></i>\n' +
                    '      </div>';

                $('.work-people .add-work-people').before(div);
            }
        }

        if(taskLog.type==='移除了执行者'){
            getLog(taskLog.object.taskLog);

            $('.who-wrap').after('<div class="no-renling">\n' +
                '                <i class="layui-icon layui-icon-username no-people-img" style="font-size: 20px; color: #A6A6A6;"></i>\n' +
                '                <span>待认领</span>\n' +
                '            </div>');
            $('.who-wrap').remove();
        }


        if(taskLog.type === '取消了关联'){
            $('#bind li.data-info ').each(function(){
                if($(this).attr('data-id') == taskLog.object.bId){
                    $(this).remove();
                    if($('.related-rw li.data-info').length == 0){
                        $('.related-rw-wrap').hide();
                    }
                    if($('.related-rc li.data-info').length == 0){
                        $('.related-rc-wrap').hide();
                    }
                    if($('.related-wj li.data-info').length == 0){
                        $('.related-wj-wrap').hide();
                    }
                    if($('.related-fx li.data-info').length == 0){
                        $('.related-fx-wrap').hide();
                    }
                }
            });
        }
    });
}