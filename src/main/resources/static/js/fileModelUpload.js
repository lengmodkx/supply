
var fileCommon = {};
var fileModel = {};
var fileTemps = [];
var g_object_name = '';
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
    var pos = filename.lastIndexOf('.');
    var suffix = '';
    if (pos !== -1) {
        suffix = filename.substring(pos)
    }
    return suffix;
}

layui.use(['element','form','layer'], function() {
    var $ = layui.jquery,element = layui.element,layer = layui.layer;
    var accessid= 'LTAIP4MyTAbONGJx';
    var accesskey= 'coCyCStZwTPbfu93a3Ax0WiVg3D4EW';
    var host = 'https://art1001-bim-5d.oss-cn-beijing.aliyuncs.com';

    var policyText = {
        "expiration": "2020-01-01T12:00:00.000Z", //设置该Policy的失效时间，超过这个失效时间之后，就没有办法通过这个policy上传文件了
        "conditions": [
            ["content-length-range", 0, 1048576000] // 设置上传文件的大小限制
        ]
    };

    var policyBase64 = Base64.encode(JSON.stringify(policyText));
    var bytes = Crypto.HMAC(Crypto.SHA1, policyBase64, accesskey, { asBytes: true }) ;
    var signature = Crypto.util.bytesToBase64(bytes);

    function calculate_object_name(suffix) {
        g_object_name = g_dirname+random_string(10) + suffix;
    }

    function get_uploaded_object_name() {
        return g_object_name;
    }

    function set_upload_param(up, filename, ret) {
        if (filename !== '') {
            var suffix = get_suffix(filename);
            calculate_object_name(suffix)
        }
        var new_multipart_params = {
            'key' : g_object_name,
            'policy': policyBase64,
            'OSSAccessKeyId': accessid,
            'success_action_status' : '200', //让服务端返回200,不然，默认会返回204
            'signature': signature
        };

        up.setOption({
            'url': host,
            'multipart_params': new_multipart_params
        });

        up.start();
    }

    var uploader1 = new plupload.Uploader({
        runtimes : 'html5,flash,silverlight,html4',
        browse_button : 'upModel2',
        multi_selection: false,
        container: document.getElementById('container'),
        flash_swf_url : 'js/lib/plupload-2.1.2/js/Moxie.swf',
        silverlight_xap_url : 'js/lib/plupload-2.1.2/js/Moxie.xap',
        url : 'http://oss.aliyuncs.com',
        filters: {
            mime_types : [
                { title : "Image files", extensions : "gif,GIF,jpg,JPG,jpeg,JPEG,png,PNG,bmp,BMP" }
            ],
            max_file_size : '10mb', //最大只能上传10mb的文件
            prevent_duplicates : true //不允许选取重复文件
        },
        init: {

            FilesAdded: function(up, files) {
                plupload.each(files, function(file) {
                    $('.model-name2').html(file.name);
                    $('.upMode2').show();
                    set_upload_param(up, file.name, true);
                });
            },
            UploadProgress: function(up, file) {
                element.progress('upMode2', file.percent+'%');
            },
            FileUploaded: function(up, file, info) {
                if (info.status === 200) {
                    fileCommon.fileName = file.name;
                    fileCommon.fileUrl = get_uploaded_object_name(file.name);
                    fileCommon.size = plupload.formatSize(file.size);
                    $('.suolue').attr('src',IMAGE_SERVER + fileCommon.fileUrl);
                    $('.suolue').show();
                    $('.suolue-icon').hide();
                }
                else {
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
            mime_types : [
                { title : "images", extensions : "gif,GIF,jpg,JPG,jpeg,JPEG,png,PNG,bmp,BMP" },
                { title : "medias", extensions : "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb,mp4,SWF,FLV,MP3,WAV,WMA,WMV,MID,AVI,MPG,ASF,RM,RMVB,MP4" },
                { title : "sensitive", extensions : "txt,TXT" },
                { title : "sensitive", extensions : "zip,rar,gz" },
                { title : "model", extensions : "dwg,dxf,dae,tpl,3ds,ifc,obj" }
            ],
            max_file_size : '1024mb', //最大只能上传400kb的文件
            prevent_duplicates : true //不允许选取重复文件
        },
        init: {
            PostInit: function() {
                document.getElementById('postfiles').onclick = function() {
                    set_upload_param(uploader2, '', false);
                    return false;
                };
            },
            FilesAdded: function(up, files) {
                plupload.each(files, function(file) {
                    var content = '';
                        content += '<li class="boxsizing">\n' +
                            '                <div class="remove-it">\n' +
                            '                    <i class="layui-icon layui-icon-close-fill " style=""></i>\n' +
                            '                </div>\n' +
                            '                <img src="/image/file_1.png" alt="">\n' +
                            '                <p class="over-hidden">' + file.name + '</p>\n' +
                            '                <div class="layui-progress ordinaryFile" lay-filter ='+ file.id +'>\n' +
                            '                    <div class="layui-progress-bar" lay-percent="0%"></div>\n' +
                            '                </div>\n' +
                            '            </li>'
                    $('.all-file-ul').prepend(content);$('.ordinaryFile').show();
                });
            },

            BeforeUpload: function(up, file) {
                set_upload_param(up, file.name, true);
            },
            UploadProgress: function(up, file) {
                element.progress(file.id, file.percent+'%');
            },
            FileUploaded: function(up, file, info) {
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
            },

            UploadComplete:function(uploader,files){
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
            },
            Error: function(up, err) {
                console.log(err.response);
            }

        }
    });
    uploader2.init();

    $('#file').change(function () {
        var array =['.pln','.skp','.dwg','.dxf','.dae','.gsm','.tpl','.3ds','.ifc','.obj','.mod'];
        var zip = new JSZip();
        var client = new OSS({
            region: "oss-cn-beijing",
            accessKeyId:'LTAIP4MyTAbONGJx',
            accessKeySecret: 'coCyCStZwTPbfu93a3Ax0WiVg3D4EW',
            bucket: "art1001-bim-5d"//用户oss仓库地址
        });

        var f = document.getElementById("file").files[0];
        var suffix = get_suffix(f.name);

        if(array.indexOf(suffix)===-1){
            layer.msg("请选择模型文件",{icon:5})
        }else{
            $('.model-name').html(f.name);
            $('.upModel').show();
            $('.model-icon').hide();
            $('.model').show();
            var fileName = g_dirname+random_string(10) + get_suffix(f.name);
            layer.load();
            zip.file(f.name, f, {type: 'blob'});
            zip.generateAsync({
                type: 'blob',
                compression: 'DEFLATE', //  force a compression for this file
                compressionOptions: { //  使用压缩等级，1-9级，1级压缩比最低，9级压缩比最高
                    level: 6
                }
            }).then(function(data) {  //promise对象中的数据只能在then方法中取到
                layer.closeAll('loading');
                var file = new File([data],fileName);
                client.multipartUpload(file.name, file,{
                    progress: function (p) {
                        var percent = Math.floor(p * 100) + '%';
                        element.progress('upModel',percent);
                    }
                }).then(function (result) {
                    fileModel.fileName = f.name;
                    fileModel.fileUrl = result.name;
                    fileModel.size = plupload.formatSize(file.size);
                }).catch(function (err) {
                    console.log(err);
                });
            });
        }

    });
});




/**
 * 确定上传模型文件
 */
$('.model-ok-btn').click(function () {
    if(JSON.stringify(fileCommon) === "{}"|| JSON.stringify(fileModel) === "{}"){
        layer.msg("请选择模型和缩略图!");
    } else{
        $.post('/file/uploadModel',{"projectId":projectId,"fileCommon":JSON.stringify(fileCommon),"fileModel":JSON.stringify(fileModel),"parentId":parentId},function (data) {
            if(data.result === 1){
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

