<%@ include file="../common/taglibs.jspf" %>
<c:set var="pageTitle" value="Customers"/>
<%@ include file="../common/header.jspf" %>

<div class="page-head">
    <h1>Customers</h1>
    <a class="btn btn-primary" href="${ctx}/customers/new">+ Register customer</a>
</div>

<form class="filters" method="get" action="${ctx}/customers">
    <input type="text" name="q" value="${fn:escapeXml(q)}" placeholder="Name or email">
    <select name="city" onchange="this.form.submit()">
        <option value="">All cities</option>
        <c:forEach items="${cities}" var="ct">
            <option ${ct == city ? 'selected' : ''}>${ct}</option>
        </c:forEach>
    </select>
    <input type="hidden" name="sort" value="${sort}">
    <input type="hidden" name="dir" value="${dir}">
    <button class="btn">Search</button>
    <c:if test="${not empty q || not empty city}"><a href="${ctx}/customers">Clear</a></c:if>
</form>

<%-- base url keeps the filters, the tags add sort/page --%>
<c:url var="filterBase" value="/customers"><c:param name="q" value="${q}"/><c:param name="city" value="${city}"/></c:url>
<c:url var="pageBase" value="/customers"><c:param name="q" value="${q}"/><c:param name="city" value="${city}"/><c:param name="sort" value="${sort}"/><c:param name="dir" value="${dir}"/></c:url>

<table class="data">
    <thead>
    <tr>
        <th><t:sortlink field="firstName" label="Name" base="${filterBase}" sort="${sort}" dir="${dir}"/></th>
        <th><t:sortlink field="email" label="Email" base="${filterBase}" sort="${sort}" dir="${dir}"/></th>
        <th>Phone</th>
        <th><t:sortlink field="city" label="City" base="${filterBase}" sort="${sort}" dir="${dir}"/></th>
        <th><t:sortlink field="registeredOn" label="Registered" base="${filterBase}" sort="${sort}" dir="${dir}"/></th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.content}" var="cu">
        <tr>
            <td><a href="${ctx}/customers/${cu.id}"><c:out value="${cu.fullName}"/></a></td>
            <td>${cu.email}</td>
            <td>${cu.phone}</td>
            <td><c:out value="${cu.city}"/></td>
            <td>${cu.registeredOn.format(dFmt)}</td>
            <td class="actions right">
                <a class="btn btn-sm" href="${ctx}/customers/${cu.id}/edit">Edit</a>
                <form class="inline" method="post" action="${ctx}/customers/${cu.id}/delete"
                      data-confirm="Delete customer ${fn:escapeXml(cu.fullName)}?">
                    <button class="btn btn-sm btn-danger">Delete</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${page.totalElements == 0}"><tr><td colspan="6" class="muted">No customers match the search</td></tr></c:if>
    </tbody>
</table>
<t:pager page="${page}" base="${pageBase}"/>

<%@ include file="../common/footer.jspf" %>
