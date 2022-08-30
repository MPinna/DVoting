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

   %>

<form action = "<%=request.getContextPath()%>/Admin" method = "get" >
    <fieldset> <!-- login phase with private key upload -->
        <legend>Status: <%= votingStatus%></legend>
        <input type = "hidden" name = "action" id="action"/>
    </fieldset>
    <input type = "submit" value = "Suspend" onsubmit="this.getElementById('action').value=<%=AdminServlet.ActionSUSPEND%>" />
    <input type = "submit" value = "Resume" onsubmit="this.getElementById('action').value=<%=AdminServlet.ActionRESUME%>" />
    <input type = "submit" value = "Stop" onsubmit="this.getElementById('action').value=<%=AdminServlet.ActionSTOP%>" />
</form>

</body>
</html>
