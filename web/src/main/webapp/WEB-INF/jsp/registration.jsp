<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page errorPage="error.jsp" %>
<html>
<head><title>Регистрация пользователя</title>
</head>
<body><H3>Заполните регистрационную форму:</H3>
<HR>
<%--<FORM name="registerForm"--%>
<s:form name="registerForm"
        method="POST"
        modelAttribute="regData"
        action="">
    <table border="0">
        <tr>
            <td align="right">Логин:</td>
            <td><INPUT type="text"
                       <%--name="newLogin"--%>
                       name="login"
                       title="Придумайте оригинальный логин"
                       value="${regData.getLogin()}"></td>
            <td>
                <form method="post" action="">
                    <BUTTON type="submit" formaction="register" title="Проверить доступность">Доступность</BUTTON>
                </form>
            </td>
        </tr>
        <tr style="height: 25px">
            <td></td>
            <td>${unavailableMessage}</td>
            <td></td>
        </tr>
        <tr>
            <td align="right">Имя:</td>
            <td><INPUT type="text"
                       <%--name="newName"--%>
                       name="firstName"
                       title="Имя"
                       value="${regData.getFirstName()}"></td>
            <td></td>
        </tr>
        <tr>
            <td align="right">Фамилия:</td>
            <td><INPUT type="text"
                       <%--name="newLastName"--%>
                       name="lastName"
                       title="Фамилия"
                       value="${regData.getLastName()}"></td>
            <td></td>
        </tr>
        <tr>
            <td align="right">Пароль:</td>
            <td><INPUT type="password"
                       <%--name="newPassword"--%>
                       name="password"
                       title="Пароль не менее 8 символов"
                       value=""></td>
            <td></td>
        </tr>
        <tr>
            <td align="right">Повторите пароль:</td>
            <td><INPUT type="password"
                       name="repeatPassword"
                       title="Повторите ввод пароля для проверки"
                       value=""></td>
            <td></td>
        </tr>
        <tr style="height: 25px">
            <td>
                <form method="post" action="">
                    <BUTTON tabindex="-1" type="submit" formaction="login" title="Назад">Назад</BUTTON>
                </form>
            </td>
            <td align="right"><INPUT type="submit" formaction="register" title="Зарегистрироваться"
                                     value="Зарегистрироваться"></td>
            <td></td>
        </tr>
    </table>
    <br>
    <%--</form>--%>
</s:form>

<HR>
<c:if test="${requestScope['errorMessage'] != null}">
    Ошибка: ${errorMessage}
</c:if>
</body>
</html>
