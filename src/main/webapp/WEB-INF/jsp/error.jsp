<%@ include file="common/taglibs.jspf" %>
<c:set var="pageTitle" value="Error"/>
<%@ include file="common/header.jspf" %>
<%-- used by GlobalExceptionHandler (code/title/message) and by Spring Boot's default /error (status/error) --%>
<c:set var="errCode" value="${not empty code ? code : status}"/>
<c:set var="errTitle" value="${not empty title ? title : (errCode == 404 ? 'Page not found' : 'Something went wrong')}"/>

<div class="card error-box">
    <div class="code">${errCode}</div>
    <h1><c:out value="${errTitle}"/></h1>
    <p><c:out value="${not empty message ? message : 'The page you requested could not be loaded.'}"/></p>
    <p class="muted small">Requested: <c:out value="${path}"/></p>
    <p>
        <a class="btn" href="javascript:history.back()">Go back</a>
        <a class="btn btn-primary" href="${ctx}/">Dashboard</a>
    </p>
</div>

<%@ include file="common/footer.jspf" %>
