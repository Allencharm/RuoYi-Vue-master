var menu = [], liIndex, curNav, delMenu, layId, element;
var changeRefreshStr = window.sessionStorage.getItem("changeRefresh");
var openTabNum = 10;
var tabFilter = "bodyTab";
var tabIdIndex = 0;

$(document).ready(function () {
    loadConfig();
    loadEvent();
});

//加载配置信息
function loadConfig() {
    layui.define(["element", "jquery"], function () {
        element = layui.element;
    });
}

//打开新的窗口
function menuTabAdd(thisObj) {
    if (window.sessionStorage.getItem("menu")) {
        menu = JSON.parse(window.sessionStorage.getItem("menu"));
    }
    if (thisObj.attr("target") == "_blank") {
        window.open(thisObj.attr("data-url"));
    } else if (thisObj.attr("data-url") != undefined) {
        var title = '';
        title += '<i class="layui-icon">' + thisObj.find("i.layui-icon").attr("data-icon") + '</i>';
        //已打开的窗口中不存在
        if (hasTab(thisObj.find("cite").text()) == -1 && thisObj.siblings("dl.layui-nav-child").length == 0) {
            if ($(".layui-tab-title.top_tab li").length == openTabNum) {
                layer.msg('只能同时打开' + openTabNum + '个选项卡哦。不然系统会卡的！');
                return;
            }
            tabIdIndex++;
            title += '<cite>' + thisObj.find("cite").text() + '</cite>';
            title += '<i class="layui-icon layui-unselect layui-tab-close" data-id="' + tabIdIndex + '">&#x1006;</i>';
            element.tabAdd(tabFilter, {
                title: title,
                content: "<iframe src='" + thisObj.attr("data-url") + "' data-id='" + tabIdIndex + "'></frame>",
                id: new Date().getTime()
            })
            //当前窗口内容
            var curmenu = {
                "icon": thisObj.find("i.layui-icon").attr("data-icon"),
                "title": thisObj.find("cite").text(),
                "href": thisObj.attr("data-url"),
                "layId": new Date().getTime()
            }
            menu.push(curmenu);
            //打开的窗口
            window.sessionStorage.setItem("menu", JSON.stringify(menu));
            //当前的窗口
            window.sessionStorage.setItem("curmenu", JSON.stringify(curmenu));
            element.tabChange(tabFilter, getLayId(thisObj.find("cite").text()));
            tabMove();
        } else {
            //当前窗口内容
            var curmenu = {
                "icon": thisObj.find("i.layui-icon").attr("data-icon"),
                "title": thisObj.find("cite").text(),
                "href": thisObj.attr("data-url")
            }
            changeRegresh(thisObj.parent('.layui-nav-item').index());
            //当前的窗口
            window.sessionStorage.setItem("curmenu", JSON.stringify(curmenu));
            element.tabChange(tabFilter, getLayId(thisObj.find("cite").text()));
            tabMove();
        }
    }
}


function btnTabAdd(thisObj) {
    if (window.sessionStorage.getItem("menu")) {
        menu = JSON.parse(window.sessionStorage.getItem("menu"));
    }
    var title = '';
    title += '<i class="layui-icon">' + thisObj.find("i.layui-icon").attr("data-icon") + '</i>';
    //已打开的窗口中不存在
    if (hasTab(thisObj.find("cite").text()) == -1) {
        if ($(".layui-tab-title.top_tab li").length == openTabNum) {
            layer.msg('只能同时打开' + openTabNum + '个选项卡哦。不然系统会卡的！');
            return;
        }
        tabIdIndex++;
        title += '<cite>' + thisObj.find("cite").text() + '</cite>';
        title += '<i class="layui-icon layui-unselect layui-tab-close" data-id="' + tabIdIndex + '">&#x1006;</i>';
        element.tabAdd(tabFilter, {
            title: title,
            content: "<iframe src='" + thisObj.attr("data-url") + "' data-id='" + tabIdIndex + "'></frame>",
            id: new Date().getTime()
        });
        element.init();
        //当前窗口内容
        var curmenu = {
            "icon": thisObj.find("i.layui-icon").attr("data-icon"),
            "title": thisObj.find("cite").text(),
            "href": thisObj.attr("data-url"),
            "layId": new Date().getTime()
        }
        menu.push(curmenu);
        //打开的窗口
        window.sessionStorage.setItem("menu", JSON.stringify(menu));
        //当前的窗口
        window.sessionStorage.setItem("curmenu", JSON.stringify(curmenu));
        element.tabChange(tabFilter, getLayId(thisObj.find("cite").text()));
        tabMove();
    } else {
        //当前窗口内容
        var curmenu = {
            "icon": thisObj.find("i.layui-icon").attr("data-icon"),
            "title": thisObj.find("cite").text(),
            "href": thisObj.attr("data-url")
        }
        changeRegresh(thisObj.parent('.layui-nav-item').index());
        //当前的窗口
        window.sessionStorage.setItem("curmenu", JSON.stringify(curmenu));
        element.tabChange(tabFilter, getLayId(thisObj.find("cite").text()));
        tabMove();
    }
}


