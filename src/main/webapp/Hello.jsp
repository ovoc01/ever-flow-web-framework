<%@ page import="java.util.LinkedList" %>
<%@ page import="testFramework.Client" %><%--
  Created by IntelliJ IDEA.
  User: rakotoharisoa
  Date: 04/04/2023
  Time: 10:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <ul>
        <%
            LinkedList<Client> clientLinkedList =(LinkedList<Client>)request.getAttribute("listClient");
            for (Client c: clientLinkedList)  {%>
                <li><%=c.getNom()%></li>
       <% } %>
    </ul>
</body>
</html>
