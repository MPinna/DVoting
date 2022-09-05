<%@ page import="it.unipi.dsmt.DVoting.AdminServlet" %>
<%--
  Created by IntelliJ IDEA.
  User: yuri
  Date: 30/08/22
  Time: 11:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Title</title>
</head>
<body> <%      // prevent access from unregistered users
    if(!AdminServlet.authenticateAdmin(request.getSession())) {
        request.getSession().invalidate();
        response.sendRedirect(request.getContextPath());
    }
    String votingStatus=AdminServlet.getVotingStatus(request.getSession());
    int turnout= AdminServlet.getTurnout(request.getSession());
   %>

<form action = "<%=request.getContextPath()%>/Admin" method = "get" >
    <fieldset>
        <legend>Status: <%= votingStatus%> Turnout: <%= turnout%></legend>
        <input type = "hidden" name = "action" id="action"/>
    </fieldset>
    <input type = "submit" value = "Suspend" onclick="document.getElementById('action').value='<%=AdminServlet.ActionSUSPEND%>'" />
    <input type = "submit" value = "Resume" onclick="document.getElementById('action').value='<%=AdminServlet.ActionRESUME%>'" />
    <input type = "submit" value = "Stop" onclick="document.getElementById('action').value='<%=AdminServlet.ActionSTOP%>'" />
    <input type = "submit" value = "Exit" onclick="document.getElementById('action').value='<%=AdminServlet.ActionEXIT%>'" />
</form>

</body>
</html>