function btnTabAdd1(thisObj) {
    tabIdIndex++;
    var title = '';
    title += '<i class="layui-icon">' + thisObj.find("i.layui-icon").attr("data-icon") + '</i>';
    title += '<cite>' + thisObj.find("cite").text() + '</cite>';
    title += '<i class="layui-icon layui-unselect layui-tab-close" data-id="' + tabIdIndex + '">&#x1006;</i>';
    element.tabAdd(tabFilter, {
        title: title,
        content: "<iframe src='" + thisObj.attr("data-url") + "' data-id='" + tabIdIndex + "'></frame>",
        id: new Date().getTime()
    });
    element.tabChange(tabFilter, getLayId(thisObj.find("cite").text()));
    tabMove();
}


//是否点击窗口切换刷新页面
function changeRegresh(index) {
    if (changeRefreshStr == "true") {
        $(".clildFrame .layui-tab-item").eq(index).find("iframe")[0].contentWindow.location.reload();
    }
}

//通过title获取lay-id
function getLayId(title) {
    $(".layui-tab-title.top_tab li").each(function () {
        if ($(this).find("cite").text() == title) {
            layId = $(this).attr("lay-id");
        }
    })
    return layId;
}

//通过title判断tab是否存在
function hasTab(title) {
    var tabIndex = -1;
    $(".layui-tab-title.top_tab li").each(function () {
        if ($(this).find("cite").text() == title) {
            tabIndex = 1;
        }
    })
    return tabIndex;
}

//顶部窗口移动
function tabMove() {
    $(window).on("resize", function (event) {
        var topTabsBoxWidth = $("#top_tabs_box").width();
        var topTabs = $("#top_tabs");
        var topTabsWidth = $("#top_tabs").width();
        var tabLi = topTabs.find("li.layui-this");
        var top_tabs = document.getElementById("top_tabs");
        var event = event || window.event;
        if (topTabsWidth > topTabsBoxWidth) {
            if (tabLi.position().left > topTabsBoxWidth || tabLi.position().left + topTabsBoxWidth > topTabsWidth) {
                topTabs.css("left", topTabsBoxWidth - topTabsWidth);
            } else {
                topTabs.css("left", -tabLi.position().left);
            }
            //拖动效果
            var flag = false;
            var cur = {
                x: 0,
                y: 0
            }

            var nx, dx, x;

            function down() {
                flag = true;
                var touch;
                if (event.touches) {
                    touch = event.touches[0];
                } else {
                    touch = event;
                }
                cur.x = touch.clientX;
                dx = top_tabs.offsetLeft;
            }

            function move() {
                if (flag) {
                    window.getSelection ? window.getSelection().removeAllRanges() : document.selection.empty();
                    var touch;
                    if (event.touches) {
                        touch = event.touches[0];
                    } else {
                        touch = event;
                    }
                    nx = touch.clientX - cur.x;
                    x = dx + nx;
                    if (x > 0) {
                        x = 0;
                    } else {
                        if (x < topTabsBoxWidth - topTabsWidth) {
                            x = topTabsBoxWidth - topTabsWidth;
                        } else {
                            x = dx + nx;
                        }
                    }
                    top_tabs.style.left = x + "px";
                    //阻止页面的滑动默认事件
                    document.addEventListener("touchmove", function () {
                        event.preventDefault();
                    }, false);
                }
            }

            //鼠标释放时候的函数
            function end() {
                flag = false;
            }

            //pc端拖动效果
            topTabs.on("mousedown", down);
            topTabs.on("mousemove", move);
            $(document).on("mouseup", end);
            //移动端拖动效果
            topTabs.on("touchstart", down);
            topTabs.on("touchmove", move);
            topTabs.on("touchend", end);
        } else {
            //移除pc端拖动效果
            topTabs.off("mousedown", down);
            topTabs.off("mousemove", move);
            topTabs.off("mouseup", end);
            //移除移动端拖动效果
            topTabs.off("touchstart", down);
            topTabs.off("touchmove", move);
            topTabs.off("touchend", end);
            topTabs.removeAttr("style");
            return false;
        }
    }).resize();
}

