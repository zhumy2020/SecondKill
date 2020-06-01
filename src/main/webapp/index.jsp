<%--
  Created by IntelliJ IDEA.
  User: zmy20
  Date: 2020/5/28
  Time: 16:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>上愁网秒杀页面</title>
    <script src="jquery/jquery-2.1.1.min.js" type="text/javascript"></script>
</head>
<body>
<form id="skForm" action="sk/doSecondKill" method="post">
    <input type="hidden" name="id" value="10001">
    <a href="#">点击1元秒杀AlienwareM15</a>
</form>
<br>
<form id="phoneMsg" action="sk/subCredNum" method="post">
    请输入手机号：<input type="text" name="phoneNum"><button id="subPhoneNum">获取验证码</button><br>
    请输入验证码：<input type="text" name="creditNum"><br>
    <input type="submit" value="提交">
</form>
<script type="text/javascript">
    $("a").click(function () {
        $.ajax({
           type:"post",
           url:$("#skForm").prop("action"),
           data:$("#skForm").serialize(),
           success: function (res) {
                if(res=="ok"){
                    alert("秒杀成功");
                }else {
                    alert(res);
                    $("a").prop("disabled",true);
                }
           }
        });
        return false;
    });
    $("#subPhoneNum").click(function () {
        $.ajax({
            type:"post",
            url:"sk/getPhoneMsg",
            data:$("#phoneMsg").serialize(),
            success: function (res) {
                if(res=="ok"){
                    alert("验证码已发送！");
                }else {
                    alert("验证码发送失败！");
                }
            }
        });
        return false;
    });

</script>

</body>
</html>
