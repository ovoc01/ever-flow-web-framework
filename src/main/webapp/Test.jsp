
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String message =(String) request.getAttribute("message");
%>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <h1><%=message%></h1>
</body>
</html>
