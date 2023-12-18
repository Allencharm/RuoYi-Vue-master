var basePath = $("#basePath").val();

$(document).ready(function () {
    //加载菜单
    loadRoleMenu();

    //注册监听事件
    $("#roleMenuBtn").on("click", function () {
        submitRoleMenuForm();
    });

    //注册监听事件
    $("#backRoleBtn").on("click", function () {
        //关闭弹出层
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });

});


var zTreeSetting = {
    check: {
        enable: true
    },
    data: {
        simpleData: {
            enable: true
        }
    }
};

function loadRoleMenu() {
    $.ajax({
        type: "get",
        url: basePath + '/sys/menu/mytree?roleId=' + $("#roleId").val(),
        sync: false,
        success: function (data) {
            $.fn.zTree.init($("#menuTree"), zTreeSetting, data.data);
            $.fn.zTree.getZTreeObj("menuTree").expandAll(true);
        }
    });
}

function submitRoleMenuForm() {
    var zTree = $.fn.zTree.getZTreeObj("menuTree");
    var nodes = zTree.getCheckedNodes(true);
    var authMenu = new Array();
    for (var i = 0; i < nodes.length; i++) {
        authMenu.push(nodes[i].id)
    }
    $.ajax({
        type: "post",
        url: basePath + '/sys/role/config',
        data: {
            "roleId": $("#roleId").val(),
            "roleMenu": authMenu.join(",")
        },
        success: function (data) {
            layer.alert(data.msgs, {
                skin: 'layui-layer-molv',
                closeBtn: 0
            }, function () {
                //关闭弹出层
                var index = parent.layer.getFrameIndex(window.name);
                parent.layer.close(index);
                //刷新父页面
                parent.location.reload();
            });
        }
    });
}
