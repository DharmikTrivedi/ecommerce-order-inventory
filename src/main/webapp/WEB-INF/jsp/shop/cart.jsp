<%@ include file="../common/taglibs.jspf" %>
<c:set var="pageTitle" value="Cart"/>
<%@ include file="../common/header.jspf" %>

<h1>Shopping cart</h1>

<c:choose>
    <c:when test="${empty items}">
        <div class="card">
            <p>The cart is empty.</p>
            <a class="btn btn-primary" href="${ctx}/shop">Continue shopping</a>
        </div>
    </c:when>
    <c:otherwise>
        <table class="data">
            <thead><tr><th>Product</th><th class="num">Price (&#8377;)</th><th>Quantity</th><th class="num">In stock</th><th class="num">Subtotal (&#8377;)</th><th></th></tr></thead>
            <tbody>
            <c:forEach items="${items}" var="it">
                <%-- max allowed = min(10, stock) --%>
                <c:set var="lineMax" value="${it.product.stockQty < maxQty ? it.product.stockQty : maxQty}"/>
                <tr class="cart-row" data-id="${it.id}" data-max="${lineMax}">
                    <td><c:out value="${it.product.name}"/><br><span class="small muted">${it.product.sku}</span></td>
                    <td class="num"><fmt:formatNumber value="${it.product.price}" pattern="#,##0.00"/></td>
                    <td>
                        <span class="qty-box">
                            <button type="button" class="qty-minus">-</button>
                            <input type="text" class="qty-input" value="${it.quantity}">
                            <button type="button" class="qty-plus">+</button>
                        </span>
                    </td>
                    <td class="num">${it.product.stockQty}</td>
                    <td class="num line-subtotal"><fmt:formatNumber value="${it.subtotal}" pattern="#,##0.00"/></td>
                    <td class="right">
                        <form class="inline" method="post" action="${ctx}/cart/${it.id}/remove">
                            <button class="btn btn-sm">Remove</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <tr class="total-row">
                <td colspan="4" class="right">Total</td>
                <td class="num">&#8377;<span id="cartTotal"><fmt:formatNumber value="${total}" pattern="#,##0.00"/></span></td>
                <td></td>
            </tr>
            </tbody>
        </table>
        <p class="muted small">Max ${maxQty} units per product. Quantity changes are saved immediately.</p>
        <p class="right">
            <a class="btn" href="${ctx}/shop">Continue shopping</a>
            <a class="btn btn-success" href="${ctx}/checkout">Proceed to checkout</a>
        </p>
    </c:otherwise>
</c:choose>

<%@ include file="../common/footer.jspf" %>
