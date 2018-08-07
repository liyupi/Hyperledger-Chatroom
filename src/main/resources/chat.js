let nickname;
let isLegalName = false;
const host = "localhost:5927";
while (!isLegalName) {
    nickname = prompt("请输入昵称");
    if (nickname) {
        $.ajax({
            url: "http://" + host + "/checkName",
            data: {nickname},
            async: false,
            success: function (data) {
                if (data == 0) {
                    isLegalName = true;
                } else {
                    alert("该昵称已被使用！");
                }
            }
        });
    } else {
        alert("昵称不能为空！");
    }
}
$("#wrapper").removeClass("hide");
let socket = new WebSocket("ws://" + host + "/" + nickname);

let $message = $("#message");

$message.on("keyup", function (e) {
    if (e.keyCode === 13 && e.ctrlKey) {
        sendMessage();
    }
});

socket.onopen = event => {
    getUserList();
};

socket.onmessage = event => {
    let messageType = event.data.substring(0, 2);
    if (messageType == "sm") {
        getUserList();
    }
    let othersMessage = "<div class='receive " + messageType
        + "'><p>" + new Date().format('yyyy-MM-dd hh:mm:ss') + "</p><p>"
        + event.data.substring(2) + "</p></div>";
    $("#messageWindow").append(othersMessage);
};

socket.onclose = event => {
    alert("您已退出聊天室，重新加入请刷新页面！")
}

socket.onerror = event => {
    alert("网络连接异常，请刷新页面重试！")
}

function getUserList() {
    $.ajax({
        url: "http://" + host + "/getUsers",
        success: function (data) {
            $("#userList").html("");
            for (let i = 0; i < data.length; i++) {
                $("#userList").append("<li>" + data[i] + "</li>");
            }
        }
    });
}

function sendMessage() {
    if (socket.readyState != socket.OPEN) {
        alert("网络连接中断，请刷新页面重试！");
        return;
    }
    let myMessage = "<div class='send'><p class='date'>"
        + new Date().format('yyyy-MM-dd hh:mm:ss')
        + "</p><p>" + $message.val() + "</p></div>"
    socket.send($message.val());
    $("#messageWindow").append(myMessage);
    $message.val("");
}

function getAllMessage() {
    if (socket.readyState != socket.OPEN) {
        alert("网络连接中断，请刷新页面重试！");
        return;
    }
    window.open("http://localhost:5927/getAllMessage");
}
// 日期格式化
Date.prototype.format = function (format) {
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(),    //day
        "h+": this.getHours(),   //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3),  //quarter
        "S": this.getMilliseconds() //millisecond
    }
    if (/(y+)/.test(format)) format = format.replace(RegExp.$1,
        (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o) if (new RegExp("(" + k + ")").test(format))
        format = format.replace(RegExp.$1,
            RegExp.$1.length == 1 ? o[k] :
                ("00" + o[k]).substr(("" + o[k]).length));
    return format;
}