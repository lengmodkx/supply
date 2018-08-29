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

//订阅消息
function subscribe() {
    stompClient.subscribe('/topic/'+taskId, function (response) {
        var returnData = JSON.parse(response.body);
        var taskLog = JSON.parse(returnData.responseMessage);
        console.log(">>>>>",taskLog);
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
            showLog(taskLog.object.taskLog);
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
        if(taskLog.type === '移除标签'){
            $('#'+taskLog.object.tag).remove();
        }
        if(taskLog.type === '把任务移入了回收站'){
            $(".detele-box").show();
            $(".revise-task *").attr("disabled","true");
            $(".revise-task *").css("cursor"," not-allowed");

            $(".hide-zan-x").hide();
            $(".revise-task *").unbind();
            $(".no-renling").click(function (e) {
                return false
            });
        }

        if(taskLog.type==='更新提醒模式为'){
            getLog(taskLog.object.taskLog);
            if(taskLog.object.task.remind==='不提醒'){
                $('.no-remand i').css('color','#a6a6a6');
            }else{
                $('.no-remand i').css('color','#3DA8F5');
            }
        }

        if(taskLog.type==='更新任务的重复'){
            getLog(taskLog.object.taskLog);
            if(taskLog.object.task.remind==='不重复') {
                $('.no-repeat i').css('color','#a6a6a6');
            }else{
                $('.no-repeat i').css('color','#3DA8F5');
            }
        }


        if(taskLog.type === '添加了标签'){
            var tag = taskLog.object.tag;
            var content = '<span class="tag" id="'+tag.tagId+'"  style="background-color:'+tag.bgColor+'">\n' +
                '                    <b style="font-weight: 400">'+tag.tagName+'</b>\n' +
                '                    <i class="layui-icon layui-icon-close-fill remove-tag" style="font-size: 14px; color: #1E9FFF;"></i>\n' +
                '                </span>';
            $(".has-tags").prepend(content);
        }


        if(taskLog.type==='更新了参与者'){
            getLog(taskLog.taskLog);
            var members = taskLog.members;
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

        if(taskLog.type === '更新任务开始结束时间'){
            getLog(taskLog.object.taskLog);
            if(taskLog.object.task.startTime != null){
                $('#beginTimes').val(taskLog.object.startTime);
            } else{
                $('#overTimes').val(taskLog.object.endTime);
            }
        }

        if(taskLog.type === '更新备注'){
            getLog(taskLog.taskLog);
            editor.txt.html(taskLog.task.remarks);
        }

        if(taskLog.type==='添加附件'){
            var taskFile = taskLog.object.taskFile;
            var li='<li class="boxsizing">';
            if(taskFile.fileThumbnail!=null){
                li+='<img src="'+IMAGE_SERVER+taskFile.fileThumbnail+'"/>';
                if(taskFile.fileName.length>10){
                    li += '<p class="over-hidden file-name">'+taskFile.fileName.substring(0,9)+taskFile.fileExt+'</p><div class="cover-box"><span id="'+taskFile.id+'" class="download">下载</span></div>';
                }else{
                    li += '<p class="over-hidden file-name">'+taskFile.fileName+'</p><div class="cover-box"><span id="'+taskFile.id+'" class="download">下载</span></div>';
                }

            }else{
                li+='<img src="/image/file_1.png" />';
                if(taskFile.fileName.length>10){
                    li += '<p class="over-hidden file-name">'+taskFile.fileName.substring(0,9)+taskFile.fileExt+'</p><div class="cover-box"><span id="'+taskFile.id+'" class="download">下载</span></div>';
                }else{
                    li += '<p class="over-hidden file-name">'+taskFile.fileName+'</p><div class="cover-box"><span id="'+taskFile.id+'" class="download">下载</span></div>';
                }
            }
            li+='</li>';
            $('.task-file'+taskLog.object.taskId).append(li);
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

function showLog(logVo){
    var date = new Date().pattern('yyyy-MM-dd HH:mm');
    var log = '<li class="combox clearfix">';
    log+='<div><div class="touxiang-img"><img src="'+IMAGE_SERVER+logVo.userEntity.userInfo.image+'"></div><div class="file-con-box"><div class="file-con-box-header boxsizing"><span class="file-con-box-name">'+logVo.userEntity.userName+'</span><span class="file-con-box-time">'+date+'</span></div><p class="publish-con boxsizing">'+logVo.content+'</p>';
    log+='</div></div></li>';
    $('.log').append(log);
    document.getElementById('showView').scrollIntoView(true);

}