<%@ include file="../common/taglibs.jspf" %>
<c:set var="pageTitle" value="${customer.fullName}"/>
<%@ include file="../common/header.jspf" %>

<div class="page-head">
    <h1><c:out value="${customer.fullName}"/></h1>
    <div>
        <a class="btn" href="${ctx}/customers/${customer.id}/edit">Edit</a>
        <a class="btn" href="${ctx}/customers">Back to list</a>
    </div>
</div>

<div class="grid-2">
    <div class="card">
        <h2 style="margin-top:0">Details</h2>
        <p><strong>Email:</strong> ${customer.email}<br>
           <strong>Mobile:</strong> ${customer.phone}<br>
           <strong>Address:</strong> <c:out value="${customer.address}"/>, <c:out value="${customer.city}"/> - ${customer.pincode}<br>
           <strong>Customer since:</strong> ${customer.registeredOn.format(dFmt)}</p>
    </div>
    <div class="card">
        <h2 style="margin-top:0">Wishlist (${fn:length(customer.wishlist)})</h2>
        <c:forEach items="${customer.wishlist}" var="w">
            <div>${w.name} <span class="muted">- &#8377;<fmt:formatNumber value="${w.price}" pattern="#,##0.00"/></span></div>
        </c:forEach>
        <c:if test="${empty customer.wishlist}"><p class="muted">Nothing saved yet</p></c:if>
    </div>
</div>

<h2>Order history</h2>
<c:set var="spent" value="${0}"/>
<table class="data">
    <thead><tr><th>Order no.</th><th>Date</th><th class="num">Items</th><th>Payment</th><th class="num">Amount (&#8377;)</th><th>Status</th></tr></thead>
    <tbody>
    <c:forEach items="${orders}" var="o">
        <tr>
            <td><a href="${ctx}/orders/${o.id}">${o.orderNumber}</a></td>
            <td>${o.orderDate.format(dtFmt)}</td>
            <td class="num">${fn:length(o.items)}</td>
            <td>${o.paymentMode}</td>
            <td class="num"><fmt:formatNumber value="${o.totalAmount}" pattern="#,##0.00"/></td>
            <td><span class="tag tag-${o.status}">${o.status}</span></td>
        </tr>
        <c:if test="${o.status != 'CANCELLED'}"><c:set var="spent" value="${spent + o.totalAmount}"/></c:if>
    </c:forEach>
    <c:if test="${empty orders}"><tr><td colspan="6" class="muted">No orders yet</td></tr></c:if>
    </tbody>
</table>
<c:if test="${not empty orders}">
    <p class="right"><strong>Total spent: &#8377;<fmt:formatNumber value="${spent}" pattern="#,##0.00"/></strong></p>
</c:if>

<%@ include file="../common/footer.jspf" %>
