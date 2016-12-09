<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page errorPage="error.jsp" %>
<html>
<head>
    <title>Главная страница</title>
</head>
<body>
<H3>Добро пожаловать, ${user}!</H3><HR>
Вы вошли в систему как АДМИНИСТРАТОР<BR><BR>
<a href="login">На главную</a><BR>
<a href="showUsers">Список пользователей</a><BR>
<a href="register">Добавить пользователя</a><BR>
<a href="waitingOrders">Ожидающие заказы</a><BR>
<a href="openedOrders">Открытые заказы</a><BR>
<a href="expiredOrders">Просроченные заказы</a><BR>
<a href="blacklist">Черный список</a><BR>
<a href="searchBookAdmin">Управление книгами</a><BR>
<a href="adminSeminars">Управление семинарами</a><BR>
<BR><a href="logout">Выход</a><BR>
<c:if test="${requestScope['errorMessage'] != null}">
    <BR>Ошибка: ${errorMessage}
</c:if>
</body>
</html>
