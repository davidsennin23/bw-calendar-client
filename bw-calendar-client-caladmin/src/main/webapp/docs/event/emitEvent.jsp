<%@ taglib uri='struts-logic' prefix='logic' %>
<%@ taglib uri='bedework' prefix='bw' %>

<bean:define id="eventFmt" name="eventFormatter"/>
<bean:define id="eventInfo" name="eventFmt" property="eventInfo" toScope="request"  />
<bean:define id="event" name="eventInfo" property="event" toScope="request"  />
<%-- Output a single event. This page handles fields common to all views --%>
<%@ include file="/docs/event/emitEventCommon.jsp" %>



