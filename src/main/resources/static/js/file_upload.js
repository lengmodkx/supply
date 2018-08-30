layui.use(['element','form','layer'], function() {
    var $ = layui.jquery
        , element = layui.element
        , layer = layui.layer; //Tab的切换功能，切换事件监听等，需要依赖element模块

    var accessid = 'LTAIP4MyTAbONGJx';
    var accesskey = 'coCyCStZwTPbfu93a3Ax0WiVg3D4EW';
    var host = 'https://art1001-bim-5d.oss-cn-beijing.aliyuncs.com';

    var index = 0;
    g_dirname = 'upload/' + projectId + "/";
    g_object_name = '';
    var g_object_names = [];
    g_object_name_type = 'random_name';
    now = timestamp = Date.parse(new Date()) / 1000;
    var fileTemps = [];
    var policyText = {
        "expiration": "2020-01-01T12:00:00.000Z", //设置该Policy的失效时间，超过这个失效时间之后，就没有办法通过这个policy上传文件了
        "conditions": [
            ["content-length-range", 0, 1048576000] // 设置上传文件的大小限制
        ]
    };

    var policyBase64 = Base64.encode(JSON.stringify(policyText))
    message = policyBase64
    var bytes = Crypto.HMAC(Crypto.SHA1, message, accesskey, {asBytes: true});
    var signature = Crypto.util.bytesToBase64(bytes);

    function random_string(len) {
        len = len || 32;
        var chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';
        var maxPos = chars.length;
        var pwd = '';
        for (i = 0; i < len; i++) {
            pwd += chars.charAt(Math.floor(Math.random() * maxPos));
        }
        return pwd;
    }

    function get_suffix(filename) {
        pos = filename.lastIndexOf('.')
        suffix = ''
        if (pos != -1) {
            suffix = filename.substring(pos)
        }
        return suffix;
    }

    function calculate_object_name(filename) {
        if (g_object_name_type == 'local_name') {
            g_object_name += "${filename}"
        }
        else if (g_object_name_type == 'random_name') {
            suffix = get_suffix(filename)
            g_object_name = g_dirname + random_string(10) + suffix
        }
        g_object_names.push(g_object_name);
        return ''
    }

    function get_uploaded_object_name(filename) {
        if (g_object_name_type == 'local_name') {
            tmp_name = g_object_name
            tmp_name = tmp_name.replace("${filename}", filename);
            return tmp_name
        }
        else if (g_object_name_type == 'random_name') {
            return g_object_name
        }
    }

    function set_upload_param(up, filename, ret) {
        g_object_name = g_dirname;
        if (filename != '') {
            suffix = get_suffix(filename)
            calculate_object_name(filename)
        }
        new_multipart_params = {
            'key': g_object_name,
            'policy': policyBase64,
            'OSSAccessKeyId': accessid,
            'success_action_status': '200', //让服务端返回200,不然，默认会返回204
            'signature': signature,
        };

        up.setOption({
            'url': host,
            'multipart_params': new_multipart_params
        });
        up.start();
    }

    var uploader = new plupload.Uploader({
        runtimes: 'html5,flash,silverlight,html4',
        browse_button: 'selectfiles',
        //multi_selection: false,
        container: document.getElementById('container'),
        flash_swf_url: 'js/lib/plupload-2.1.2/js/Moxie.swf',
        silverlight_xap_url: 'js/lib/plupload-2.1.2/js/Moxie.xap',
        url: 'http://oss.aliyuncs.com',

        init: {
            PostInit: function () {
                document.getElementById('postfiles').onclick = function () {
                    set_upload_param(uploader, '', false);
                    $('.btn-box').addClass('layui-btn layui-btn-disabled');
                    return false;
                };
            },
            FilesAdded: function (up, files) {
                var content = '';
                $('.file-upload').removeClass("show-file-upload");
                plupload.each(files, function (file) {
                    // $('.fileList').append('<li class="boxsizing" style="width: 550px;height: 40px;background-color: #eee;padding: 4px 8px;line-height: 20px;font-size: 12px;margin: 2px auto;margin-top: 0"><div id="' + file.id + '" class="over-hidden">' + file.name + ' (' + plupload.formatSize(file.size) + ')<i class="layui-icon layui-icon-close" style="font-size: 16px; color: gray;float: right;cursor: pointer"></i>'
                    //     +'<div class="progress"><div class="progress-bar" style="width: 0%"></div></div>'
                    //     +'</div></li>');

                    for (var i = 0; i < exts.length; i++) {
                        var index = file.name.lastIndexOf('.');
                        var ext = file.name.substring(index, file.name.length);
                        if (exts[i] === ext) {
                            previewImage(file, function (imgsrc) {
                                content = '<li class="file-list-li boxsizing" data-id = ' + file.id + '>\n' +
                                    '                                <div class="file-list-li-con">\n';
                                content += '<img src="' + imgsrc + '" />';
                                content += '                                    <p class="file-con-name over-hidden">' + file.name + '</p>\n' +
                                    '                                    <i class="layui-icon layui-icon-close" style="font-size: 16px; color: gray;float: right;cursor: pointer"></i>\n' +
                                    '                                    <p class="file-con-size over-hidden">' + '(' + plupload.formatSize(file.size) + ') ' + '</p>\n' +
                                    '                                </div>\n' +
                                    '                                <div class="layui-progress" lay-filter = ' + file.id + '>\n' +
                                    '                                    <div class="layui-progress-bar" lay-percent="0%"></div>\n' +
                                    '                                </div>\n' +
                                    '                            </li>';
                                $('.fileList').append(content);
                            });
                            break;
                        }
                        if (i === exts.length - 1) {
                            content = '<li class="file-list-li boxsizing" data-id = ' + file.id + '>\n' +
                                '                                <div class="file-list-li-con">\n';
                            content += '<img src="/image/file_1.png">\n';
                            content += '                                    <p class="file-con-name over-hidden">' + file.name + '</p>\n' +
                                '                                    <i class="layui-icon layui-icon-close" style="font-size: 16px; color: gray;float: right;cursor: pointer"></i>\n' +
                                '                                    <p class="file-con-size over-hidden">' + '(' + plupload.formatSize(file.size) + ')' + '</p>\n' +
                                '                                </div>\n' +
                                '                                <div class="layui-progress" lay-filter = ' + file.id + '>\n' +
                                '                                    <div class="layui-progress-bar" lay-percent="0%"></div>\n' +
                                '                                </div>\n' +
                                '                            </li>';
                            $('.fileList').append(content);
                        }
                    }

                });
                $(".fujian-box").slideUp();
            },
            BeforeUpload: function (up, file) {
                $('.layui-progress').show();
                set_upload_param(up, file.name, true);
            },
            UploadProgress: function (up, file) {
                // var d = $('#' + file.id);
                // var prog = d.find('.progress');
                // var progBar = prog.find('.progress-bar');
                // progBar.width(file.percent + '%');
                element.progress(file.id, file.percent+'%');
                //progBar.attr('aria-valuenow', file.percent);
            },
            FileUploaded: function (up, file, info) {
                if (info.status === 200) {
                    var fileT = {};
                    fileT.fileName = file.name;
                    fileT.fileUrl = get_uploaded_object_name(file.name);
                    fileT.size = plupload.formatSize(file.size);
                    fileTemps.push(fileT);
                }
                else {
                    console.log(info.response);
                }
                index++;
            },
            UploadComplete: function (uploader, files) {
                if (($('#chat').val() === '' && $('#chat').val() === null) && fileTemps.length === 0) {
                    return false;
                }
                $.post('/chat/saveChat', {
                    "projectId": projectId,
                    "content": $('#chat').val(),
                    "files": JSON.stringify(fileTemps)
                }, function (data) {
                    if (data.result === 1) {
                        fileTemps = [];
                        $('.no-msg').remove();
                        $('#chat').val('');
                        $('.fileList').html('');
                        $('.btn-box').removeClass('layui-btn layui-btn-disabled');
                    }
                });
            },
            Error: function (up, err) {
                alert(err.message);
                console.log(err.response);
            }
        }

    });
    uploader.init();

//plupload(1.2)中为我们提供了mOxie对象
//有关mOxie的介绍和说明请看：https://github.com/moxiecode/moxie/wiki/API
    function previewImage(file, callback) { //file为plupload事件监听函数参数中的file对象,callback为预览图片准备完成的回调函数
        if (!file || !/image\//.test(file.type)) return;
        if (file.type == 'image/gif') { //gif使用FileReader进行预览,因为mOxie.Image只支持jpg和png
            var fr = new mOxie.FileReader();
            fr.onload = function () {
                callback(fr.result);
                fr.destroy();
                fr = null;
            }
            fr.readAsDataURL(file.getSource());
        } else {
            var preloader = new mOxie.Image();
            preloader.onload = function () {
                preloader.downsize(80, 80); //先压缩一下要预览的图片,宽300，高300
                var imgsrc = preloader.type == 'image/jpeg' ? preloader.getAsDataURL('image/jpeg', 80) : preloader.getAsDataURL(); //得到图片src,实质为一个base64编码的数据
                callback && callback(imgsrc); //callback传入的参数为预览图片的url
                preloader.destroy();
                preloader = null;
            };
            preloader.load(file.getSource());
        }
    }

    //点击一个上传完成文件的x 移除掉该文件
    $('html').on('click', '.layui-icon-close', function () {
        var id = $(this).parents('li').attr('data-id');

        uploader.removeFile(id);
        $(this).parents('li').remove();
    });


});