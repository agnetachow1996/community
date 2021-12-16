$(function () {
    $("#sendBtn").click(send_letter);
    $("#deleteMsg").click(delete_msg);
});

function send_letter() {
    $("#sendModal").modal("hide");

    var toName = $("#recipient-name").val();
    var content = $("#message-text").val();
    $.post(
        //服务端URL
        CONTEXT_PATH + "letter/send",
        //向服务器端发送的数据
        {
            "toName": toName,
            "content": content
        },
        //成功函数
        function (data) {
        //$.parseJSON() 函数用于将符合标准格式的的JSON字符串转为与之对应的JavaScript对象。
            data = $.parseJSON(data);
            if (data.code === 0) {
                $("#hintBody").text("发送成功");
            } else {
                $("#hintBody").text(data.msg);
            }

            $("#hintModal").modal("show");
            setTimeout(function () {
                $("#hintModal").modal("hide");
                location.reload();
            }, 2000);
        }
    );
}

function delete_msg() {
    // TODO 删除数据
    var id = $("#letterId").attr("value");
    console.log(id);
    $.post(
        CONTEXT_PATH + "letter/delete",
        {
            "id": id
        },
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                $("#hintBody").text("删除成功");
            } else {
                $("#hintBody").text(data.msg);
            }

            $("#hintModal").modal("show");
            setTimeout(function () {
                $("#hintModal").modal("hide");
                location.reload();
            }, 2000);
        }
    );
    $(this).parents(".media").remove();
}