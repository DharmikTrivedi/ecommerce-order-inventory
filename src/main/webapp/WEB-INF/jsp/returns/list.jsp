<%@ include file="../common/taglibs.jspf" %>
<c:set var="pageTitle" value="Return requests"/>
<%@ include file="../common/header.jspf" %>

<h1>Return / refund requests</h1>

<form class="filters" method="get" action="${ctx}/returns">
    <select name="status" onchange="this.form.submit()">
        <option value="">All</option>
        <c:forEach items="${statuses}" var="st">
            <option value="${st}" ${st == status ? 'selected' : ''}>${st}</option>
        </c:forEach>
    </select>
</form>

<table class="data">
    <thead><tr><th>#</th><th>Order</th><th>Product</th><th class="num">Qty</th><th>Reason</th><th>Requested</th><th>Status</th><th class="num">Refund (&#8377;)</th><th></th></tr></thead>
    <tbody>
    <c:forEach items="${returns}" var="r">
        <tr>
            <td>${r.id}</td>
            <td><a href="${ctx}/orders/${r.orderItem.order.id}">${r.orderItem.order.orderNumber}</a></td>
            <td><c:out value="${r.orderItem.product.name}"/></td>
            <td class="num">${r.quantity}</td>
            <td class="small"><c:out value="${r.reason}"/></td>
            <td class="small">${r.requestedOn.format(dtFmt)}</td>
            <td><span class="tag tag-${r.status}">${r.status}</span></td>
            <td class="num"><c:if test="${not empty r.refundAmount}"><fmt:formatNumber value="${r.refundAmount}" pattern="#,##0.00"/></c:if></td>
            <td class="actions right">
                <c:if test="${r.status == 'REQUESTED'}">
                    <form class="inline" method="post" action="${ctx}/returns/${r.id}/approve"
                          data-confirm="Approve return #${r.id}? Refund will be issued and stock added back.">
                        <button class="btn btn-sm btn-success">Approve</button>
                    </form>
                    <form class="inline" method="post" action="${ctx}/returns/${r.id}/reject" data-confirm="Reject return #${r.id}?">
                        <button class="btn btn-sm btn-danger">Reject</button>
                    </form>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty returns}"><tr><td colspan="9" class="muted">No return requests</td></tr></c:if>
    </tbody>
</table>

<%@ include file="../common/footer.jspf" %>
