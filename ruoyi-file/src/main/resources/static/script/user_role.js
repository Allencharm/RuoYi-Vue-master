var basePath = $("#basePath").val();
var table;
$(document).ready(function () {
    //加载菜单
    loadRoleListData();
    //注册监听事件
    loadRoleEvent();
});

function loadRoleEvent() {
    $("#btnQuery").on("click", function () {
        loadRoleListData();
    });

    //注册监听事件
    $("#userRoleBtn").on("click", function () {
        submitSaveUserRoleForm();
    });

    //注册监听事件
    $("#backRoleBtn").on("click", function () {
        //关闭弹出层
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });

}

function loadRoleListData() {
    layer.load(2);
    layui.use('table', function () {
        table = layui.table;
        table.render({
            elem: '#dataList',
            id: 'roleId',
            height: 471,
            type: 'post',
            url: basePath + '/sys/role/select',
            where: {"roleName": $("#roleName").val()},
            cols: [[
                {type: 'checkbox'},
                {field: 'roleName', title: '角色名称'},
                {field: 'createTime', title: '创建时间'}
            ]],
            response: {
                statusName: 'code',
                statusCode: 200,
                countName: 'count',
                msgName: 'msgs',
                dataName: 'data'
            },
            page: true,
            done: function (res, curr, count) {
                var data = res.data;
                if (data) {
                    for (var i = 0; i < data.length; i++) {
                        var index = data[i].LAY_TABLE_INDEX;
                        var roleIdTemp = data[i].roleId;
                        var roleIdCurr = $("#roleId").val();
                        if (roleIdTemp == roleIdCurr) {
                            var tableDiv = $(".layui-table-body.layui-table-main");
                            //选中单击行
                            var checkCell = tableDiv.find("tr[data-index=" + index + "]").find(checkBoxStr);
                            if (checkCell.length > 0) {
                                checkCell.click();
                            }
                            getRoleMenu(roleIdCurr);
                        }
                    }
                }
                layer.closeAll('loading');
            }
        });
    });
}

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

var checkBoxStr = "td div.laytable-cell-checkbox div.layui-form-checkbox";

//单击行勾选checkbox事件
$(document).on("click", ".layui-table-main tbody tr", function () {
    console.log($(this)[0].children[1].innerText);
    var index = $(this).attr('data-index');
    var tableBox = $(this).parents('.layui-table-box');
    var tableDiv = tableBox.find(".layui-table-body.layui-table-main");
    //获取已选中列并取消选中
    var trs = tableDiv.find(".layui-unselect.layui-form-checkbox.layui-form-checked").parent().parent().parent();
    for (var i = 0; i < trs.length; i++) {
        var trIndex = $(trs[i]).attr("data-index");
        if (trIndex != index) {
            //点击不是同一行
            var checkCell = tableDiv.find("tr[data-index=" + trIndex + "]").find(checkBoxStr);
            if (checkCell.length > 0) {
                checkCell.click();
            }
        } else {
            //点击是同一行
            var checkCell = tableDiv.find("tr[data-index=" + trIndex + "]").find(checkBoxStr);
            if (checkCell.length > 0) {
                checkCell.click();
            }
        }
    }
    //选中单击行
    var checkCell = tableDiv.find("tr[data-index=" + index + "]").find(checkBoxStr);
    if (checkCell.length > 0) {
        checkCell.click();
    }
    var checkStatus = table.checkStatus('roleId');
    if (checkStatus) {
        console.log(checkStatus.data[0].roleId);
        getRoleMenu(checkStatus.data[0].roleId);
    }
});

$(document).on("click", checkBoxStr, function (e) {
    e.stopPropagation();
});


function getRoleMenu(roleId) {
    //触发请求菜单数据
    $.ajax({
        type: "get",
        url: basePath + '/sys/menu/mytree?roleId=' + roleId,
        sync: false,
        success: function (data) {
            $.fn.zTree.init($("#menuTree"), zTreeSetting, data.data);
            var treeObj = $.fn.zTree.getZTreeObj("menuTree");
            for (var i = 0; i < data.data.length; i++) {
                var disabledNode = treeObj.getNodeByParam("id", data.data[i].id);
                treeObj.setChkDisabled(disabledNode, true);
            }
            treeObj.expandAll(true);
        }
    });
}


function submitSaveUserRoleForm() {
    var roleId = table.checkStatus('roleId').data[0].roleId;
    console.log(roleId);
    if (null != roleId) {
        $.ajax({
            type: "post",
            url: basePath + '/sys/user/config',
            data: {
                'userId': $("#userId").val(),
                'roleId': roleId
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
    } else {
        layer.alert("请选择一个角色", {
            skin: 'layui-layer-molv'
        });
    }
}



