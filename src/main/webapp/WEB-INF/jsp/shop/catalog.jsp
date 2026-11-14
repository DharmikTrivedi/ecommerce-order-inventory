<%@ include file="../common/taglibs.jspf" %>
<c:set var="pageTitle" value="Shop"/>
<%@ include file="../common/header.jspf" %>

<div class="page-head">
    <h1>Shop</h1>
    <c:if test="${empty currentCustomerId}">
        <span class="muted">Pick a customer in <strong>Shopping as</strong> (top right) to add items to a cart</span>
    </c:if>
</div>

<form class="filters" method="get" action="${ctx}/shop">
    <div class="search-box">
        <input type="text" id="liveSearch" name="q" value="${fn:escapeXml(q)}" placeholder="Search products..." autocomplete="off" style="width:280px">
        <div id="suggestions" class="suggestions"></div>
    </div>
    <select name="category" onchange="this.form.submit()">
        <option value="">All categories</option>
        <c:forEach items="${categories}" var="cat">
            <option value="${cat.id}" ${cat.id == category ? 'selected' : ''}>${cat.name}</option>
        </c:forEach>
    </select>
    <select name="sort" onchange="this.form.dir.value = this.options[this.selectedIndex].dataset.dir; this.form.submit()">
        <option value="name" data-dir="asc" ${sort == 'name' ? 'selected' : ''}>Name A-Z</option>
        <option value="price" data-dir="asc" ${sort == 'price' && dir == 'asc' ? 'selected' : ''}>Price: low to high</option>
        <option value="price" data-dir="desc" ${sort == 'price' && dir == 'desc' ? 'selected' : ''}>Price: high to low</option>
    </select>
    <input type="hidden" name="dir" value="${dir}">
    <button class="btn">Search</button>
    <c:if test="${not empty q || not empty category}"><a href="${ctx}/shop">Clear</a></c:if>
</form>

<div class="products">
    <c:forEach items="${page.content}" var="p">
        <c:set var="inWish" value="${not empty wishlist && wishlist.contains(p)}"/>
        <div class="product" data-id="${p.id}">
            <c:if test="${not empty currentCustomerId}">
                <button type="button" class="wish ${inWish ? 'on' : ''}" data-id="${p.id}" title="Wishlist">&#10084;</button>
            </c:if>
            <span class="cat">${p.category.name}</span>
            <h3><c:out value="${p.name}"/></h3>
            <div class="desc"><c:out value="${p.description}"/><br><span class="small">Sold by ${p.seller.name}</span></div>
            <div class="price">&#8377;<fmt:formatNumber value="${p.price}" pattern="#,##0.00"/></div>
            <div class="stock">
                <c:choose>
                    <c:when test="${p.stockQty == 0}"><span class="out-stock">Out of stock</span></c:when>
                    <c:when test="${p.lowStock}"><span class="out-stock">Only ${p.stockQty} left</span></c:when>
                    <c:otherwise><span class="in-stock">${p.stockQty} in stock</span></c:otherwise>
                </c:choose>
            </div>
            <div class="buy">
                <input type="number" class="qty-input" value="1" min="1" max="10" ${p.stockQty == 0 ? 'disabled' : ''}>
                <button type="button" class="btn btn-warn btn-add-cart" data-id="${p.id}"
                        ${p.stockQty == 0 || empty currentCustomerId ? 'disabled' : ''}>Add to cart</button>
            </div>
        </div>
    </c:forEach>
</div>
<c:if test="${page.totalElements == 0}"><p class="muted">No products match your search.</p></c:if>

<c:url var="pageBase" value="/shop"><c:param name="q" value="${q}"/><c:param name="category" value="${category}"/><c:param name="sort" value="${sort}"/><c:param name="dir" value="${dir}"/></c:url>
<t:pager page="${page}" base="${pageBase}"/>

<%@ include file="../common/footer.jspf" %>
