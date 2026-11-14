<%@ include file="../common/taglibs.jspf" %>
<c:set var="pageTitle" value="Products"/>
<%@ include file="../common/header.jspf" %>

<div class="page-head">
    <h1>Products</h1>
    <a class="btn btn-primary" href="${ctx}/products/new">+ Add product</a>
</div>

<form class="filters" method="get" action="${ctx}/products">
    <input type="text" name="q" value="${fn:escapeXml(q)}" placeholder="Name or SKU">
    <select name="category" onchange="this.form.submit()">
        <option value="">All categories</option>
        <c:forEach items="${categories}" var="cat">
            <option value="${cat.id}" ${cat.id == category ? 'selected' : ''}>${cat.name}</option>
        </c:forEach>
    </select>
    <input type="hidden" name="sort" value="${sort}">
    <input type="hidden" name="dir" value="${dir}">
    <button class="btn">Search</button>
    <c:if test="${not empty q || not empty category}"><a href="${ctx}/products">Clear</a></c:if>
</form>

<c:url var="filterBase" value="/products"><c:param name="q" value="${q}"/><c:param name="category" value="${category}"/></c:url>
<c:url var="pageBase" value="/products"><c:param name="q" value="${q}"/><c:param name="category" value="${category}"/><c:param name="sort" value="${sort}"/><c:param name="dir" value="${dir}"/></c:url>

<table class="data">
    <thead>
    <tr>
        <th><t:sortlink field="sku" label="SKU" base="${filterBase}" sort="${sort}" dir="${dir}"/></th>
        <th><t:sortlink field="name" label="Name" base="${filterBase}" sort="${sort}" dir="${dir}"/></th>
        <th>Category</th>
        <th>Seller</th>
        <th class="num"><t:sortlink field="price" label="Price (&#8377;)" base="${filterBase}" sort="${sort}" dir="${dir}"/></th>
        <th class="num"><t:sortlink field="stockQty" label="Stock" base="${filterBase}" sort="${sort}" dir="${dir}"/></th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.content}" var="p">
        <tr>
            <td class="small">${p.sku}</td>
            <td><c:out value="${p.name}"/>
                <c:if test="${!p.active}"> <span class="tag tag-off">inactive</span></c:if></td>
            <td>${p.category.name}</td>
            <td>${p.seller.name}</td>
            <td class="num"><fmt:formatNumber value="${p.price}" pattern="#,##0.00"/></td>
            <td class="num">${p.stockQty}
                <c:if test="${p.active && p.lowStock}"> <span class="tag tag-low">low</span></c:if></td>
            <td class="actions right">
                <a class="btn btn-sm" href="${ctx}/products/${p.id}/edit">Edit</a>
                <form class="inline" method="post" action="${ctx}/products/${p.id}/delete"
                      data-confirm="Delete ${fn:escapeXml(p.name)}? If it was ordered before it will only be deactivated.">
                    <button class="btn btn-sm btn-danger">Delete</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${page.totalElements == 0}"><tr><td colspan="7" class="muted">No products found</td></tr></c:if>
    </tbody>
</table>
<t:pager page="${page}" base="${pageBase}"/>

<%@ include file="../common/footer.jspf" %>
