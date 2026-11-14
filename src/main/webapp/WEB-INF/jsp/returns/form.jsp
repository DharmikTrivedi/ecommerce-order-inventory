<%@ include file="../common/taglibs.jspf" %>
<c:set var="pageTitle" value="Request return"/>
<%@ include file="../common/header.jspf" %>

<h1>Return / refund request</h1>

<div class="card form-narrow">
    <p><strong>Order:</strong> <a href="${ctx}/orders/${item.order.id}">${item.order.orderNumber}</a>
        &nbsp; <strong>Delivered:</strong> ${item.order.deliveredOn.format(dFmt)}<br>
       <strong>Item:</strong> <c:out value="${item.product.name}"/> &times; ${item.quantity}
        @ &#8377;<fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/></p>

    <form method="post" action="${ctx}/returns" class="js-validate" novalidate>
        <input type="hidden" name="itemId" value="${item.id}">
        <div class="form-row">
            <label for="returnQty">Quantity to return *</label>
            <input type="number" id="returnQty" name="quantity" min="1" max="${maxQty}" required
                   value="${empty quantity ? 1 : quantity}" data-label="Quantity" data-price="${item.unitPrice}">
            <span class="hint">You can return up to ${maxQty} unit(s)</span>
        </div>
        <div class="form-row">
            <label for="reason">Reason *</label>
            <textarea id="reason" name="reason" rows="3" maxlength="255" required data-minlen="10"
                      data-label="Reason"><c:out value="${reason}"/></textarea>
        </div>
        <p>Estimated refund: <strong>&#8377;<span id="refundPreview">0.00</span></strong></p>
        <button class="btn btn-warn">Submit request</button>
        <a class="btn" href="${ctx}/orders/${item.order.id}">Cancel</a>
    </form>
</div>

<%@ include file="../common/footer.jspf" %>
