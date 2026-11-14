<%@ include file="../common/taglibs.jspf" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="${empty seller.id ? 'Add seller' : 'Edit seller'}"/>
<%@ include file="../common/header.jspf" %>

<h1>${pageTitle}</h1>

<div class="card form-narrow">
    <form:form modelAttribute="seller" action="${ctx}/sellers/save" method="post" cssClass="js-validate" novalidate="novalidate">
        <form:hidden path="id"/>
        <div class="form-row">
            <label for="name">Business name *</label>
            <form:input path="name" maxlength="100" required="required" data-label="Business name"/>
            <form:errors path="name" cssClass="field-error"/>
        </div>
        <div class="form-cols">
            <div class="form-row">
                <label for="email">Email *</label>
                <form:input path="email" type="email" required="required" data-label="Email"/>
                <form:errors path="email" cssClass="field-error"/>
            </div>
            <div class="form-row">
                <label for="phone">Phone *</label>
                <form:input path="phone" maxlength="10" required="required" data-label="Phone"
                            data-pattern="[6-9][0-9]{9}" data-pattern-msg="Enter a 10 digit mobile number"/>
                <form:errors path="phone" cssClass="field-error"/>
            </div>
            <div class="form-row">
                <label for="city">City</label>
                <form:input path="city" maxlength="60"/>
            </div>
            <div class="form-row">
                <label for="gstin">GSTIN</label>
                <form:input path="gstin" maxlength="15" data-label="GSTIN"
                            data-pattern="[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][A-Z0-9]Z[A-Z0-9]"
                            data-pattern-msg="GSTIN should look like 27AAACK1234A1Z5"/>
                <span class="hint">15 characters, capital letters</span>
                <form:errors path="gstin" cssClass="field-error"/>
            </div>
        </div>
        <button class="btn btn-primary">Save</button>
        <a class="btn" href="${ctx}/sellers">Cancel</a>
    </form:form>
</div>

<%@ include file="../common/footer.jspf" %>
