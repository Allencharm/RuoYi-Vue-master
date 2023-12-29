var basePath = $("#basePath").val();
/*var ftpId = $("#ftpId").val();
var ftpType = $("#ftpType").val();*/
var form;
var table;
var lastPath;
var currPath;
var homePath;
var tableUrl;

/**初始化数据表格*/
layui.use(['form', 'table', 'upload'], function () {


    console.log("******")
    console.log( $("#showFile").val())
    console.log("******")
    /*switch (ftpType) {
        case '1':
            tableUrl = basePath + '/rtb/ftp/lsfile';
            break;
        case '2':
            break;
        case '3':
            tableUrl = basePath + '/rtb/sftp/lsfile';
            break;
        case '4':
            tableUrl = basePath + '/file/lsfile';
            break;
    }*/
    tableUrl = basePath + '/file/lslist';
    layer.load(2);
    table = layui.table;
    table.render({
        elem: '#dataList',
        id: 'fileList',
        type: 'post',
        url: tableUrl,
        where: {
            //"ftpId": ftpId,
            "showFile": $("#showFile").val()
        },
        height: 'full-120',
        cols: [[
            {
                title: '文件名称',
                width: '20%',
                templet: function (data) {
                    console.log(data);
                    /*var fileHtml = '';
                    if (data.fileType) {
                        fileHtml += '<span style="width: 25px">';
                        fileHtml += '<img src="' + basePath + '/image/folder.png" width="19" height="20"/>&nbsp;&nbsp;&nbsp;&nbsp;';
                        fileHtml += '</span>';
                        fileHtml += '<a href="javascript:listNextFile(\'' + data.filePath + '\')">' + data.fileName + '</a>';
                        return fileHtml;
                    } else {
                        var fileHtml = '<img src="' + basePath + '/image/file.png" width="20" height="20"/>&nbsp;&nbsp;&nbsp;&nbsp;';
                        fileHtml += '<span>' + data.fileName + '</span>';
                        return fileHtml;
                    }*/
                    var fileHtml = '';
                    data.filePath = data.filePath.replace(/\\/g,'/')
                    if (!data.fileHidden){
                        if (data.fileType) {
                            var fileHtml = '<img src="' + basePath + '/image/file.png" width="20" height="20"/>&nbsp;&nbsp;&nbsp;&nbsp;';
                            fileHtml += '<span>' + data.fileName + '</span>';
                            return fileHtml;
                        } else {
                            fileHtml += '<span style="width: 25px">';
                            fileHtml += '<img src="' + basePath + '/image/folder.png" width="19" height="20"/>&nbsp;&nbsp;&nbsp;&nbsp;';
                            fileHtml += '</span>';
                            fileHtml += '<a href="javascript:listNextFile(\'' + data.filePath + '\')">' + data.fileName + '</a>';
                            return fileHtml;
                        }
                    }


                }
            },
            {
                title: '文件大小',
                width: '10%',
                templet: function (data) {
                    if (!data.fileType) {
                        return "--";
                    } else {
                        return (data.fileSize / 1014).toFixed(1) + "KB";
                    }
                }
            },
            {field: 'accessTime', title: '访问时间', width: '20%'},
            {field: 'modifyTime', title: '修改时间', width: '20%'},
            {
                title: '操作',
                align: "center",
                width: '30%',
                templet: function (data) {
                    var html = '';
                    data.filePath = data.filePath.replace(/\\/g,'/')
                    html += '<a class="layui-btn layui-btn-xs" href="javascript:renameFile(\'' + data.fileName + '\')">重命名</a>';
                    html += '<a class="layui-btn layui-btn-xs layui-btn-normal" href="javascript:openCopyFtpPage(\'' + data.filePath + '\')">复制到</a>';
                    html += '<a class="layui-btn layui-btn-xs layui-btn-warm" href="javascript:openMoveFtpPage(\'' + data.filePath + '\')">移动到</a>';
                    html += '<a class="layui-btn layui-btn-xs layui-btn-danger" href="javascript:deleteFile(\'' + data.fileName + '\',\'' + data.fileType + '\')">删除</a>';
                    /*switch (ftpType) {
                        case '1':
                            break;
                        case '2':
                            break;
                        case '3':
                            if (!data.fileType) {
                                html += '<a class="layui-btn layui-btn-xs layui-btn-normal" href="/rtb/sftp/dodown?ftpId=' + ftpId + '&filePath=' + data.filePath + '&fileName=' + data.fileName + '">下载</a>';
                            }
                            break;
                    }*/
                    //html += '<a class="layui-btn layui-btn-xs layui-btn-normal" href="/file/dodown?filePath=' + data.filePath + '&fileName=' + data.fileName + '">下载</a>';
                    if (data.fileType) {
                        html += '<a class="layui-btn layui-btn-xs layui-btn-normal" href="/file/dodown?filePath=' + data.filePath + '&fileName=' + data.fileName + '">下载</a>';
                    }else{
                        html += '<div class="layui-btn layui-btn-xs layui-btn-normal" v-show="false" style="pointer-events: none; color: rgba(0,0,0,0); background: rgba(0,0,0,0);">禁止</div>'
                    }
                    return html;
                }
            }
        ]],
        response: {
            statusName: 'code',
            statusCode: 200,
            countName: 'count',
            msgName: 'msgs',
            dataName: 'data'
        },
        done: function (res) {
            loadDataSuccess(res);
        }
    });

});

function listNextFile(filePath) {
    layer.load(2);
    table.reload("fileList", {
        where: {
            //"ftpId": ftpId,
            "showFile": $("#showFile").val(),
            "filePath": filePath
        },
        done: function (res) {
            loadDataSuccess(res);
        }
    });
}

