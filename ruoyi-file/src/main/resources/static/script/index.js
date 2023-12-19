var basePath = $("#basePath").val();

$(document).ready(function () {
    //注册事件
    loadMenuInitEvent()
});


function loadMenuInitEvent() {

    //切换导航栏按钮点击事件
    $("#switchNav").on("click", function () {
        switchNav();
    });

    //菜单点击事件
    $(".navBar").on("click", function () {
        if ($('body').hasClass('nav-mini')) {
            $('body').removeClass('nav-mini');
        }
    });

    //点击菜单项
    $("body").on("click", ".layui-nav .layui-nav-item a:not('.mobileTopLevelMenus .layui-nav-item a')", function () {
        if ($(this).siblings().length == 0) {
            //如果不存在子级
            menuTabAdd($(this));
            //移动端点击菜单关闭菜单层
            $('body').removeClass('site-mobile');
        }
        $(this).parent("li").siblings().removeClass("layui-nav-itemed");
    });


    //系统提示
    $('*[lay-tips]').on('mouseenter', function () {
        var content = $(this).attr('lay-tips');
        this.index = layer.tips(content, this, {
            tips: [1, '#3A3D49']
        });
    }).on('mouseleave', function () {
        layer.close(this.index);
    });

    //清楚缓存
    $("#clearCache").on("click", function () {
        window.sessionStorage.removeItem("showNotice");
        layer.msg("缓存清除成功！");
    });
}

//折叠显示导航栏
function switchNav() {
    var sideNavExpand = $('body').hasClass('nav-mini');
    if (!sideNavExpand) {
        $('body').addClass('nav-mini');
    } else {
        $('body').removeClass('nav-mini');
    }
    $('.nav-mini .layui-side .layui-nav .layui-nav-item').hover(function () {
        var text = $(this).children("a:eq(0)").find("cite").text();
        if ($('body').hasClass('nav-mini') && document.body.clientWidth > 750) {
            layer.tips(text, this);
        }
    }, function () {
        layer.closeAll('tips');
    });
}

function openInfoPage() {
    layer.open({
        title: "我的资料",
        type: 2,
        content: basePath + '/sys/user/info',
        area: ['600px', '600px'],
        maxmin: false,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}

function openPassPage() {
    layer.open({
        title: "修改密码",
        type: 2,
        content: basePath + '/sys/user/pass',
        area: ['450px', '300px'],
        maxmin: false,
        closeBtn: 0,
        skin: 'layui-layer-molv',
        scrolling: 'no'
    });
}


//公告层
function showNotice() {
    var content = '<div style="padding: 20px; line-height: 22px; height：200px;' +
        'background-color: #393D49; color: #fff; font-weight: 300;">' +
        '暂无系统公告' +
        '</div>';
    layer.open({
        type: 1,
        title: "系统公告",
        area: '300px',
        shade: 0.8,
        btn: ['关闭'],
        moveType: 1,
        content: content,
        success: function (layero) {
            var btn = layero.find('.layui-layer-btn');
            btn.css('text-align', 'center');
            btn.on("click", function () {
                tipsShow();
            });
        },
        cancel: function () {
            tipsShow();
        }
    });
}

function tipsShow() {
    window.sessionStorage.setItem("showNotice", "true");
    if ($(window).width() > 432) {
        //如果页面宽度不足以显示顶部“系统公告”按钮，则不提示
        layer.tips('系统公告躲在了这里', '#userInfo', {
            tips: 3,
            time: 1000
        });
    }
}


//功能设定
function systemSetting() {
    layui.use(['form', 'jquery', "layer"], function () {
        var $ = layui.jquery;
        var layer = layui.layer;
        var form = layui.form;
        layer.open({
            title: "功能设定",
            area: ['500px', '350px'],
            type: "1",
            content: '<div class="functionSrtting_box" style="height: 280px">' +
            '<form class="layui-form">' +
            '<div class="layui-form-item" >' +
            '<label class="layui-form-label" style="text-align: center; width:30%">窗口切换刷新</label>' +
            '<div class="layui-input-block">' +
            '<input type="checkbox" name="changeRefresh" lay-skin="switch" lay-text="开|关">' +
            '<div class="layui-word-aux">开启后切换窗口刷新当前页面</div>' +
            '</div>' +
            '</div>' +
            '<div class="layui-form-item skinBtn" style="width:100%;margin: auto 0px;bottom: 10px;position:absolute;">' +
            '<a href="javascript:;" class="layui-btn layui-btn-sm layui-btn-normal" lay-submit="" lay-filter="settingSuccess">确定</a>' +
            '<a href="javascript:;" class="layui-btn layui-btn-sm layui-btn-primary" lay-submit="" lay-filter="noSetting">取消</a>' +
            '</div>' +
            '</form>' +
            '</div>',
            success: function (index, layero) {
                //如果之前设置过，则设置它的值
                $(".functionSrtting_box input[name=changeRefresh]").prop("checked", changeRefreshStr == "true" ? true : false);
                //设定
                form.on("submit(settingSuccess)", function (data) {
                    window.sessionStorage.setItem("changeRefresh", data.field.changeRefresh == "on" ? "true" : "false");
                    location.reload();
                    return false;
                });
                //取消设定
                form.on("submit(noSetting)", function () {
                    layer.closeAll("page");
                });
                form.render();
            }
        });
    });
}

//退出
function logout() {
    //询问框
    layer.confirm('确定要退出系统？', {
        btn: ['确定', '取消']
    }, function () {
        window.location.href = basePath + "/logout";
    });
}
