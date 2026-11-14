<%@ include file="../common/taglibs.jspf" %>
<c:set var="pageTitle" value="Low stock report"/>
<%@ include file="../common/header.jspf" %>

<div class="page-head">
    <h1>Low stock / reorder report</h1>
    <button class="btn" onclick="window.print()">Print</button>
</div>
<p class="muted">Active products whose stock is at or below the reorder level, lowest first.</p>

<table class="data">
    <thead><tr><th>SKU</th><th>Product</th><th>Seller</th><th>Seller phone</th><th class="num">In stock</th><th class="num">Reorder level</th><th class="num">Suggested order</th><th></th></tr></thead>
    <tbody>
    <c:forEach items="${rows}" var="p">
        <tr>
            <td class="small">${p.sku}</td>
            <td><c:out value="${p.name}"/></td>
            <td>${p.seller.name}</td>
            <td>${p.seller.phone}</td>
            <td class="num"><span class="tag ${p.stockQty == 0 ? 'tag-CANCELLED' : 'tag-low'}">${p.stockQty}</span></td>
            <td class="num">${p.reorderLevel}</td>
            <%-- bring stock up to twice the reorder level --%>
            <td class="num">${p.reorderLevel * 2 - p.stockQty}</td>
            <td class="right"><a class="btn btn-sm" href="${ctx}/products/${p.id}/edit">Update stock</a></td>
        </tr>
    </c:forEach>
    <c:if test="${empty rows}"><tr><td colspan="8" class="muted">All products are above their reorder level</td></tr></c:if>
    </tbody>
</table>

<%@ include file="../common/footer.jspf" %>
