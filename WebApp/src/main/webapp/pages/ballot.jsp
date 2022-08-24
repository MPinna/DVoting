<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="it.unipi.dsmt.DVoting.Candidates" %><%--
  Created by IntelliJ IDEA.
  User: yuri
  Date: 20/08/22
  Time: 10:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form style="
    height: 600px;
    width: 900px;
    background-color: bisque;
    border: double black;"
    action="<%=request.getContextPath()%>/PollingStationServlet" method="post" onsubmit="return confirm('close ballot?')">
    <fieldset>
        <legend> choose a candidate</legend>

        <%
            List<String> candidates=new ArrayList<>();
//            candidates.add("Giulio Andreotti");
//            candidates.add("Bettino Craxi");
            candidates= Candidates.getCandidates(request.getSession().getId());
            for (String candidate :candidates) {
        %>
        <input type="button" value="<%=candidate%>" onclick="document.getElementById('vote').setAttribute('value',this.value)">

        <%  }   %>

    </fieldset>
    <input type="text" value="" id="vote" name="vote">
    <input type="submit" value="SEND VOTE" id="send">


</form>
</body>
</html>
