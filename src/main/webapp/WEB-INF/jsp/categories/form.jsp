<%@ include file="../common/taglibs.jspf" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="${empty category.id ? 'Add category' : 'Edit category'}"/>
<%@ include file="../common/header.jspf" %>

<h1>${pageTitle}</h1>

<div class="card form-narrow">
    <form:form modelAttribute="category" action="${ctx}/categories/save" method="post" cssClass="js-validate" novalidate="novalidate">
        <form:hidden path="id"/>
        <div class="form-row">
            <label for="name">Name *</label>
            <form:input path="name" maxlength="60" required="required" data-label="Name"/>
            <form:errors path="name" cssClass="field-error"/>
        </div>
        <div class="form-row">
            <label for="description">Description</label>
            <form:textarea path="description" rows="3" maxlength="255"/>
            <form:errors path="description" cssClass="field-error"/>
        </div>
        <button class="btn btn-primary">Save</button>
        <a class="btn" href="${ctx}/categories">Cancel</a>
    </form:form>
</div>

<%@ include file="../common/footer.jspf" %>
