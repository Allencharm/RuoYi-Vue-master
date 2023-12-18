var basePath = $("#basePath").val();

$(document).ready(function () {
    //加载菜单
    loadMenuListData();
    //注册监听事件
    loadMenuEvent();
});

function loadMenuEvent() {
    $("#btnQuery").on("click", function () {
        loadMenuListData();
    });

    $("#btnAddPage").on("click", function () {
        openSaveMenuPage();
    });
}

function loadMenuListData() {
    layer.load(2);
    layui.use('table', function () {
        var table = layui.table;
        table.render({
            elem: '#dataList',
            height: 500,
            type: 'post',
            url: basePath + '/sys/menu/select',
            where: {"menuName": $("#menuName").val()},
            cols: [[
                {field: 'menuName', title: '菜单名称'},
                {field: 'menuCode', title: '菜单编码'},
                {field: 'menuPath', title: '菜单路径'},
                {
                    title: '菜单图标',
                    templet: function (data) {
                        return "<i class=\"layui-icon\">" + data.menuIcon + "</i>";
                    }
                },
                {
                    title: '是否显示',
                    templet: function (data) {
                        if (data.menuShow == 'y') {
                            return "是";
                        } else {
                            return "否";
                        }
                    }
                },
                {
                    title: '菜单级别',
                    templet: function (data) {
                        if (data.menuPid) {
                            return "二级菜单";
                        } else {
                            return "一级菜单";
                        }
                        return '<i class="layui-icon">' + data.menuIcon + '</i>';
                    }
                },
                {field: 'createUser', title: '创建用户', sort: true},
                {
                    title: '创建时间',
                    templet: function (data) {
                        return formatDate(new Date(data.createTime));
                    }
                },
                {
                    title: '操作',
                    align: "center",
                    templet: function (data) {
                        var html = '<a class="layui-btn layui-btn-xs" href="javascript:openEditMenuPage(\'' + data.menuId + '\')">编辑</a>';
                        html += '<a class="layui-btn layui-btn-xs layui-btn-danger" href="javascript:delMenu(\'' + data.menuId + '\')">删除</a>';
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
            page: true,
            done: function () {
                layer.closeAll('loading');
            }
        });
    });
}


function delMenu(id) {
    layer.confirm('确认删除该菜单？', {
        btn: ['确定', '取消']
    }, function () {
        $.ajax({
            type: "post",
            url: basePath + '/sys/menu/delete',
            data: {'menuId': id},
            success: function (data) {
                layer.alert(data.msgs, {
                    skin: 'layui-layer-molv'
                    , closeBtn: 0
                }, function () {
                    layer.closeAll("iframe");
                    location.reload();
                });
            }
        });
    });
}

function openSaveMenuPage() {
    layer.open({
        title: "添加菜单",
        type: 2,
        content: basePath + '/sys/menu/save',
        area: ['450px', '450px'],
        maxmin: false,
        closeBtn: 0,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}

function openEditMenuPage(id) {
    layer.open({
        title: "编辑菜单",
        type: 2,
        content: basePath + '/sys/menu/edit?menuId=' + id,
        area: ['450px', '450px'],
        maxmin: false,
        closeBtn: 0,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}
