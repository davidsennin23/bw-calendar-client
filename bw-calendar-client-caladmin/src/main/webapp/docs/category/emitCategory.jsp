<%@ taglib uri='struts-bean' prefix='bean' %>
<%@ taglib uri='struts-logic' prefix='logic' %>

<category>
  <%-- unique id  --%>
  <bw:emitText name="category" property="uid" />
  <bw:emitText name="category" property="href" />
  <%-- text value of the category --%>
  <logic:present name="category" property="word" >
    <bw:emitText name="category" property="word.value" tagName="value" />
    <bw:emitText name="category" property="word.lang" tagName="status" />
  </logic:present>
  <logic:notPresent name="category" property="word" >
    <value></value>
    <status></status>
  </logic:notPresent>
  <logic:present name="category" property="colPath" >
    <bw:emitText name="category" property="colPath" />
  </logic:present>
  <logic:present name="category" property="name" >
    <bw:emitText name="category" property="name" />
  </logic:present>
  <%-- description of the category  --%>
    <logic:present name="category" property="descriptionVal" >
      <bw:emitText name="category" property="descriptionVal" tagName="description" />
    </logic:present>
    <%-- status of the category  --%>
    <logic:present name="category" property="status" >
      <bw:emitText name="category" property="status" tagName="status" />
    </logic:present>
  <%-- creator of the category  --%>
  <bw:emitText name="category" property="creatorHref" tagName="creator" />
</category>

