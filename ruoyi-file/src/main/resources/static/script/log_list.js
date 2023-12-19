var basePath = $("#basePath").val();

$(document).ready(function () {
    //加载菜单
    loadLogListData();
    //注册监听事件
    loadLogEvent();
});

function loadLogEvent() {
    $("#btnQuery").on("click", function () {
        loadLogListData();
    });
}

function loadLogListData() {
    layer.load(2);
    layui.use('table', function () {
        var table = layui.table;
        table.render({
            elem: '#dataList',
            height: 500,
            type: 'post',
            url: basePath + '/sys/log/select',
            loading: true,
            where: {"logName": $("#logName").val()},
            cols: [[
                {field: 'logName', title: '日志名称'},
                {field: 'logUser', title: '请求用户'},
                {field: 'logUrls', title: '请求路径'},
                {field: 'logAddr', title: '用户地址'},
                {
                    title: '创建时间',
                    templet: function (data) {
                        return formatDate(new Date(data.logTime));
                    }
                },
                {field: 'logSystem', title: '操作系统'},
                {field: 'logMethod', title: '请求方式'},
                {field: 'logBrowser', title: '浏览器'},
                {
                    title: '操作',
                    align: "center",
                    templet: function (data) {
                        return '<a class="layui-btn layui-btn-xs" href="javascript:openLookLogPage(\'' + data.logId + '\')">详情</a>';
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

function openLookLogPage(id) {
    layer.open({
        title: "日志详情",
        type: 2,
        content: basePath + '/sys/log/look?logId=' + id,
        area: ['800px', '600px'],
        maxmin: false,
        btn: ['关闭'],
        btnAlign: 'c',
        skin: 'layui-layer-molv',
        scrolling: 'no',
        no: function (index) {
            layer.close(index);
        }
    });
}
