<%@ page contentType="text/xml;charset=UTF-8" buffer="none" language="java" %><?xml version="1.0" encoding="UTF-8"?>
<%@ taglib uri='struts-bean' prefix='bean' %>
<%@ taglib uri='struts-logic' prefix='logic' %>
<%@ taglib uri='struts-html' prefix='html' %>
<%@ taglib uri='struts-genurl' prefix='genurl' %>
<%@ taglib uri='bedework' prefix='bw' %>
<html:xhtml/>

<bedework>
<%@include file="/docs/header.jsp"%>

<% /*  the same as calendarList.jsp, but will be treated differently  */ %>
<page>calendarDescriptions</page>

<%@include file="/docs/calendar/emitPublicCalendars.jsp"%>

<%@include file="/docs/footer.jsp"%>
</bedework>
