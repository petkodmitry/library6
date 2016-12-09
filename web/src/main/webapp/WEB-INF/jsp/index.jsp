<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="s" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page errorPage="error.jsp" %>
<HTML>
<HEAD><TITLE>Library login page</TITLE></HEAD>
<BODY><H3>Введите логин и пароль для входа в библиотеку:</H3>
<HR>
<%--<FORM name="loginForm"--%>
<s:form id="loginForm"
        name="usersEntity"
        method="POST"
        action="login"
        modelAttribute="usersEntity">
    <%--<fieldset>
        <label for="login">Login</label>
        <s:input id="login" type="text" value="" maxlength="20" path="login"/><br/>
        <label for="psw">Password</label>
        <s:input id="psw" type="password" value="" maxlength="20" path="psw"/><br/>
        <input id="personButton" type="submit" value=""/>
    </fieldset>--%>



    <INPUT type="hidden" name="cmd" value="login">
    Логин:<BR>
    <INPUT type="text"
           name="login"
           title="Логин"
           value=""><BR>
    Пароль:<BR>
    <INPUT type="password"
           name="password"
           title="Пароль"
           value=""><BR><BR>
    <INPUT title="Войти" type="submit" value="Войти">
    <BUTTON style="position: relative; left: 25px" title="Зарегистрироваться в системе" formaction="register"
            type="submit">Регистрация
    </BUTTON>
</s:form>
<HR>
<c:if test="${requestScope['errorMessage'] != null}">
    Ошибка: ${errorMessage}
</c:if>
</BODY>
</HTML>
