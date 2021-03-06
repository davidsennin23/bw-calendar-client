<%@ page contentType="text/xml;charset=UTF-8" language="java" %>
<%@ taglib uri='struts-bean' prefix='bean' %>
<%@ taglib uri='struts-logic' prefix='logic' %>
<%@ taglib uri='struts-genurl' prefix='genurl' %>
<%@ taglib uri='bedework' prefix='bw' %>
<%
try {
%>

  <bean:define id="bwconfig" name="calForm" property="config" toScope="session" />
  <bean:define id="moduleState" name="bw_module_state" scope="request" />
  <bw:emitText name="calForm" property="calSuiteName" />

  <logic:present name="calForm" property="currentCalSuite" >
    <currentCalSuite>
      <bw:emitText name="calForm" property="currentCalSuite.name" tagName="name" />
      <bw:emitText name="calForm" property="currentCalSuite.group.account" tagName="group" />
      <bw:emitText name="calForm" property="currentCalSuite.group.principalRef" tagName="groupHref" />
      <bw:emitText name="calForm" property="currentCalSuite.resourcesHome" tagName="resourcesHome" />
      <bw:emitCurrentPrivs name="calForm" property="currentCalSuite.currentAccess" tagName="currentAccess"/>
    </currentCalSuite>
  </logic:present>

  <now><%-- The actual date right "now" - this may not be the same as currentdate --%>
    <bean:define id="fmtnow" name="calForm" property="today.formatted" />
    <date><bean:write name="fmtnow" property="date"/></date><%--
      Value: YYYYMMDD --%>
    <longdate><bean:write name="fmtnow" property="longDateString"/></longdate><%--
      Value (example): February 8, 2004 - long representation of the date --%>
    <shortdate><bean:write name="fmtnow" property="dateString"/></shortdate><%--
      Value (example): 2/8/04 - short representation of the date --%>
    <time><bean:write name="fmtnow" property="timeString"/></time><%--
      Value (example): 10:15 PM --%>
    <twodigithour24><bean:write name="fmtnow" property="twoDigitHour24"/></twodigithour24>
    <utc><bean:write name="calForm" property="today.utcDate" /></utc>
    <bw:emitText name="calForm" property="defaultTzid" />
  </now>
  <bean:define id="ctView" name="moduleState" property="curTimeView"/>
  <bean:define id="curDayFmt" name="moduleState" property="curDayFmt"/>
  <currentdate><%-- The current user-selected date --%>
    <date><bean:write name="curDayFmt" 
                      property="dateDigits"/></date><%--
      Value: yyyymmdd - date value --%>
    <longdate><bean:write name="curDayFmt"
                          property="fullDateString"/></longdate><%--
      Value (example): Wednesday, February 11, 2004 --%>
    <shortdate><bean:write name="curDayFmt" 
                           property="shortDateString"/></shortdate><%--
      Value (example): 2/8/04 - short representation of the date --%>
    <monthname><bean:write name="curDayFmt" 
                           property="monthName"/></monthname><%--
      Value (example): January - full month name --%>
  </currentdate>
  <bean:define id="firstDayFmt" name="moduleState" property="firstDayFmt"/>
  <firstday><%-- The first date appearing in the currently selected time period --%>
    <date><bean:write name="firstDayFmt" 
                      property="dateDigits"/></date><%--
      Value: yyyymmdd - date value --%>
    <longdate><bean:write name="firstDayFmt"
                          property="fullDateString"/></longdate><%--
      Value (example): Wednesday, February 11, 2004 --%>
    <shortdate><bean:write name="firstDayFmt" 
                           property="shortDateString"/></shortdate><%--
      Value (example): 2/8/04 - short representation of the date --%>
    <monthname><bean:write name="firstDayFmt" 
                           property="monthName"/></monthname><%--
      Value (example): January - full month name --%>
  </firstday>
  <bean:define id="lastDayFmt" name="moduleState" property="lastDayFmt"/>
  <lastday><%-- The last date appearing in the currently selected time period --%>
    <date><bean:write name="lastDayFmt" 
                      property="dateDigits"/></date><%--
      Value: yyyymmdd - date value --%>
    <longdate><bean:write name="lastDayFmt"
                          property="fullDateString"/></longdate><%--
      Value (example): Wednesday, February 11, 2004 --%>
    <shortdate><bean:write name="lastDayFmt" 
                           property="shortDateString"/></shortdate><%--
      Value (example): 2/8/04 - short representation of the date --%>
    <monthname><bean:write name="lastDayFmt" 
                           property="monthName"/></monthname><%--
      Value (example): January - full month name --%>
  </lastday>
  <previousdate><bean:write name="ctView" property="prevDate"/></previousdate><%--
    Value: YYYYMMDD - The previous "firstdate" in the selected time period  --%>
  <nextdate><bean:write name="ctView" property="nextDate"/></nextdate><%--
    Value: YYYYMMDD - The next "firstdate" in the selected time period --%>
  <periodname><bean:write name="ctView" property="periodName"/></periodname><%--
    Values: Day, Week, Month, Year - The current time period name.   --%>
  <multiday><bean:write name="ctView" property="multiDay"/></multiday><%--
    Values: true, false - Flag if we are viewing multiple days --%>
  <bw:emitText name="calForm" property="hour24" /><%--
    Values: true, false - Flag if we are using 24 hour time --%>

  <publicview><bean:write name="calForm" property="publicView" /></publicview><%--
    Values: true, false - Flag if we are in the guest (public) view  --%>
  <guest><bean:write name="calForm" property="guest" /></guest><%--
    Value: true, false - Flag if we are a guest --%>
  <logic:equal name="calForm" property="guest" value="false">
    <userid><bean:write name="calForm" property="currentUser" /></userid><%--
      Value: string - Userid of non-guest user --%>
  </logic:equal>

  <logic:iterate id="msg" name="calForm" property="msg.msgList">
    <message>
      <id><bean:write name="msg" property="msgId" /></id>
      <logic:iterate id="param" name="msg" property="params" >
        <param><bean:write name="param" /></param>
      </logic:iterate>
    </message>
  </logic:iterate>

  <logic:iterate id="errBean" name="calForm" property="err.msgList">
    <error>
      <id><bean:write name="errBean" property="msgId" /></id>
      <logic:iterate id="param" name="errBean" property="params" >
        <param><bean:write name="param" /></param>
      </logic:iterate>
    </error>
  </logic:iterate>

  <bean:define id="presentationState"
               name="bw_presentationstate" scope="request" />
  <bw:emitText name="presentationState" property="appRoot" tagName="appRoot" /><%--
        Value: URI - the location of web resources used by the code to find the
        XSLT files.  This element is defined prior to build in
        ../../../../clones/democal.properties
        as pubevents.app.root and personal.app.root. Note that references to
        html web resources such as images are set in the xsl stylesheets. --%>
  <bw:emitText name="presentationState" property="browserResourceRoot"/>
  <urlprefix><bean:write name="calForm" property="urlPrefix"/></urlprefix><%--
        Value: URI - this is prefix of the calendar application.
        e.g. http://localhost:8080/cal
        Use this value to prefix calls to the application actions in your XSLT.
        e.g. <a href="{$urlPrefix}/eventView.do?guid=...">View Event</a> --%>
  <urlpattern><genurl:rewrite action="DUMMYACTION.DO" /></urlpattern>

  <%-- URLs of other Bedework web clients --%>
  <personaluri><bean:write name="bwconfig" property="personalCalendarUri"/></personaluri>
  <publicuri><bean:write name="bwconfig" property="publicCalendarUri"/></publicuri>
  <adminuri><bean:write name="bwconfig" property="publicAdminUri"/></adminuri>

  <!-- Path to calendar for public event submissions -->
  <submissionsRoot>
    <encoded><bean:write name="calForm" property="encodedSubmissionRoot"/></encoded>
    <unencoded><bean:write name="bwconfig" property="submissionRoot"/></unencoded>
  </submissionsRoot>

  <urlPrefixes>
    <%-- Use URL prefixes when writing hyperlinks; these use the "genurl"
       struts tag to correctly build up application links within the
       container. "b=de" in the query string of each prefix has no meaning to
       the application and is not processed: it ensures that if we need to
       append the query string, we can always begin with an ampersand. --%>

    <%-- Public and personal client URLs --%>
    <setup><bw:rewrite actionURL="true" action="setup.do?b=de"/></setup>

    <main>
      <initialise><genurl:rewrite forward="initialise"/></initialise>
      <setSelection><bw:rewrite actionURL="true" page="/main/setSelection.do?b=de"/></setSelection>
      <setViewPeriod><bw:rewrite actionURL="true" page="/main/setViewPeriod.do?b=de"/></setViewPeriod>
      <listEvents><bw:rewrite actionURL="true" page="/main/listEvents.do?b=de"/></listEvents>
      <showPage><bw:rewrite actionURL="true" page="/main/showPage.do?b=de"/></showPage>
    </main>

    <event>
      <eventMore><genurl:rewrite forward="eventMore"/></eventMore>
      <eventView><bw:rewrite actionURL="true" page="/event/eventView.do?b=de"/></eventView>
      <addEventRef><bw:rewrite actionURL="true" page="/event/addEventRef.do?b=de"/></addEventRef>
    </event>

      <%--
    <calendar>
      <fetchPublicCalendars><bw:rewrite actionURL="true" page="/calendar/fetchPublicCalendars.do?b=de"/></fetchPublicCalendars>
      <fetchCalendars><bw:rewrite actionURL="true" page="/calendar/fetchCalendars.do?b=de"/></fetchCalendars>
      <fetchForExport><bw:rewrite actionURL="true" page="/calendar/fetchForExport.do?b=de"/></fetchForExport>
    </calendar>
    --%>

    <search>
      <search><bw:rewrite renderURL="true" page="/search/search.rdo?b=de"/></search>
      <next><bw:rewrite actionURL="true" page="/search/next.do?b=de"/></next>
    </search>

    <misc>
      <export><bw:rewrite resourceURL="true" page="/misc/export.gdo?b=de"/></export>
      <showPage><bw:rewrite renderURL="true" page="/misc/showPage.rdo?b=de"/></showPage>
    </misc>

    <mail>
      <mailEvent><bw:rewrite actionURL="true" page="/mail/mailEvent.do?b=de"/></mailEvent>
    </mail>

    <stats>
      <stats><bw:rewrite actionURL="true" page="/stats/stats.do?b=de"/></stats>
    </stats>

    <%-- The following URLs are used only in the personal client --%>
    <%-- ======================================================= --%>
    <logic:equal name="calForm" property="guest" value="false">
      <event>
        <initEvent><bw:rewrite actionURL="true" page="/event/initEvent.do?b=de"/></initEvent>
        <initPendingEvents><bw:rewrite actionURL="true" page="/event/initPendingEvents.do?b=de"/></initPendingEvents>
        <addEvent><bw:rewrite actionURL="true" page="/event/addEvent.do?b=de"/></addEvent>
        <attendeesForEvent><bw:rewrite actionURL="true" page="/event/attendeesForEvent.do?b=de"/></attendeesForEvent>
        <showAttendeesForEvent><bw:rewrite renderURL="true" page="/event/showAttendeesForEvent.rdo?b=de"/></showAttendeesForEvent>
        <initMeeting><bw:rewrite actionURL="true" page="/event/initMeeting.do?b=de"/></initMeeting>
        <editEvent><bw:rewrite actionURL="true" page="/event/editEvent.do?b=de"/></editEvent>
        <gotoEditEvent><bw:rewrite actionURL="true" page="/event/gotoEditEvent.do?b=de"/></gotoEditEvent>
        <updateEvent><bw:rewrite actionURL="true" page="/event/updateEvent.do?b=de"/></updateEvent>
        <delEvent><bw:rewrite actionURL="true" page="/event/delEvent.do?b=de"/></delEvent>
        <delInboxEvent><bw:rewrite actionURL="true" page="/event/delInboxEvent.do?b=de"/></delInboxEvent>
        <showAccess><bw:rewrite renderURL="true" page="/event/showAccess.rdo?b=de"/></showAccess>
        <addEventRefComplete><bw:rewrite actionURL="true" page="/event/addEventRefComplete.do?b=de"/></addEventRefComplete>
        <selectCalForEvent><bw:rewrite resourceURL="true" page="/event/selectCalForEvent.gdo?b=de"/></selectCalForEvent>
      </event>

      <schedule>
        <showInbox><bw:rewrite renderURL="true" page="/schedule/showInbox.rdo?b=de"/></showInbox>
        <showOutbox><bw:rewrite renderURL="true" page="/schedule/showOutbox.rdo?b=de"/></showOutbox>
        <initAttendeeRespond><bw:rewrite actionURL="true" page="/schedule/initAttendeeRespond.do?b=de"/></initAttendeeRespond>
        <attendeeRespond><bw:rewrite actionURL="true" page="/schedule/attendeeRespond.do?b=de"/></attendeeRespond>
        <initAttendeeReply><bw:rewrite actionURL="true" page="/schedule/initAttendeeReply.do?b=de"/></initAttendeeReply>
        <initAttendeeUpdate><bw:rewrite actionURL="true" page="/schedule/initAttendeeUpdate.do?b=de"/></initAttendeeUpdate>
        <processAttendeeReply><bw:rewrite actionURL="true" page="/schedule/processAttendeeReply.do?b=de"/></processAttendeeReply>
        <clearReply><bw:rewrite actionURL="true" page="/schedule/clearReply.do?b=de"/></clearReply>
        <processRefresh><bw:rewrite actionURL="true" page="/schedule/processRefresh.do?b=de"/></processRefresh>
        <refresh><bw:rewrite actionURL="true" page="/schedule/refresh.do?b=de"/></refresh>
      </schedule>

      <freeBusy>
        <fetch><bw:rewrite actionURL="true" page="/freeBusy/getFreeBusy.do?b=de"/></fetch>
      </freeBusy>

      <calendar>
        <fetch><bw:rewrite renderURL="true" page="/calendar/showUpdateList.rdo?b=de"/></fetch>
        <fetchDescriptions><bw:rewrite renderURL="true" page="/calendar/showDescriptionList.rdo?b=de"/></fetchDescriptions>
        <initAdd><bw:rewrite actionURL="true" page="/calendar/initAdd.do?b=de"/></initAdd>
        <delete><bw:rewrite actionURL="true" page="/calendar/delete.do?b=de"/></delete>
        <fetchForDisplay><bw:rewrite actionURL="true" page="/calendar/fetchForDisplay.do?b=de"/></fetchForDisplay>
        <fetchForUpdate><bw:rewrite actionURL="true" page="/calendar/fetchForUpdate.do?b=de"/></fetchForUpdate>
        <update><bw:rewrite actionURL="true" page="/calendar/update.do?b=de"/></update>
        <listForExport><bw:rewrite renderURL="true" page="/calendar/listForExport.rdo?b=de"/></listForExport>
        <setPropsInGrid><bw:rewrite actionURL="true" page="/calendar/setPropsInGrid.do?b=de"/></setPropsInGrid>
        <setPropsInList><bw:rewrite actionURL="true" page="/calendar/setPropsInList.do?b=de"/></setPropsInList>
      </calendar>

      <category>
        <initAdd><bw:rewrite actionURL="true" page="/category/initAdd.do?b=de"/></initAdd>
        <initUpdate><bw:rewrite actionURL="true" page="/category/initUpdate.do?b=de"/></initUpdate>
        <fetchForUpdate><bw:rewrite actionURL="true" page="/category/fetchForUpdate.do?b=de"/></fetchForUpdate>
        <update><bw:rewrite actionURL="true" page="/category/update.do?b=de"/></update>
        <delete><bw:rewrite actionURL="true" page="/category/delete.do?b=de"/></delete>
      </category>

      <location>
        <initAdd><bw:rewrite actionURL="true" page="/location/initAdd.do?b=de"/></initAdd>
        <initUpdate><bw:rewrite actionURL="true" page="/location/initUpdate.do?b=de"/></initUpdate>
        <fetchForUpdate><bw:rewrite actionURL="true" page="/location/fetchForUpdate.do?b=de"/></fetchForUpdate>
        <update><bw:rewrite actionURL="true" page="/location/update.do?b=de"/></update>
        <delete><bw:rewrite actionURL="true" page="/location/delete.do?b=de"/></delete>
      </location>

      <prefs>
        <fetchForUpdate><bw:rewrite actionURL="true" page="/prefs/fetchForUpdate.do?b=de"/></fetchForUpdate>
        <update><bw:rewrite actionURL="true" page="/prefs/update.do?b=de"/></update>
        <fetchSchedulingForUpdate><bw:rewrite actionURL="true" page="/prefs/fetchSchedulingForUpdate.do?b=de"/></fetchSchedulingForUpdate>
        <updateSchedulingPrefs><bw:rewrite actionURL="true" page="/prefs/updateSchedulingPrefs.do?b=de"/></updateSchedulingPrefs>
      </prefs>

      <misc>
        <upload><bw:rewrite actionURL="true" page="/misc/upload.do?b=de"/></upload>
        <initUpload><bw:rewrite renderURL="true" page="/misc/initUpload.rdo?b=de"/></initUpload>
      </misc>

      <alarm>
        <initEventAlarm><bw:rewrite actionURL="true" page="/alarm/initEventAlarm.do?b=de"/></initEventAlarm>
        <setAlarm><bw:rewrite actionURL="true" page="/alarm/setAlarm.do?b=de"/></setAlarm>
      </alarm>

    </logic:equal>
  </urlPrefixes>

  <confirmationid><bean:write name="calForm" property="confirmationId"/></confirmationid><%--
        Value: String - a 16 character random string used to allow users to confirm
        additions to their private calendar.  DEPRECATED. --%>

  <logic:iterate id="appvar" name="calForm" property="appVars">
    <appvar><%--
        Application variables can be set arbitrarily by the stylesheet designer.
        Use an "appvar" by adding setappvar=key(value) to the query string of
        a URL.  This feature is useful for setting up state during a user's session.
        e.g. <a href="{$urlPrefix}/eventView.do?guid=...&setappvar=currentTab(event)">View Event</a>
        To change the value of an appvar, call the same key with a different value.
        e.g. <a href="{$urlPrefix}/setup.do?setappvar=currentTab(home)">Return Home</a>
        If appvars exist, they will be output in the following form:  --%>
      <key><bean:write name="appvar" property="key" /></key>
      <value><bean:write name="appvar" property="value" /></value>

      <logic:equal name="appvar" property="key" value="summaryMode"><%--
        This is a special use of the appvar feature.  Normally, we don't return
        all details about events except when we display a single event (to keep the
        XML lighter).  To return all event details in an events listing, append a
        query string with setappvar=summaryMode(details).  Turn the detailed view
        off with setappvar=summaryMode(summary).--%>
        <logic:equal name="appvar" property="value" value="details">
          <bean:define id="detailView" value="true" toScope="request"/><%--
            Send this bean to the request scope so we can test for it on the page
            that builds the calendar tree (main.jsp) --%>
        </logic:equal>
      </logic:equal>
    </appvar>
  </logic:iterate>

<logic:present name="bw_feature_flags" scope="session" >
    <featureFlags><bean:write name="bw_feature_flags" scope="session" /></featureFlags>
</logic:present>

  <%-- System parameters --%>
  <syspars>
    <logic:present name="calForm" property="dirInfo" >
      <bean:define id="dir" name="calForm" property="dirInfo" />
      <bw:emitText name="dir" property="userPrincipalRoot" />
      <bw:emitText name="dir" property="groupPrincipalRoot" />
      <bw:emitText name="dir" property="ticketPrincipalRoot" />
      <bw:emitText name="dir" property="resourcePrincipalRoot" />
      <bw:emitText name="dir" property="hostPrincipalRoot" />
      <bw:emitText name="dir" property="venuePrincipalRoot" />
    </logic:present>
  </syspars>

  <myCalendars>
    <jsp:include page="/docs/calendar/emitCalendars.jsp"/>
  </myCalendars>

<%
} catch (Throwable t) {
  t.printStackTrace();
}
%>

