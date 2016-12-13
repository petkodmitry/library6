<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page errorPage="error.jsp" %>
<HTML>
<HEAD><TITLE><spring:message code="page.login.title"/></TITLE></HEAD>


<BODY><H3><spring:message code="page.login.body.head"/></H3>
<div style="float: left">
    <a href="login?locale=ru" style="padding: 5px">RU</a>
    <a href="login?locale=en" style="padding: 5px">EN</a>
</div><BR>
<HR>
<FORM name="loginForm"
      method="POST"
      action="login">
    <spring:message code="page.login.login"/><BR>
    <INPUT type="text"
           name="login"
           title="Логин"
           value=""><BR>
    <spring:message code="page.login.password"/><BR>
    <INPUT type="password"
           name="password"
           title="Пароль"
           value=""><BR><BR>
    <INPUT title="<spring:message code="page.login.enter"/>" type="submit"
           value="<spring:message code="page.login.enter"/>">
    <BUTTON style="position: relative; left: 25px" title="Зарегистрироваться в системе" formaction="register"
            type="submit"><spring:message code="page.login.registration"/>
    </BUTTON>
</form>

<HR>
<c:if test="${requestScope['errorMessage'] != null}">
    <spring:message code="message.error"/> ${errorMessage}
</c:if>
</BODY>


<%--<div id="mainWrapper">
    <div class="login-container">
        <div class="login-card">
            <div class="login-form">
                <c:url var="loginUrl" value="/login" />
                <form action="${loginUrl}" method="post" class="form-horizontal">
                    <c:if test="${param.error != null}">
                        <div class="alert alert-danger">
                            <p>Invalid username and password.</p>
                        </div>
                    </c:if>
                    <c:if test="${param.logout != null}">
                        <div class="alert alert-success">
                            <p>You have been logged out successfully.</p>
                        </div>
                    </c:if>
                    <div class="input-group input-sm">
                        <label class="input-group-addon" for="username"><i class="fa fa-user"></i></label>
                        <input type="text" class="form-control" id="username" name="userName" placeholder="Enter Username" required="required"><jsp:text/></input>
                    </div>
                    <div class="input-group input-sm">
                        <label class="input-group-addon" for="password"><i class="fa fa-lock"></i></label>
                        <input type="password" class="form-control" id="password" name="password" placeholder="Enter Password" required="required"><jsp:text/></input>
                    </div>
                    &lt;%&ndash;<input type="hidden" name="${_csrf.parameterName}"  value="${_csrf.token}"><jsp:text/></input>&ndash;%&gt;
                    <input type="hidden" name="${_csrf.parameterName}"  value="${_csrf.token}"/><jsp:text/></input>

                    <div class="form-actions">
                        <input type="submit"
                               class="btn btn-block btn-primary btn-default" value="Log in"/>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>--%>


</HTML>
