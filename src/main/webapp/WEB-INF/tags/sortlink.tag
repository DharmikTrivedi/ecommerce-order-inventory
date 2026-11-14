<%@ tag pageEncoding="UTF-8" %>
<%@ attribute name="field" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="base" required="true" %>
<%@ attribute name="sort" required="true" %>
<%@ attribute name="dir" required="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- clicking the current column flips the direction --%>
<c:set var="newDir" value="${sort == field && dir == 'asc' ? 'desc' : 'asc'}"/>
<a class="sortlink" href="${base}&sort=${field}&dir=${newDir}">${label}<c:if test="${sort == field}"> ${dir == 'asc' ? '&#9650;' : '&#9660;'}</c:if></a>
