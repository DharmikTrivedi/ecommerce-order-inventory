<%@ include file="../common/taglibs.jspf" %>
<c:set var="pageTitle" value="Checkout"/>
<%@ include file="../common/header.jspf" %>

<h1>Checkout</h1>

<div class="grid-2">
    <div class="card">
        <h2 style="margin-top:0">Delivery and payment</h2>
        <form method="post" action="${ctx}/checkout" class="js-validate" novalidate>
            <div class="form-row">
                <label>Customer</label>
                <div><c:out value="${customer.fullName}"/> (${customer.phone})</div>
            </div>
            <div class="form-row">
                <label for="shippingAddress">Shipping address *</label>
                <textarea id="shippingAddress" name="shippingAddress" rows="3" maxlength="255" readonly required
                          data-label="Shipping address" data-minlen="10"
                          data-default="${fn:escapeXml(defaultAddress)}">${fn:escapeXml(defaultAddress)}</textarea>
                <label style="font-weight:normal; margin-top:6px">
                    <input type="checkbox" id="useOtherAddress"> Ship to a different address
                </label>
            </div>
            <div class="form-row">
                <label for="paymentMode">Payment mode *</label>
                <select id="paymentMode" name="paymentMode" required data-label="Payment mode">
                    <option value="">-- choose --</option>
                    <c:forEach items="${paymentModes}" var="pm">
                        <option value="${pm}">${pm == 'COD' ? 'Cash on delivery' : pm}</option>
                    </c:forEach>
                </select>
            </div>
            <button class="btn btn-success">Place order</button>
            <a class="btn" href="${ctx}/cart">Back to cart</a>
        </form>
    </div>

    <div class="card">
        <h2 style="margin-top:0">Order summary</h2>
        <table class="data">
            <thead><tr><th>Item</th><th class="num">Qty</th><th class="num">Amount (&#8377;)</th></tr></thead>
            <tbody>
            <c:forEach items="${items}" var="it">
                <tr>
                    <td><c:out value="${it.product.name}"/></td>
                    <td class="num">${it.quantity}</td>
                    <td class="num"><fmt:formatNumber value="${it.subtotal}" pattern="#,##0.00"/></td>
                </tr>
            </c:forEach>
            <tr class="total-row"><td colspan="2" class="right">Total</td>
                <td class="num">&#8377;<fmt:formatNumber value="${total}" pattern="#,##0.00"/></td></tr>
            </tbody>
        </table>
        <p class="small muted">Stock is checked again when the order is placed.</p>
    </div>
</div>

<%@ include file="../common/footer.jspf" %>