function loadDataSuccess(res, upload) {
    homePath = res.param.homePath;
    lastPath = res.param.lastPath;
    currPath = res.param.currPath;
    console.log('path01:' + homePath);
    console.log('path02:' + lastPath);
    console.log('path03:' + currPath);
    var html = '';
    currPath = currPath.replace(/\\/g,'/')
    homePath = homePath.replace(/\\/g,'/')
    lastPath = lastPath.replace(/\\/g,'/')
    if (currPath != homePath) {
        //当前是子目录
        html += '<a style="color:blue" href="javascript:listNextFile(\'' + homePath + '\')" >主目录</a> | ';
        html += '<a style="color:blue" href="javascript:listNextFile(\'' + lastPath + '\')" >返回上一级</a> > ';
        html += '<span style="color:slategray">' + currPath + '</span>';
    } else {
        //当前是根目录
        html += '<span style="color:slategray">主目录 > ' + currPath + '</span>';
    }
    $("#pathShow").html(html);
    layer.closeAll('loading');
    //防止出现横向滚动条
    $(".layui-table-box").css("width", "101%");
}


//多文件上传接口
function openUploadPage() {
    layer.open({
        title: "上传文件",
        type: 2,
        //content: basePath + '/rtb/ftp/pick?ftpId=' + ftpId + '&ftpType=' + ftpType + '&currPath=' + currPath,
        content: basePath + '/file/pick?currPath=' + currPath,
        area: ['400px', '300px'],
        maxmin: false,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}

function openMkdirPage() {
    var mkdirUrl;
    /*switch (ftpType) {
        case '1':
            break;
        case '2':
            break;
        case '3':
            mkdirUrl = basePath + '/rtb/sftp/mkpath';
            break;
    }*/
    mkdirUrl = basePath + '/rtb/sftp/mkpath';
    layer.prompt(
        {
            title: '输入文件夹名称',
            formType: 3,
            closeBtn: 0
        },
        function (text, index) {
            $.ajax({
                type: "post",
                url: mkdirUrl,
                data: {
                    //'ftpId': ftpId,
                    'filePath': currPath,
                    'fileName': text,
                },
                sync: false,
                success: function (data) {
                    if (data.code == 200) {
                        layer.close(index);
                        listNextFile(currPath);
                    } else {
                        layer.alert(data.msgs);
                        layer.close(index);
                    }
                }
            });
        }
    );
}


function showHiddenFile() {
    var showFile = $("#showFile").val();
    if (showFile == 'false') {
        $("#showFile").val('true');
        $("#showText").text("隐藏文件");
    } else {
        $("#showFile").val('false');
        $("#showText").html("显示文件");
    }
    layer.load(2);
    table.reload('fileList', {
        url: tableUrl,
        where: {
            //"ftpId": ftpId,
            "showFile": $("#showFile").val(),
            "filePath": currPath
        },
        done: function (res) {
            loadDataSuccess(res);
        }
    });
}

function renameFile(fileName) {
    var renameFileUrl;
    /*switch (ftpType) {
        case '1':
            renameFileUrl = basePath + '/rtb/ftp/rename';
            break;
        case '2':
            break;
        case '3':
            renameFileUrl = basePath + '/rtb/sftp/rename';
            break;
    }*/
    renameFileUrl = basePath + '/rtb/sftp/rename';
    layer.prompt(
        {
            title: '输入新的文件夹名称',
            formType: 3,
            value: fileName,
            closeBtn: 0
        },
        function (text, index) {
            $.ajax({
                type: "post",
                url: renameFileUrl,
                data: {
                    //'ftpId': ftpId,
                    'oldFilePath': currPath + fileName,
                    'newFilePath': currPath + text,
                    'currPath': currPath,
                    'fromFileName': fileName,
                    'toFileName': text
                },
                sync: false,
                success: function (data) {
                    if (data.code == 200) {
                        layer.close(index);
                        listNextFile(currPath);
                    } else {
                        layer.alert(data.msgs);
                        layer.close(index);
                    }
                }
            });
        }
    );
}

function deleteFile(fileName, fileType) {
    var deleteFileUrl;
    /*switch (ftpType) {
        case '1':
            break;
        case '2':
            break;
        case '3':
            deleteFileUrl = basePath + '/rtb/sftp/rmfile';
            break;
    }*/
    deleteFileUrl = basePath + '/rtb/sftp/rmfile';
    var index = layer.confirm('确认删除该项？', {
        btn: ['确定', '取消']
    }, function () {
        $.ajax({
            type: "post",
            url: deleteFileUrl,
            data: {
                //'ftpId': ftpId,
                'filePath': currPath + fileName,
                'fileType': fileType
            },
            sync: false,
            success: function (data) {
                layer.close(index);
                if (data.code == 200) {
                    listNextFile(currPath);
                } else {
                    layer.alert(data.msgs);
                }
            }
        });
    });
}


function openCopyFtpPage(filePath) {
    layer.open({
        title: "复制文件",
        type: 2,
        //content: basePath + '/rtb/ftp/copy?ftpId=' + ftpId + '&ftpType=' + ftpType + '&filePath=' + filePath,
        content: basePath + '/rtb/ftp/copy?&filePath=' + filePath,
        area: ['700px', '600px'],
        maxmin: false,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}


function openMoveFtpPage(filePath) {
    layer.open({
        title: "移动文件",
        type: 2,
        //content: basePath + '/rtb/ftp/move?ftpId=' + ftpId + '&ftpType=' + ftpType + '&filePath=' + filePath,
        content: basePath + '/rtb/ftp/move?filePath=' + filePath,
        area: ['700px', '600px'],
        maxmin: false,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}


