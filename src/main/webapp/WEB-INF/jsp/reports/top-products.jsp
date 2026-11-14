<%@ include file="../common/taglibs.jspf" %>
<c:set var="pageTitle" value="Top selling products"/>
<%@ include file="../common/header.jspf" %>

<div class="page-head">
    <h1>Top ${n} selling products</h1>
    <form class="filters" method="get" action="${ctx}/reports/top-products" style="margin:0">
        <label for="n">Show top</label>
        <select id="n" name="n" onchange="this.form.submit()">
            <c:forEach items="5,10,20" var="opt">
                <option ${opt == n ? 'selected' : ''}>${opt}</option>
            </c:forEach>
        </select>
    </form>
</div>

<table class="data">
    <thead><tr><th>Rank</th><th>SKU</th><th>Product</th><th class="num">Units sold</th><th class="num">Revenue (&#8377;)</th></tr></thead>
    <tbody>
    <c:forEach items="${rows}" var="r" varStatus="s">
        <tr>
            <td>${s.count}</td>
            <td class="small">${r.sku}</td>
            <td><a href="${ctx}/products/${r.productId}/edit"><c:out value="${r.name}"/></a></td>
            <td class="num">${r.qtySold}</td>
            <td class="num"><fmt:formatNumber value="${r.revenue}" pattern="#,##0.00"/></td>
        </tr>
    </c:forEach>
    <c:if test="${empty rows}"><tr><td colspan="5" class="muted">No sales yet</td></tr></c:if>
    </tbody>
</table>

<%@ include file="../common/footer.jspf" %>
