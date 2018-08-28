layui.use(['element','form','layer'], function() {
    var $ = layui.jquery
        , element = layui.element
        ,layer = layui.layer; //Tab的切换功能，切换事件监听等，需要依赖element模块
var accessid= 'LTAIP4MyTAbONGJx';
var accesskey= 'coCyCStZwTPbfu93a3Ax0WiVg3D4EW';
var host = 'https://art1001-bim-5d.oss-cn-beijing.aliyuncs.com';

g_dirname = 'upload/project/'+projectId;
g_object_name = '';
g_object_name_type = 'random_name';
now = timestamp = Date.parse(new Date()) / 1000;
var fileCommon = {};
var fileModel = {};
var fileTemps = [];
var policyText = {
    "expiration": "2020-01-01T12:00:00.000Z", //设置该Policy的失效时间，超过这个失效时间之后，就没有办法通过这个policy上传文件了
    "conditions": [
        ["content-length-range", 0, 1048576000] // 设置上传文件的大小限制
    ]
};

var policyBase64 = Base64.encode(JSON.stringify(policyText))
message = policyBase64
var bytes = Crypto.HMAC(Crypto.SHA1, message, accesskey, { asBytes: true }) ;
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

function calculate_object_name(filename)
{
    if (g_object_name_type == 'local_name')
    {
        g_object_name += "${filename}"
    }
    else if (g_object_name_type == 'random_name')
    {
        suffix = get_suffix(filename)
        g_object_name = g_dirname + random_string(10) + suffix
    }
    return ''
}

function get_uploaded_object_name(filename)
{
    if (g_object_name_type == 'local_name')
    {
        tmp_name = g_object_name
        tmp_name = tmp_name.replace("${filename}", filename);
        return tmp_name
    }
    else if(g_object_name_type == 'random_name')
    {
        return g_object_name
    }
}

function set_upload_param(up, filename, ret)
{
    g_object_name = g_dirname;
    if (filename != '') {
        suffix = get_suffix(filename)
        calculate_object_name(filename)
    }
    new_multipart_params = {
        'key' : g_object_name,
        'policy': policyBase64,
        'OSSAccessKeyId': accessid,
        'success_action_status' : '200', //让服务端返回200,不然，默认会返回204
        'signature': signature,
    };

    up.setOption({
        'url': host,
        'multipart_params': new_multipart_params
    });

    up.start();
}

var uploader = new plupload.Uploader({
    runtimes : 'html5,flash,silverlight,html4',
    browse_button : 'upModel',
    //multi_selection: false,
    container: document.getElementById('container'),
    flash_swf_url : 'js/lib/plupload-2.1.2/js/Moxie.swf',
    silverlight_xap_url : 'js/lib/plupload-2.1.2/js/Moxie.xap',
    url : 'http://oss.aliyuncs.com',
    filters: {
        mime_types : [ //只允许上传图片和zip文件
            { title : "model files", extensions : "pln,skp,dwg,dxf,dae,gsm,tpl,3ds,ifc,obj" }
        ],
        max_file_size : '1024mb', //最大只能上传400kb的文件
        prevent_duplicates : true //不允许选取重复文件
    },
    init: {

        FilesAdded: function(up, files) {
            // $('.file-upload').removeClass("show-file-upload");
            plupload.each(files, function(file) {
                $('.model-name').html(file.name);
                $('.upModel').show();
                set_upload_param(up, file.name, true);
                $('.model-icon').hide();
                $('.model').show();
            });
        },
        UploadProgress: function(up, file) {
            // var d = $('#'+file.id);
            // var prog = d.find('.progress');
            // var progBar = prog.find('.progress-bar');
            // progBar.width(5*file.percent+'px');
            element.progress('upModel', file.percent+'%');
            // progBar.attr('aria-valuenow', file.percent);
        },
        FileUploaded: function(up, file, info) {
            if (info.status === 200)
            {
                fileModel.fileName = file.name;
                fileModel.fileUrl = get_uploaded_object_name(file.name);
                fileModel.size = plupload.formatSize(file.size);
            }
            else
            {
                console.log(info.response);
            }
        },

        Error: function(up, err) {
            console.log(err.response);
        }

    }
});
uploader.init();


    var uploader1 = new plupload.Uploader({
        runtimes : 'html5,flash,silverlight,html4',
        browse_button : 'upModel2',
        //multi_selection: false,
        container: document.getElementById('container'),
        flash_swf_url : 'js/lib/plupload-2.1.2/js/Moxie.swf',
        silverlight_xap_url : 'js/lib/plupload-2.1.2/js/Moxie.xap',
        url : 'http://oss.aliyuncs.com',
        filters: {
            mime_types : [ //只允许上传图片和zip文件
                { title : "Image files", extensions : "gif,GIF,jpg,JPG,jpeg,JPEG,png,PNG,bmp,BMP" }
            ],
            max_file_size : '10mb', //最大只能上传400kb的文件
            prevent_duplicates : true //不允许选取重复文件
        },
        init: {

            FilesAdded: function(up, files) {
                // $('.file-upload').removeClass("show-file-upload");
                plupload.each(files, function(file) {
                    $('.model-name2').html(file.name);
                    $('.upMode2').show();
                    set_upload_param(up, file.name, true);
                });
            },
            UploadProgress: function(up, file) {
                // var d = $('#'+file.id);
                // var prog = d.find('.progress');
                // var progBar = prog.find('.progress-bar');
                // progBar.width(5*file.percent+'px');
                element.progress('upMode2', file.percent+'%');
                // progBar.attr('aria-valuenow', file.percent);
            },
            FileUploaded: function(up, file, info) {
                if (info.status === 200)
                {
                    fileCommon.fileName = file.name;
                    fileCommon.fileUrl = get_uploaded_object_name(file.name);
                    fileCommon.size = plupload.formatSize(file.size);
                    $('.suolue').attr('src',IMAGE_SERVER+fileCommon.fileUrl);
                    $('.suolue').show();
                    $('.suolue-icon').hide();
                }
                else
                {
                    console.log(info.response);
                }
            },
            Error: function(up, err) {
                console.log(err.response);
            }
        }
    });
    uploader1.init();


    var uploader2 = new plupload.Uploader({
        runtimes : 'html5,flash,silverlight,html4',
        browse_button : 'upload',
        //multi_selection: false,
        container: document.getElementById('container'),
        flash_swf_url : 'js/lib/plupload-2.1.2/js/Moxie.swf',
        silverlight_xap_url : 'js/lib/plupload-2.1.2/js/Moxie.xap',
        url : 'http://oss.aliyuncs.com',
        filters: {
            max_file_size : '1024mb', //最大只能上传400kb的文件
            prevent_duplicates : true //不允许选取重复文件
        },
        init: {

            FilesAdded: function(up, files) {
                // $('.file-upload').removeClass("show-file-upload");
                plupload.each(files, function(file) {
                    var content = '';
                        content += '<li class="boxsizing">\n' +
                            '                <div class="remove-it">\n' +
                            '                    <i class="layui-icon layui-icon-close-fill " style=""></i>\n' +
                            '                </div>\n' +
                            '                <img src="/image/choose.png" alt="">\n' +
                            '                <p class="over-hidden">' + file.name + '</p>\n' +
                            '                <div class="layui-progress ordinaryFile" lay-filter ='+ "upModel" +'>\n' +
                            '                    <div class="layui-progress-bar" lay-percent="0%"></div>\n' +
                            '                </div>\n' +
                            '            </li>'
                    $('.all-file-ul').prepend(content);
                        $('.ordinaryFile').show();
                    set_upload_param(up, file.name, true);
                });
            },
            UploadProgress: function(up, file) {
                // var d = $('#'+file.id);
                // var prog = d.find('.progress');
                // var progBar = prog.find('.progress-bar');
                // progBar.width(5*file.percent+'px');
                element.progress('upModel', file.percent+'%');
                // progBar.attr('aria-valuenow', file.percent);
            },
            FileUploaded: function(up, file, info) {
                if (info.status === 200)
                {
                    var fileT = {};
                    fileT.fileName = file.name;
                    fileT.fileUrl = get_uploaded_object_name(file.name);
                    fileT.size = plupload.formatSize(file.size);
                    fileTemps.push(fileT);
                }
                else
                {
                    console.log(info.response);
                }
            },

            Error: function(up, err) {
                console.log(err.response);
            }

        }
    });
    uploader2.init();

    /**
     * 确定上传模型文件
     */
    $('.model-ok-btn').click(function () {
       if(fileCommon === null || fileModel === null){
           layer.msg("请选择模型和缩略图!");
       } else{
           $.post('/file/uploadModel',{"projectId":projectId,"fileCommon":JSON.stringify(fileCommon),"fileModel":JSON.stringify(fileModel),"parentId":parentId},function (data) {
                if(data.result == 1){
                    parent.window.location.reload();
                    //当你在iframe页面关闭自身时
                    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                    parent.layer.close(index); //再执行关闭
                }else{
                    layer.msg(data.msg);
                }
            },"json");
       }
    });

    /**
     * 确定上传文件
     */
    $('.ordinary-ok-btn').click(function () {
        if(fileTemps === null){
            return false;
        } else{
            $.post('/file/upload',{"projectId":projectId,"files":JSON.stringify(fileTemps),"parentId":parentId},function (data) {
                if(data.result === 1){
                    //当你在iframe页面关闭自身时
                    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                    parent.window.location.reload();
                    parent.layer.close(index); //再执行关闭
                } else{
                    layer.msg(data.msg);
                }
            });
        }
    });
});

