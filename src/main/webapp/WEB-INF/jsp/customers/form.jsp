<%@ include file="../common/taglibs.jspf" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="${empty customer.id ? 'Register customer' : 'Edit customer'}"/>
<%@ include file="../common/header.jspf" %>

<h1>${pageTitle}</h1>

<div class="card form-narrow">
    <form:form modelAttribute="customer" action="${ctx}/customers/save" method="post" cssClass="js-validate" novalidate="novalidate">
        <form:hidden path="id"/>
        <div class="form-cols">
            <div class="form-row">
                <label for="firstName">First name *</label>
                <form:input path="firstName" maxlength="50" required="required" data-label="First name"/>
                <form:errors path="firstName" cssClass="field-error"/>
            </div>
            <div class="form-row">
                <label for="lastName">Last name *</label>
                <form:input path="lastName" maxlength="50" required="required" data-label="Last name"/>
                <form:errors path="lastName" cssClass="field-error"/>
            </div>
            <div class="form-row">
                <label for="email">Email *</label>
                <form:input path="email" type="email" required="required" data-label="Email"
                            data-check-url="${ctx}/api/customers/email-available" data-id="${customer.id}"/>
                <form:errors path="email" cssClass="field-error"/>
            </div>
            <div class="form-row">
                <label for="phone">Mobile *</label>
                <form:input path="phone" maxlength="10" required="required" data-label="Mobile"
                            data-pattern="[6-9][0-9]{9}" data-pattern-msg="Enter a 10 digit mobile number"/>
                <form:errors path="phone" cssClass="field-error"/>
            </div>
        </div>
        <div class="form-row">
            <label for="address">Address *</label>
            <form:input path="address" maxlength="255" required="required" data-label="Address"/>
            <form:errors path="address" cssClass="field-error"/>
        </div>
        <div class="form-cols">
            <div class="form-row">
                <label for="city">City *</label>
                <form:input path="city" maxlength="60" required="required" data-label="City"/>
                <form:errors path="city" cssClass="field-error"/>
            </div>
            <div class="form-row">
                <label for="pincode">Pincode *</label>
                <form:input path="pincode" maxlength="6" required="required" data-label="Pincode"
                            data-pattern="[0-9]{6}" data-pattern-msg="Pincode must be 6 digits"/>
                <form:errors path="pincode" cssClass="field-error"/>
            </div>
        </div>
        <button class="btn btn-primary">Save</button>
        <a class="btn" href="${ctx}/customers">Cancel</a>
    </form:form>
</div>

<%@ include file="../common/footer.jspf" %>
