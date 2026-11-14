<%@ include file="../common/taglibs.jspf" %>
<c:set var="pageTitle" value="Monthly sales report"/>
<%@ include file="../common/header.jspf" %>

<div class="page-head">
    <h1>Monthly sales report</h1>
    <button class="btn" onclick="window.print()">Print</button>
</div>
<p class="muted">Cancelled orders are excluded.</p>

<table class="data">
    <thead><tr><th>Month</th><th class="num">Orders</th><th class="num">Revenue (&#8377;)</th><th class="num">Avg order (&#8377;)</th><th style="width:35%"></th></tr></thead>
    <tbody>
    <c:forEach items="${rows}" var="m">
        <tr>
            <td>${m.label}</td>
            <td class="num">${m.orderCount}</td>
            <td class="num"><fmt:formatNumber value="${m.revenue}" pattern="#,##0.00"/></td>
            <td class="num"><fmt:formatNumber value="${m.revenue / m.orderCount}" pattern="#,##0.00"/></td>
            <td><div class="bar" style="width:${m.revenue * 100 / maxRevenue}%"></div></td>
        </tr>
    </c:forEach>
    <c:if test="${empty rows}"><tr><td colspan="5" class="muted">No sales yet</td></tr></c:if>
    <tr class="total-row">
        <td>Total</td>
        <td class="num">${totalOrders}</td>
        <td class="num"><fmt:formatNumber value="${grandTotal}" pattern="#,##0.00"/></td>
        <td colspan="2"></td>
    </tr>
    </tbody>
</table>

<%@ include file="../common/footer.jspf" %>
