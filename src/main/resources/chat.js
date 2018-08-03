let nickname;
let isLegalName = false;
while (!isLegalName) {
    nickname = prompt("请输入昵称");
    if (nickname) {
        $.ajax({
            url: "http://localhost:5927/checkName",
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
let socket = new WebSocket("ws://localhost:5927/" + nickname);

let $message = $("#message");

$message.on("keyup", function (e) {
    if (e.keyCode === 13 && e.ctrlKey) {
        sendMessage();
    }
});

socket.onopen = event => {
    $.ajax({
        url: "http://localhost:5927/getUsers",
        success: function (data) {
            for (let i = 0; i < data.length; i++) {
                $("#userList").append("<li>" + data[i] + "</li>");
            }
        }
    });
};

function sendMessage() {
    let myMessage = "<div class='send'><p>" + new Date() + "</p><p>" + $message.val() + "</p></div>"
    $("#messageWindow").append(myMessage);
    $message.val("");
}