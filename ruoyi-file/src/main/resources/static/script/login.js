layui.use(['form', 'layer', 'jquery'], function () {
    //定义变量
    var $ = layui.jquery;
    var form = layui.form;
    var layer = layui.layer;
    var basePath = $("#basePath").val();

    //监听提交
    form.on('submit(login)', function (data) {
        submitLoginForm();
        return false;
    });

    function submitLoginForm() {
        $("#loginBtn").text("登录中...").attr("disabled", "disabled").addClass("layui-disabled");
        $.ajax({
            type: "post",
            url: basePath + '/login',
            data: {
                'username': $("#userName").val(),
                'password':  $("#userPass").val()
                // 'authcode':
            },
            success: function (data) {
                if (data.code == 200) {
                    window.location.href = basePath + data.data;
                } else {
                    layer.alert(data.msgs);
                    $("#loginBtn").text("登录").removeAttr("disabled").removeClass("layui-disabled");
                }
            }
        });
    }
});
