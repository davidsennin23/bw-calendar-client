<%@ taglib uri='struts-bean' prefix='bean' %>
<%@ taglib uri='struts-logic' prefix='logic' %>
<%@ taglib uri='struts-html' prefix='html' %>
<%@ taglib uri='struts-genurl' prefix='genurl' %>
<html:xhtml/>

<%@include file="/docs/header.jsp"%>

<page>categoryList</page>

<categories>
  <logic:present name="bw_categories_list" scope="session">
    <logic:iterate id="category" name="bw_categories_list" scope="session">
      <%@include file="/docs/category/emitCategory.jsp"%>
    </logic:iterate>
  </logic:present>
</categories>

<%-- categories with creators are output in the header;
     we needn't reproduce them here. --%>

<%@include file="/docs/footer.jsp"%>

