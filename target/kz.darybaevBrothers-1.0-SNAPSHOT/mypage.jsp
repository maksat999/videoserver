<%--
  Created by IntelliJ IDEA.
  User: Максат
  Date: 03.02.2016
  Time: 19:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Brothers.kz</title>
</head>
<body>

<h1>Video Hosting Server ${name}</h1>

<form action="/" enctype="multipart/form-data" method="post" >
    <p><input type="file" name="videofile">
     <input type="submit" value="Отправить"></p>
</form>
</body>
</html>
