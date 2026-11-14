<%@ include file="../common/taglibs.jspf" %>
<c:set var="pageTitle" value="Categories"/>
<%@ include file="../common/header.jspf" %>

<div class="page-head">
    <h1>Categories</h1>
    <a class="btn btn-primary" href="${ctx}/categories/new">+ Add category</a>
</div>

<table class="data">
    <thead><tr><th>#</th><th>Name</th><th>Description</th><th class="num">Products</th><th></th></tr></thead>
    <tbody>
    <c:forEach items="${rows}" var="r">
        <tr>
            <td>${r[0]}</td>
            <td><a href="${ctx}/products?category=${r[0]}"><c:out value="${r[1]}"/></a></td>
            <td class="muted"><c:out value="${r[2]}"/></td>
            <td class="num">${r[3]}</td>
            <td class="actions right">
                <a class="btn btn-sm" href="${ctx}/categories/${r[0]}/edit">Edit</a>
                <form class="inline" method="post" action="${ctx}/categories/${r[0]}/delete"
                      data-confirm="Delete category '${fn:escapeXml(r[1])}'?">
                    <button class="btn btn-sm btn-danger">Delete</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty rows}"><tr><td colspan="5" class="muted">No categories yet</td></tr></c:if>
    </tbody>
</table>

<%@ include file="../common/footer.jspf" %>
