<%@ include file="../common/taglibs.jspf" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="${empty product.id ? 'Add product' : 'Edit product'}"/>
<%@ include file="../common/header.jspf" %>

<h1>${pageTitle}</h1>

<div class="card form-narrow">
    <form:form modelAttribute="product" action="${ctx}/products/save" method="post" cssClass="js-validate" novalidate="novalidate">
        <form:hidden path="id"/>
        <div class="form-cols">
            <div class="form-row">
                <label for="sku">SKU *</label>
                <form:input path="sku" maxlength="7" required="required" data-label="SKU"
                            data-pattern="[A-Z]{2}-[0-9]{4}" data-pattern-msg="SKU must look like EL-1001"/>
                <form:errors path="sku" cssClass="field-error"/>
            </div>
            <div class="form-row">
                <label for="name">Product name *</label>
                <form:input path="name" maxlength="120" required="required" data-label="Product name"/>
                <form:errors path="name" cssClass="field-error"/>
            </div>
        </div>
        <div class="form-row">
            <label for="description">Description</label>
            <form:textarea path="description" rows="2" maxlength="500"/>
        </div>
        <div class="form-cols">
            <div class="form-row">
                <label for="category">Category *</label>
                <%-- plain select: Spring Data converts the submitted id into a Category --%>
                <select id="category" name="category" required data-label="Category">
                    <option value="">-- select --</option>
                    <c:forEach items="${categories}" var="cat">
                        <option value="${cat.id}" ${cat.id == product.category.id ? 'selected' : ''}>${cat.name}</option>
                    </c:forEach>
                </select>
                <form:errors path="category" cssClass="field-error"/>
            </div>
            <div class="form-row">
                <label for="seller">Seller *</label>
                <select id="seller" name="seller" required data-label="Seller">
                    <option value="">-- select --</option>
                    <c:forEach items="${sellers}" var="s">
                        <option value="${s.id}" ${s.id == product.seller.id ? 'selected' : ''}>${s.name}</option>
                    </c:forEach>
                </select>
                <form:errors path="seller" cssClass="field-error"/>
            </div>
            <div class="form-row">
                <label for="price">Price (&#8377;) *</label>
                <form:input path="price" type="number" step="0.01" min="1" required="required" data-label="Price"/>
                <form:errors path="price" cssClass="field-error"/>
            </div>
            <div class="form-row">
                <label for="stockQty">Stock quantity *</label>
                <form:input path="stockQty" type="number" min="0" required="required" data-label="Stock quantity"/>
                <form:errors path="stockQty" cssClass="field-error"/>
            </div>
            <div class="form-row">
                <label for="reorderLevel">Reorder level *</label>
                <form:input path="reorderLevel" type="number" min="0" required="required" data-label="Reorder level"/>
                <span class="hint">Shown in the low stock report when stock reaches this number</span>
                <form:errors path="reorderLevel" cssClass="field-error"/>
            </div>
            <div class="form-row">
                <label>&nbsp;</label>
                <label style="font-weight:normal"><form:checkbox path="active"/> Active (visible in shop)</label>
            </div>
        </div>
        <button class="btn btn-primary">Save</button>
        <a class="btn" href="${ctx}/products">Cancel</a>
    </form:form>
</div>

<%@ include file="../common/footer.jspf" %>
