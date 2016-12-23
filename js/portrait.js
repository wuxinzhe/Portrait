/**
 * 知识产权声明:本文件自创建起,其内容的知识产权即归属于原作者,任何他人不可擅自复制或模仿.
 * 创建者: wu  创建时间: 2016/12/10
 * 类说明:
 * 更新记录：
 */
$(document).ready(function () {
    new PageInit().init();
});


function PageInit() {
    var api = null;
    var _this = this;
    this.init = function () {
        $("[name='upload']").on('click', this.portraitUpload)
    };

    this.portraitUpload = function () {
        var model = $.scojs_modal({
                title: '头像上传',
                content: template('portraitUpload'),
                onClose: refresh
            }
        );
        model.show();
        var fileUp = new FileUpload();
        var portrait = $('#fileUpload');
        var alert = $('#alert');
        fileUp.portrait(portrait, '/file/portrait', _this.getExtraData);
        portrait.on('change', _this.readURL);
        portrait.on('fileuploaderror', function (event, data, msg) {
            alert.removeClass('hidden').html(msg);
            fileUp.fileinput('disable');
        });
        portrait.on('fileclear', function (event) {
            alert.addClass('hidden').html();
        });
        portrait.on('fileloaded', function (event, file, previewId, index, reader) {
            alert.addClass('hidden').html();
        });
        portrait.on('fileuploaded', function (event, data) {
            if (!data.response.status) {
                alert.html(data.response.message).removeClass('hidden');
            }
        })
    };

    this.readURL = function () {
        var img = $('#cut-img');
        var input = $('#fileUpload');
        if (input[0].files && input[0].files[0]) {
            var reader = new FileReader();
            reader.readAsDataURL(input[0].files[0]);
            reader.onload = function (e) {
                img.removeAttr('src');
                img.attr('src', e.target.result);
                img.Jcrop({
                    setSelect: [20, 20, 200, 200],
                    handleSize: 10,
                    aspectRatio: 1,
                    onSelect: updateCords
                }, function () {
                    api = this;
                });
            };
            if (api != undefined) {
                api.destroy();
            }
        }
        function updateCords(obj) {
            $("#x").val(obj.x);
            $("#y").val(obj.y);
            $("#w").val(obj.w);
            $("#h").val(obj.h);
        }
    };

    this.getExtraData = function () {
        return {
            sw: $('.jcrop-holder').css('width'),
            sh: $('.jcrop-holder').css('height'),
            x: $('#x').val(),
            y: $('#y').val(),
            w: $('#w').val(),
            h: $('#h').val()
        }
    }
}

function refresh() {
    window.location.reload();
}

function FileUpload() {
    //start 这个是因为我使用了SpringSecurity框架，有csrf跨域提交防御，所需需要设置这个值,没用SpringSecurity的朋友去掉这个参数
    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");
    //end
    this.portrait = function (target, uploadUrl, data) {
        target.fileinput({
            language: 'zh', //设置语言
            maxFileSize: 2048,//文件最大容量
            uploadExtraData: data,//上传时除了文件以外的其他额外数据
            showPreview: false,//隐藏预览
            uploadAsync: true,//ajax同步
            dropZoneEnabled: false,//是否显示拖拽区域
            uploadUrl: uploadUrl, //上传的地址
            allowedFileExtensions: ['jpg'],//接收的文件后缀
            showUpload: true, //是否显示上传按钮
            showCaption: true,//是否显示标题
            browseClass: "btn btn-primary", //按钮样式
            previewFileIcon: "<i class='glyphicon glyphicon-king'></i>",
            ajaxSettings: {//这个是因为我使用了SpringSecurity框架，有csrf跨域提交防御，所需需要设置这个值,没用SpringSecurity的朋友去掉这个参数
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token);
                }
            }
        });
    }
}