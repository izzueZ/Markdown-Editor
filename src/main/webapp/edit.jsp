<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Edit Post</title>
    <link href="bootstrap.css" rel="stylesheet">
</head>
<body>

    <div class="container">
        <div class="row">
            <div class="col-sm-9 col-sm-offset-0 col-md-9 col-md-offset-2 main">
                <div><h1>Edit Post</h1></div>
                <br>
                <div><form action="/editor/post">
                        <input type="hidden" name="username" value="<%= request.getParameter("username") %>" /> 
                        <input type="hidden" name="postid" value="<%= request.getParameter("postid") %>" /> 
                        <div>
                            <button type="submit" formmethod="POST" name="action" value="save" class="btn btn-primary">Save</button>
                            <button type="submit" name="action" value="list" class="btn btn-primary">Close</button>
                            <button type="submit" name="action" value="preview" class="btn btn-primary">Preview</button>
                            <button type="submit" formmethod="POST" name="action" value="delete" class="btn btn-primary">Delete</button>
                        </div>
                        <br>
                        <div class="form-group">
                            <label for="title">Title</label>
                            <input type="text" id="title" name="title" class="form-control" value="<%= request.getAttribute("title") %>" />
                        </div>
                        <div class="form-group">
                            <label for="body">Body</label>
                            <!-- <input type="text" id="body" name="body" class="form-control" value="<%= request.getAttribute("body") %>" /> -->
                            <textarea class="form-control rounded-0" rows="10" id="body" name="body"><%= request.getAttribute("body") %></textarea>
                        </div>
                    </form>
                </div>
        </div>
    </div>
</body>
</html>
