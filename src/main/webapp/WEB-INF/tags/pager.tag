<%@ tag pageEncoding="UTF-8" %>
<%@ attribute name="page" required="true" type="org.springframework.data.domain.Page" %>
<%@ attribute name="base" required="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- base already contains the filter + sort params, we only add &page=n --%>
<c:if test="${page.totalPages > 1}">
    <div class="pager">
        <c:choose>
            <c:when test="${page.first}"><span class="disabled">&laquo; Prev</span></c:when>
            <c:otherwise><a href="${base}&page=${page.number - 1}">&laquo; Prev</a></c:otherwise>
        </c:choose>
        <c:forEach begin="0" end="${page.totalPages - 1}" var="i">
            <c:choose>
                <c:when test="${i == page.number}"><span class="current">${i + 1}</span></c:when>
                <c:otherwise><a href="${base}&page=${i}">${i + 1}</a></c:otherwise>
            </c:choose>
        </c:forEach>
        <c:choose>
            <c:when test="${page.last}"><span class="disabled">Next &raquo;</span></c:when>
            <c:otherwise><a href="${base}&page=${page.number + 1}">Next &raquo;</a></c:otherwise>
        </c:choose>
    </div>
</c:if>
<p class="muted small">Showing ${page.numberOfElements} of ${page.totalElements} record(s)</p>
