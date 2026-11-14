<%@ include file="../common/taglibs.jspf" %>
<c:set var="pageTitle" value="Order ${order.orderNumber}"/>
<%@ include file="../common/header.jspf" %>

<div class="page-head">
    <h1>Order ${order.orderNumber} <span class="tag tag-${order.status}">${order.status}</span></h1>
    <div>
        <c:if test="${order.status == 'PLACED' || order.status == 'SHIPPED'}">
            <form class="inline" method="post" action="${ctx}/orders/${order.id}/advance"
                  data-confirm="Mark this order as ${order.status == 'PLACED' ? 'SHIPPED' : 'DELIVERED'}?">
                <button class="btn btn-primary">Mark ${order.status == 'PLACED' ? 'shipped' : 'delivered'}</button>
            </form>
        </c:if>
        <c:if test="${order.status == 'PLACED'}">
            <form class="inline" method="post" action="${ctx}/orders/${order.id}/cancel"
                  data-confirm="Cancel order ${order.orderNumber}? Stock will be added back.">
                <button class="btn btn-danger">Cancel order</button>
            </form>
        </c:if>
        <a class="btn" href="${ctx}/orders">All orders</a>
    </div>
</div>

<div class="grid-2">
    <div class="card">
        <strong>Customer:</strong> <a href="${ctx}/customers/${order.customer.id}"><c:out value="${order.customer.fullName}"/></a><br>
        <strong>Phone:</strong> ${order.customer.phone}<br>
        <strong>Ship to:</strong> <c:out value="${order.shippingAddress}"/>
    </div>
    <div class="card">
        <strong>Ordered on:</strong> ${order.orderDate.format(dtFmt)}<br>
        <strong>Payment:</strong> ${order.paymentMode}<br>
        <c:if test="${not empty order.deliveredOn}">
            <strong>Delivered on:</strong> ${order.deliveredOn.format(dFmt)}
            <span class="muted small">(returns allowed for 7 days)</span>
        </c:if>
    </div>
</div>

<table class="data">
    <thead><tr><th>#</th><th>Product</th><th class="num">Unit price (&#8377;)</th><th class="num">Qty</th><th class="num">Line total (&#8377;)</th><th></th></tr></thead>
    <tbody>
    <c:forEach items="${order.items}" var="it" varStatus="s">
        <tr>
            <td>${s.count}</td>
            <td><c:out value="${it.product.name}"/> <span class="small muted">${it.product.sku}</span></td>
            <td class="num"><fmt:formatNumber value="${it.unitPrice}" pattern="#,##0.00"/></td>
            <td class="num">${it.quantity}</td>
            <td class="num"><fmt:formatNumber value="${it.lineTotal}" pattern="#,##0.00"/></td>
            <td class="right">
                <c:if test="${returnable[it.id] > 0}">
                    <a class="btn btn-sm btn-warn" href="${ctx}/returns/new?itemId=${it.id}">Return</a>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    <tr class="total-row"><td colspan="4" class="right">Order total</td>
        <td class="num">&#8377;<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/></td><td></td></tr>
    </tbody>
</table>

<%@ include file="../common/footer.jspf" %>
