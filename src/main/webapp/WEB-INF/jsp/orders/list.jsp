<%@ include file="../common/taglibs.jspf" %>
<c:set var="pageTitle" value="Orders"/>
<%@ include file="../common/header.jspf" %>

<h1>Orders</h1>

<form class="filters" method="get" action="${ctx}/orders">
    <input type="text" name="q" value="${fn:escapeXml(q)}" placeholder="Order no. or customer name">
    <select name="status" onchange="this.form.submit()">
        <option value="">All statuses</option>
        <c:forEach items="${statuses}" var="st">
            <option value="${st}" ${st == status ? 'selected' : ''}>${st}</option>
        </c:forEach>
    </select>
    <input type="hidden" name="sort" value="${sort}">
    <input type="hidden" name="dir" value="${dir}">
    <button class="btn">Search</button>
    <c:if test="${not empty q || not empty status}"><a href="${ctx}/orders">Clear</a></c:if>
</form>

<c:url var="filterBase" value="/orders"><c:param name="q" value="${q}"/><c:param name="status" value="${status}"/></c:url>
<c:url var="pageBase" value="/orders"><c:param name="q" value="${q}"/><c:param name="status" value="${status}"/><c:param name="sort" value="${sort}"/><c:param name="dir" value="${dir}"/></c:url>

<table class="data">
    <thead>
    <tr>
        <th><t:sortlink field="orderNumber" label="Order no." base="${filterBase}" sort="${sort}" dir="${dir}"/></th>
        <th>Customer</th>
        <th><t:sortlink field="orderDate" label="Date" base="${filterBase}" sort="${sort}" dir="${dir}"/></th>
        <th>Payment</th>
        <th class="num"><t:sortlink field="totalAmount" label="Amount (&#8377;)" base="${filterBase}" sort="${sort}" dir="${dir}"/></th>
        <th><t:sortlink field="status" label="Status" base="${filterBase}" sort="${sort}" dir="${dir}"/></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.content}" var="o">
        <tr>
            <td><a href="${ctx}/orders/${o.id}">${o.orderNumber}</a></td>
            <td><a href="${ctx}/customers/${o.customer.id}"><c:out value="${o.customer.fullName}"/></a></td>
            <td>${o.orderDate.format(dtFmt)}</td>
            <td>${o.paymentMode}</td>
            <td class="num"><fmt:formatNumber value="${o.totalAmount}" pattern="#,##0.00"/></td>
            <td><span class="tag tag-${o.status}">${o.status}</span></td>
        </tr>
    </c:forEach>
    <c:if test="${page.totalElements == 0}"><tr><td colspan="6" class="muted">No orders found</td></tr></c:if>
    </tbody>
</table>
<t:pager page="${page}" base="${pageBase}"/>

<%@ include file="../common/footer.jspf" %>
