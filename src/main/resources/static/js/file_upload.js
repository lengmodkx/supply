
var accessid= 'LTAIP4MyTAbONGJx';
var accesskey= 'coCyCStZwTPbfu93a3Ax0WiVg3D4EW';
var host = 'https://art1001-bim-5d.oss-cn-beijing.aliyuncs.com';

g_dirname = 'upload/'+projectId+"/";
g_object_name = '';
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
    browse_button : 'selectfiles',
    multi_selection: false,
    container: document.getElementById('container'),
    flash_swf_url : 'js/lib/plupload-2.1.2/js/Moxie.swf',
    silverlight_xap_url : 'js/lib/plupload-2.1.2/js/Moxie.xap',
    url : 'http://oss.aliyuncs.com',

    init: {
        FilesAdded: function(up, files) {
            $('.file-upload').removeClass("show-file-upload");
            plupload.each(files, function(file) {
                $('.fileList').append('<li class="boxsizing" style="width: 550px;height: 40px;background-color: #eee;padding: 4px 8px;line-height: 20px;font-size: 12px;margin: 2px auto;margin-top: 0"><div id="' + file.id + '">' + file.name + ' (' + plupload.formatSize(file.size) + ')<i class="layui-icon layui-icon-close" style="font-size: 16px; color: gray;float: right;cursor: pointer"></i>'
                    +'<div class="progress"><div class="progress-bar" style="width: 0%"></div></div>'
                    +'</div></li>');
                set_upload_param(up, file.name, true);
                $(".fujian-box").slideUp();
            });
        },
        UploadProgress: function(up, file) {
            var d = $('#'+file.id);
            var prog = d.find('.progress');
            var progBar = prog.find('.progress-bar');
            progBar.width(5*file.percent+'px');
            progBar.attr('aria-valuenow', file.percent);
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

uploader.init();

