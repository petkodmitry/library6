<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page errorPage="error.jsp" %>
<html>
<head>
    <title>Черный список</title>
</head>
<body>
<sec:authorize access="hasRole('true')">
    <%--Dear <strong>${user}</strong>, Welcome to Admin Page.--%>
<H3>Черный список</H3>
<HR>

<c:if test="${blackList != null && !blackList.isEmpty()}">
<form>
    <table border="1">
            <tr>
                <td>Пользователь</td>
                <td>Имя</td>
                <td>Фамилия</td>
                <td>Разблокировать</td>
            </tr>
        <c:forEach items="${blackList}" var="user">
            <tr>
                <td>${user.getLogin()}</td>
                <td>${user.getFirstName()}</td>
                <td>${user.getLastName()}</td>
                <td>
                    <a href="unblockUser?login=${user.getLogin()}">Разблокировать</a>
                </td>
            </tr>
        </c:forEach>
    </table>
</form>
</c:if>
</sec:authorize>

<BR><a href="login">На главную</a>
<BR><BR><c:if test="${requestScope['errorMessage'] != null}">
    Ошибка: ${errorMessage}
</c:if>

</body>
</html>
