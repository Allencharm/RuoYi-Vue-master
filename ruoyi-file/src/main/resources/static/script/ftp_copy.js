var basePath = $("#basePath").val();
var ftpId = $("#ftpId").val();
var ftpType = $("#ftpType").val();
var filePath = $("#filePath").val();

$(document).ready(function () {

    loadFileTree();

    //注册监听事件
    $("#copyFileBtn").on("click", function () {
        runCopyFile();
    });

    //注册监听事件
    $("#backFileBtn").on("click", function () {
        //关闭弹出层
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });

});


var zTreeSetting = {
    check: {
        enable: false,
    },
    data: {
        simpleData: {
            enable: true
        }
    },
    view: {
        showLine: false
    },
    async: {
        enable: true, //启用异步加载
        dataType: "json",
        url: basePath + '/rtb/sftp/mytree?ftpId=' + ftpId,
        autoParam: ["id", "path"],
        dataFilter: filter
    },
    callback: {
        onClick: zTreeOnClick
    }
};

//过滤异步加载ztree时返回的数据
function filter(treeId, parentNode, childNodes) {
    return childNodes.data;
}

function zTreeOnClick(event, treeId, treeNode) {
    $("#pathText").html("当前复制到: " + treeNode.path);
    $("#copyPath").val(treeNode.path);
};

function loadFileTree() {
    layer.load(2);
    $.ajax({
        type: "get",
        url: basePath + '/rtb/sftp/mytree?ftpId=' + ftpId,
        sync: false,
        success: function (data) {
            $.fn.zTree.init($("#pathTree"), zTreeSetting, data.data);
            $.fn.zTree.getZTreeObj("pathTree").expandAll(false);
            layer.closeAll('loading');
        }
    });
}

function runCopyFile() {
    var copyUrl;
    switch (ftpType) {
        case '1':
            break;
        case '2':
            break;
        case '3':
            copyUrl = basePath + '/rtb/sftp/docopy';
            break;
    }
    $.ajax({
        type: "post",
        url: copyUrl,
        data: {
            'ftpId': ftpId,
            'filePath': filePath,
            'distPath': $("#copyPath").val()
        },
        sync: false,
        success: function (data) {
            layer.alert(data.msgs, {
                skin: 'layui-layer-molv',
                closeBtn: 0
            }, function () {
                //关闭弹出层
                var index = parent.layer.getFrameIndex(window.name);
                parent.layer.close(index);
                //刷新父页面
                window.parent.listNextFile(window.parent.currPath);
            });
        }
    });
}