//注册事件
function loadEvent() {

    //切换后获取当前窗口的内容
    $("body").on("click", ".top_tab li", function () {
        var curmenu = '';
        menu = JSON.parse(window.sessionStorage.getItem("menu"));
        if (window.sessionStorage.getItem("menu")) {
            curmenu = menu[$(this).index() - 1];
        }
        if ($(this).index() == 0) {
            window.sessionStorage.setItem("curmenu", '');
        } else {
            window.sessionStorage.setItem("curmenu", JSON.stringify(curmenu));
            if (window.sessionStorage.getItem("curmenu") == "undefined") {
                //如果删除的不是当前选中的tab,则将curmenu设置成当前选中的tab
                if (curNav != JSON.stringify(delMenu)) {
                    window.sessionStorage.setItem("curmenu", curNav);
                } else {
                    window.sessionStorage.setItem("curmenu", JSON.stringify(menu[liIndex - 1]));
                }
            }
        }
        element.tabChange(tabFilter, $(this).attr("lay-id")).init();
        changeRegresh($(this).index());
        setTimeout(function () {
            tabMove();
        }, 100);
    });

    //删除tab
    $("body").on("click", ".top_tab li i.layui-tab-close", function () {
        //删除tab后重置session中的menu和curmenu
        liIndex = $(this).parent("li").index();
        menu = JSON.parse(window.sessionStorage.getItem("menu"));
        if (menu != null) {
            //获取被删除元素
            delMenu = menu[liIndex - 1];
            var curmenu = window.sessionStorage.getItem("curmenu") == "undefined" ? undefined : window.sessionStorage.getItem("curmenu") == "" ? '' : JSON.parse(window.sessionStorage.getItem("curmenu"));
            if (JSON.stringify(curmenu) != JSON.stringify(menu[liIndex - 1])) {  //如果删除的不是当前选中的tab
                // window.sessionStorage.setItem("curmenu",JSON.stringify(curmenu));
                curNav = JSON.stringify(curmenu);
            } else {
                if ($(this).parent("li").length > liIndex) {
                    window.sessionStorage.setItem("curmenu", curmenu);
                    curNav = curmenu;
                } else {
                    window.sessionStorage.setItem("curmenu", JSON.stringify(menu[liIndex - 1]));
                    curNav = JSON.stringify(menu[liIndex - 1]);
                }
            }
            menu.splice((liIndex - 1), 1);
            window.sessionStorage.setItem("menu", JSON.stringify(menu));
        }
        element.tabDelete("bodyTab", $(this).parent("li").attr("lay-id")).init();
        tabMove();
    });

    //刷新当前
    $(".refresh").on("click", function () {
        //此处添加禁止连续点击刷新一是为了降低服务器压力
        if ($(this).hasClass("refreshThis")) {
            $(this).removeClass("refreshThis");
            $(".clildFrame .layui-tab-item.layui-show").find("iframe")[0].contentWindow.location.reload();
            setTimeout(function () {
                $(".refresh").addClass("refreshThis");
            }, 2000)
        } else {
            layer.msg("您点击的速度超过了服务器的响应速度，还是等两秒再刷新吧！");
        }
    })

    //关闭其他
    $(".closePageOther").on("click", function () {
        if ($("#top_tabs li").length > 2 && $("#top_tabs li.layui-this cite").text() != "首页") {
            menu = JSON.parse(window.sessionStorage.getItem("menu"));
            $("#top_tabs li").each(function () {
                if ($(this).attr("lay-id") != '' && !$(this).hasClass("layui-this")) {
                    element.tabDelete("bodyTab", $(this).attr("lay-id")).init();
                    //此处将当前窗口重新获取放入session，避免一个个删除来回循环造成的不必要工作量
                    for (var i = 0; i < menu.length; i++) {
                        if ($("#top_tabs li.layui-this cite").text() == menu[i].title) {
                            menu.splice(0, menu.length, menu[i]);
                            window.sessionStorage.setItem("menu", JSON.stringify(menu));
                        }
                    }
                }
            })
        } else if ($("#top_tabs li.layui-this cite").text() == "首页" && $("#top_tabs li").length > 1) {
            $("#top_tabs li").each(function () {
                if ($(this).attr("lay-id") != '' && !$(this).hasClass("layui-this")) {
                    element.tabDelete("bodyTab", $(this).attr("lay-id")).init();
                    window.sessionStorage.removeItem("menu");
                    menu = [];
                    window.sessionStorage.removeItem("curmenu");
                }
            })
        }
        tabMove();
    });

    //关闭全部
    $(".closePageAll").on("click", function () {
        if ($("#top_tabs li").length > 1) {
            $("#top_tabs li").each(function () {
                if ($(this).attr("lay-id") != '') {
                    element.tabDelete("bodyTab", $(this).attr("lay-id")).init();
                    window.sessionStorage.removeItem("menu");
                    menu = [];
                    window.sessionStorage.removeItem("curmenu");
                }
            })
        } else {
            layer.msg("没有可以关闭的窗口了@_@");
        }
        //渲染顶部窗口
        tabMove();
    });

}




