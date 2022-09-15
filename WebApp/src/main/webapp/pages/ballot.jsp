<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="it.unipi.dsmt.DVoting.Candidates" %>
<%@ page import="it.unipi.dsmt.DVoting.AdminServlet" %>
<%@ page import="it.unipi.dsmt.DVoting.AccessServlet" %><%--
  Created by IntelliJ IDEA.
  User: yuri
  Date: 20/08/22
  Time: 10:14
  To change this template use File | Settings | File Templates.

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Ballot</title>
    <link href="<%=request.getContextPath()%>/css/ballot.css" rel="stylesheet" type="text/css">
</head>
<body>
<form
<%--        style="--%>
<%--    height: 600px;--%>
<%--    width: 900px;--%>
<%--    background-color: bisque;--%>
<%--    border: double black;"--%>
    action="<%=request.getContextPath()%>/Booth" method="post" onsubmit="return confirm('close ballot?')">
    <fieldset>
        <legend> Choose a candidate</legend>

        <%      // prevent access from unregistered users
            if(!AccessServlet.authenticateUser(request.getSession())) {
                request.getSession().invalidate();
                response.sendRedirect(request.getContextPath());
//            RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
//            requestDispatcher.forward(request, response);
            }
            else{
            List<String> candidates;
            candidates= Candidates.getCandidates(request.getSession().getId());
            if(candidates == null){
            %> impossible to retrieve candidates list <%
                return;
            }
            for (String candidate :candidates) {
        %>
        <input type="button" value="<%=candidate%>" onclick="document.getElementById('vote').value=this.value ">

        <%  }  } %>

    </fieldset>
    <input type="text" value="" id="vote" name="vote" >
    <input type="submit" value="SEND VOTE" id="send">


</form>
</body>
</html>
