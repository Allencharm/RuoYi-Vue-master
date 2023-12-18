var basePath = $("#basePath").val();

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

    $("#btnAddPage").on("click", function () {
        openSaveRolePage();
    });
}

function loadRoleListData() {
    layer.load(2);
    layui.use('table', function () {
        var table = layui.table;
        table.render({
            elem: '#dataList',
            height: 500,
            type: 'post',
            url: basePath + '/sys/role/select',
            where: {"roleName": $("#roleName").val()},
            cols: [[
                {field: 'roleName', title: '角色名称'},
                {
                    title: '是否启用',
                    templet: function (data) {
                        if (data.roleType == 'y') {
                            return "是";
                        } else {
                            return "否";
                        }
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
                        var html = '<a class="layui-btn layui-btn-xs" href="javascript:openEditRolePage(\'' + data.roleId + '\')">编辑</a>';
                        html += '<a class="layui-btn layui-btn-xs layui-btn-danger" href="javascript:delRole(\'' + data.roleId + '\')">删除</a>';
                        html += '<a class="layui-btn layui-btn-xs layui-btn-normal" href="javascript:openRoleMenuPage(\'' + data.roleId + '\')">配置菜单</a>';
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


function delRole(id) {
    layer.confirm('确认删除该角色？', {
        btn: ['确定', '取消']
    }, function () {
        $.ajax({
            type: "post",
            url: basePath + '/sys/role/delete',
            data: {'roleId': id},
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

function openSaveRolePage() {
    layer.open({
        title: "添加角色",
        type: 2,
        content: basePath + '/sys/role/save',
        area: ['450px', '300px'],
        maxmin: false,
        closeBtn: 0,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}

function openEditRolePage(id) {
    layer.open({
        title: "编辑角色",
        type: 2,
        content: basePath + '/sys/role/edit?roleId=' + id,
        area: ['450px', '300px'],
        maxmin: false,
        closeBtn: 0,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}


function openRoleMenuPage(id) {
    layer.open({
        title: "角色菜单授权",
        type: 2,
        content: basePath + '/sys/role/menu?roleId=' + id,
        area: ['500px', '580px'],
        maxmin: false,
        closeBtn: 0,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}

