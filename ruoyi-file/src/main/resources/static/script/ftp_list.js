var basePath = $("#basePath").val();

var element;

$(document).ready(function () {

    layui.use('element', function () {
        element = layui.element;
    });

    loadFtpListData();

    //注册监听事件
    loadRoleEvent();

});

function loadRoleEvent() {
    $("#btnQuery").on("click", function () {
        loadFtpListData();
    });

    $("#btnAddPage").on("click", function () {
        openSaveFtpPage();
    });

    //点击菜单项
    $("body").on("click", ".connect", function () {
        btnTabAdd1($(this));
    });
}


function loadFtpListData() {
    layer.load(2);
    layui.use('table', function () {
        var table = layui.table;
        table.render({
            elem: '#dataList',
            height: 500,
            type: 'post',
            url: basePath + '/rtb/ftp/select',
            where: {"ftpName": $("#ftpName").val()},
            cols: [[
                {field: 'ftpName', title: 'FTP名称', width: '10%'},
                {field: 'ftpUser', title: 'FTP用户', width: '10%'},
                {field: 'ftpHost', title: 'FTP主机', width: '10%'},
                {field: 'ftpPort', title: 'FTP端口', width: '10%'},
                {
                    title: 'FTP协议',
                    width: '10%',
                    templet: function (data) {
                        if (data.ftpType == 1) {
                            return "ftp"
                        } else if (data.ftpType == 2) {
                            return "ftps";
                        } else {
                            return "sftp";
                        }
                    }
                },
                {
                    title: 'FTP模式',
                    width: '10%',
                    templet: function (data) {
                        if (data.ftpMode == 1) {
                            return "PORT主动方式"
                        } else {
                            return "PASV被动方式";
                        }
                    }
                },
                {
                    title: '创建时间',
                    width: '10%',
                    templet: function (data) {
                        return formatDate(new Date(data.createTime));
                    }
                },
                {
                    title: '操作',
                    align: "center",
                    width: '30%',
                    templet: function (data) {
                        var html="";
                        html += '<a class="layui-btn layui-btn-xs layui-btn-warm" href="javascript:openEditFtpPage(\'' + data.ftpId + '\')">编辑</a>';
                        html += '<a class="layui-btn layui-btn-xs layui-btn-danger" href="javascript:delFtp(\'' + data.ftpId + '\')">删除</a>';
                        html += '<a class="layui-btn layui-btn-xs layui-btn-normal" href="javascript:openLookFtpPage(\'' + data.ftpId + '\')">详情</a>';
                        html += '<a class="layui-btn layui-btn-xs " href="/rtb/ftp/file?ftpId=' + data.ftpId + '&ftpType='+data.ftpType+'">连接</a>';
                        // html += '<a class="connect layui-btn layui-btn-xs layui-btn-danger" data-url="/rtb/ftp/file?ftpId=' + data.ftpId + '"><i class="layui-icon" data-icon="&#xe614;"></i><span><cite>测试</cite></span></a>';
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


function delFtp(id) {
    layer.confirm('确认删除该FTP连接信息？', {
        btn: ['确定', '取消']
    }, function () {
        $.ajax({
            type: "post",
            url: basePath + '/rtb/ftp/delete',
            data: {'ftpId': id},
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

function openSaveFtpPage() {
    layer.open({
        title: "添加FTP",
        type: 2,
        content: basePath + '/rtb/ftp/save',
        area: ['450px', '500px'],
        maxmin: false,
        closeBtn: 0,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}

function openEditFtpPage(id) {
    layer.open({
        title: "编辑FTP",
        type: 2,
        content: basePath + '/rtb/ftp/edit?ftpId=' + id,
        area: ['450px', '500px'],
        maxmin: false,
        closeBtn: 0,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}


function openLookFtpPage(id) {
    layer.open({
        title: "查看FTP",
        type: 2,
        content: basePath + '/rtb/ftp/look?ftpId=' + id,
        area: ['450px', '500px'],
        maxmin: false,
        closeBtn: 0,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}

