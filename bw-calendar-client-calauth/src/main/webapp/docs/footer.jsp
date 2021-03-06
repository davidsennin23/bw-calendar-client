<%@ taglib uri='struts-bean' prefix='bean' %>
<%@ taglib uri='struts-logic' prefix='logic' %>

<%
try {
%>
  <%-- Generates standard calendar values for use in the client for forms, etc --%>

  <bean:define id="calInfo" name="moduleState" property="calInfo" />
  <bean:define id="forLabels" name="moduleState"
               property="eventDates.forLabels" />


<daylabels>
    <logic:iterate id="dayLabel" name="calInfo" property="dayLabels">
      <val><bean:write name="dayLabel"/></val>
    </logic:iterate>
  </daylabels>
  <dayvalues>
    <logic:iterate id="dayVal" name="calInfo" property="dayVals">
      <val><bean:write name="dayVal"/></val>
    </logic:iterate>
    <start><bean:write name="moduleState" property="viewStartDate.day"/></start>
  </dayvalues>
  <daynames>
    <logic:iterate id="dayName" name="calInfo" property="dayNamesAdjusted">
      <val><bean:write name="dayName"/></val>
    </logic:iterate>
  </daynames>
  <shortdaynames>
    <logic:iterate id="shortDayName" name="calInfo" property="shortDayNamesAdjusted">
      <val><bean:write name="shortDayName"/></val>
    </logic:iterate>
  </shortdaynames>
  <recurdayvals>
    <logic:iterate id="recurDayName" name="calInfo" property="recurDayNamesAdjusted">
      <val><bean:write name="recurDayName"/></val>
    </logic:iterate>
  </recurdayvals>
  <monthlabels>
    <logic:iterate id="monthLabel" name="calInfo" property="monthLabels">
      <val><bean:write name="monthLabel"/></val>
    </logic:iterate>
  </monthlabels>
  <monthvalues>
    <logic:iterate id="monthVal" name="calInfo" property="monthVals">
      <val><bean:write name="monthVal"/></val>
    </logic:iterate>
    <start><bean:write name="moduleState" property="viewStartDate.month"/></start>
  </monthvalues>
  <yearvalues>
    <logic:iterate id="yearVals" name="calForm" property="yearVals">
      <val><bean:write name="yearVals"/></val>
    </logic:iterate>
    <start><bean:write name="moduleState" property="viewStartDate.year"/></start>
  </yearvalues>
  <hourlabels>
    <logic:iterate id="hourLabel" name="forLabels" property="hourLabels">
      <val><bean:write name="hourLabel"/></val>
    </logic:iterate>
  </hourlabels>
  <hourvalues>
    <logic:iterate id="hourVal" name="forLabels" property="hourVals">
      <val><bean:write name="hourVal"/></val>
    </logic:iterate>
    <start><bean:write name="moduleState" property="viewStartDate.hour"/></start>
  </hourvalues>
  <minvalues>
    <logic:iterate id="minuteVals" name="forLabels" property="minuteLabels">
      <val><bean:write name="minuteVals"/></val>
    </logic:iterate>
    <start><bean:write name="moduleState" property="viewStartDate.minute"/></start>
<%--    <logic:iterate id="minuteVals" name="forLabels" property="minuteLabels">
 --%>
  </minvalues>
  <ampmvalues>
    <logic:iterate id="ampmVals" name="forLabels" property="ampmLabels">
      <val><bean:write name="ampmVals"/></val>
    </logic:iterate>
    <start><bean:write name="moduleState" property="viewStartDate.ampm"/></start>
  </ampmvalues>
<%
} catch (Throwable t) {
  t.printStackTrace();
}
%>

<%-- Required to force write in portal-struts bridge --%>
<% pageContext.getOut().flush(); %>

