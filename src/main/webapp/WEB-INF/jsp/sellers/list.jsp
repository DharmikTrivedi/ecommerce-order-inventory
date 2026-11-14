<%@ include file="../common/taglibs.jspf" %>
<c:set var="pageTitle" value="Sellers"/>
<%@ include file="../common/header.jspf" %>

<div class="page-head">
    <h1>Sellers</h1>
    <a class="btn btn-primary" href="${ctx}/sellers/new">+ Add seller</a>
</div>

<form class="filters" method="get" action="${ctx}/sellers">
    <input type="text" name="q" value="${fn:escapeXml(q)}" placeholder="Search name or city">
    <button class="btn">Search</button>
    <c:if test="${not empty q}"><a href="${ctx}/sellers">Clear</a></c:if>
</form>

<table class="data">
    <thead><tr><th>Name</th><th>Email</th><th>Phone</th><th>City</th><th>GSTIN</th><th>Joined</th><th></th></tr></thead>
    <tbody>
    <c:forEach items="${sellers}" var="s">
        <tr>
            <td><c:out value="${s.name}"/></td>
            <td>${s.email}</td>
            <td>${s.phone}</td>
            <td><c:out value="${s.city}"/></td>
            <td class="small">${s.gstin}</td>
            <td>${s.joinedOn.format(dFmt)}</td>
            <td class="actions right">
                <a class="btn btn-sm" href="${ctx}/sellers/${s.id}/edit">Edit</a>
                <form class="inline" method="post" action="${ctx}/sellers/${s.id}/delete"
                      data-confirm="Delete seller '${fn:escapeXml(s.name)}'?">
                    <button class="btn btn-sm btn-danger">Delete</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty sellers}"><tr><td colspan="7" class="muted">No sellers found</td></tr></c:if>
    </tbody>
</table>

<%@ include file="../common/footer.jspf" %>
