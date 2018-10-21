<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta charset="UTF-8">
    <title>Preview</title>
</head>
<body>
    <form action="/editor/post">
        <input type="hidden" name="username" value="<%= request.getParameter("username") %>" /> 
        <input type="hidden" name="postid" value="<%= request.getParameter("postid") %>" /> 
        <input type="hidden" name="title" value="<%= request.getParameter("title") %>" /> 
        <input type="hidden" name="body" value="<%= request.getParameter("body") %>" /> 
        <div>
            <button type="submit" name="action" value="open">Close Preview</button>
        </div>
    <div><h1> <%= request.getAttribute("title_html") %> </h1></div>
    <div><%= request.getAttribute("body_html") %></div>
    </form>
</body>
</html>
