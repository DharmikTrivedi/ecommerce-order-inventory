<%@ include file="common/taglibs.jspf" %>
<c:set var="pageTitle" value="Dashboard"/>
<%@ include file="common/header.jspf" %>

<h1>Dashboard</h1>

<div class="stats">
    <div class="stat ok"><div class="val">&#8377;<fmt:formatNumber value="${counts.revenue}" pattern="#,##0"/></div><div class="lbl">Total revenue</div></div>
    <div class="stat"><div class="val">${counts.orders}</div><div class="lbl">Orders</div></div>
    <div class="stat orange"><div class="val"><a href="${ctx}/orders?status=PLACED">${counts.pendingOrders}</a></div><div class="lbl">Orders to ship</div></div>
    <div class="stat"><div class="val">${counts.products}</div><div class="lbl">Active products</div></div>
    <div class="stat"><div class="val">${counts.customers}</div><div class="lbl">Customers</div></div>
    <div class="stat warn"><div class="val"><a href="${ctx}/reports/low-stock">${counts.lowStock}</a></div><div class="lbl">Low stock items</div></div>
    <div class="stat orange"><div class="val"><a href="${ctx}/returns?status=REQUESTED">${counts.pendingReturns}</a></div><div class="lbl">Pending returns</div></div>
</div>

<div class="grid-2">
    <div class="card">
        <h2 style="margin-top:0">Recent orders</h2>
        <table class="data">
            <thead><tr><th>Order</th><th>Customer</th><th>Date</th><th class="num">Amount</th><th>Status</th></tr></thead>
            <tbody>
            <c:forEach items="${recentOrders}" var="o">
                <tr>
                    <td><a href="${ctx}/orders/${o.id}">${o.orderNumber}</a></td>
                    <td>${o.customer.fullName}</td>
                    <td>${o.orderDate.format(dFmt)}</td>
                    <td class="num"><fmt:formatNumber value="${o.totalAmount}" pattern="#,##0.00"/></td>
                    <td><span class="tag tag-${o.status}">${o.status}</span></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="card">
        <h2 style="margin-top:0">Recent stock activity</h2>
        <table class="data">
            <thead><tr><th>When</th><th>Product</th><th>Type</th><th class="num">Qty</th><th>Ref</th></tr></thead>
            <tbody>
            <c:forEach items="${movements}" var="m">
                <tr>
                    <td class="small">${m.createdAt.format(dtFmt)}</td>
                    <td>${m.product.name}</td>
                    <td>${m.movementType}</td>
                    <td class="num ${m.changeQty > 0 ? 'plus' : 'minus'}">${m.changeQty > 0 ? '+' : ''}${m.changeQty}</td>
                    <td class="small">${m.reference}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<c:if test="${not empty lowStock}">
    <div class="card">
        <h2 style="margin-top:0">Needs reorder</h2>
        <c:forEach items="${lowStock}" var="p" varStatus="s">
            <a href="${ctx}/products/${p.id}/edit">${p.name}</a> <span class="tag tag-low">${p.stockQty} left</span><c:if test="${!s.last}"> &nbsp;&middot;&nbsp; </c:if>
        </c:forEach>
    </div>
</c:if>

<%@ include file="common/footer.jspf" %>
