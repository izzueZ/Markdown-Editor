<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.ArrayList, java.util.HashMap, java.util.Date" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>List Post</title>
</head>
<body>
    <% ArrayList<Integer> ids = (ArrayList) request.getAttribute("ids");
       HashMap<Integer, String> titles = (HashMap) request.getAttribute("titles");
       HashMap<Integer, Date> creations = (HashMap) request.getAttribute("creations");
       HashMap<Integer, Date> modifications = (HashMap) request.getAttribute("modifications"); %>
    <div><h1>List Post</h1></div>
    <form method="GET" action="/editor/post">
        <input type="hidden" name="username" value="<%= request.getParameter("username") %>" /> 
        <input type="hidden" name="postid" value="-1" /> 
        <button type="submit" name="action" value="open">New Post</button>
    </form>
    <table>
        <tr>
            <th>Title</th>
            <th>Created</th>
            <th>Modified</th>
        </tr>
        <% if(!ids.isEmpty()) {
            for(int i = 0; i < ids.size(); i++) {
                int id = ids.get(i); %>
                <tr>
                    <td><%= titles.get(id) %></td>
                    <td><%= creations.get(id) %></td>
                    <td><%= modifications.get(id) %></td>
                    <td>
                        <form method="GET" action="/editor/post">
                            <input type="hidden" name="username" value="<%= request.getParameter("username") %>" /> 
                            <input type="hidden" name="postid" value="<%= id %>" /> 
                            <input type="submit" name="action" value="open" />
                        </form>
                    </td>
                    <td>
                        <form method="POST" action="/editor/post">
                            <input type="hidden" name="username" value="<%= request.getParameter("username") %>" /> 
                            <input type="hidden" name="postid" value="<%= id %>" /> 
                            <input type="submit" name="action" value="delete" />
                        </form>
                    </td>
                </tr>
        <%  }
        } %>
    </table>
</body>
</html>