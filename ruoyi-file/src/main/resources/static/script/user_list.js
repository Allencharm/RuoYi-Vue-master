var basePath = $("#basePath").val();

$(document).ready(function () {
    //加载用户数据
    loadUserListData();
    //注册监听事件
    loadUserEvent();
});

function loadUserEvent() {
    $("#btnQuery").on("click", function () {
        loadUserListData();
    });

    $("#btnAddPage").on("click", function () {
        openSaveUserPage();
    });
}

function loadUserListData() {
    layer.load(2);
    layui.use('table', function () {
        var table = layui.table;
        table.render({
            elem: '#dataList',
            height: 500,
            type: 'post',
            url: basePath + '/sys/user/select',
            where: {"userName": $("#userName").val()},
            cols: [[
                {field: 'userName', title: '用户名称'},
                {field: 'userName', title: '邮箱地址'},
                {
                    title: '用户类型',
                    templet: function (data) {
                        if (data.userType == '0') {
                            return "系统用户";
                        } else {
                            return "普通用户";
                        }
                    }
                },
                {
                    title: '用户状态',
                    templet: function (data) {
                        if (data.userStatus == 'y') {
                            return "正常";
                        } else {
                            return "冻结";
                        }
                    }
                },
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
                        var html = '<a class="layui-btn layui-btn-xs" href="javascript:openEditUserPage(\'' + data.userId + '\')">编辑</a>';
                        html += '<a class="layui-btn layui-btn-xs layui-btn-danger" href="javascript:delUser(\'' + data.userId + '\')">删除</a>';
                        html += '<a class="layui-btn layui-btn-xs layui-btn-normal" href="javascript:openUserRolePage(\'' + data.userId + '\')">配置角色</a>';
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


function delUser(id) {
    layer.confirm('确认删除该用户？', {
        btn: ['确定', '取消']
    }, function () {
        $.ajax({
            type: "post",
            url: basePath + '/sys/user/delete',
            data: {'userId': id},
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

function openSaveUserPage() {
    layer.open({
        title: "添加用户",
        type: 2,
        content: basePath + '/sys/user/save',
        area: ['450px', '450px'],
        maxmin: false,
        closeBtn: 0,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}

function openEditUserPage(id) {
    layer.open({
        title: "编辑用户",
        type: 2,
        content: basePath + '/sys/user/edit?userId=' + id,
        area: ['450px', '450px'],
        maxmin: false,
        closeBtn: 0,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}


function openUserRolePage(id) {
    layer.open({
        title: "用户角色授权",
        type: 2,
        content: basePath + '/sys/user/role?userId=' + id,
        area: ['700px', '645px'],
        maxmin: false,
        closeBtn: 0,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}

