/* ********************************************************************
    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.
*/
package org.bedework.webcommon.event;

import org.bedework.appcommon.ClientError;
import org.bedework.appcommon.EventFormatter;
import org.bedework.appcommon.EventKey;
import org.bedework.appcommon.client.Client;
import org.bedework.appcommon.client.IcalCallbackcb;
import org.bedework.calfacade.BwEvent;
import org.bedework.calfacade.svc.EventInfo;
import org.bedework.icalendar.IcalTranslator;
import org.bedework.icalendar.RecurRuleComponents;
import org.bedework.util.calendar.ScheduleMethods;
import org.bedework.util.timezones.DateTimeUtil;
import org.bedework.util.timezones.Timezones;
import org.bedework.webcommon.BwActionFormBase;
import org.bedework.webcommon.BwModuleState;
import org.bedework.webcommon.BwRequest;

import java.util.Collection;
import java.util.Date;

/** Fetch an event for rendering. We previously set the key fields.
 *
 * @author Mike Douglass  douglm - rpi.edu
 */
public class RenderEventAction extends EventActionBase {
  @Override
  public int doAction(final BwRequest request,
                      final BwActionFormBase form) throws Throwable {
    /*
    if (form.getNewSession()) {
      request.refresh();
      return forwardGotomain;
    }
    */

    final BwModuleState mstate = request.getModule().getState();
    final EventKey ekey = form.getEventKey();

    if (ekey == null) {
      request.getErr().emit(ClientError.unknownEvent, "No key supplied");
      return forwardNoAction;
    }

    final EventInfo ei = findEvent(request, ekey);

    if (ei == null) {
      return forwardNoAction;
    }

    form.setEventInfo(ei, false);

    final Client cl = request.getClient();
    final BwEvent ev = ei.getEvent();

    // Not export - just set up for display

    if (ev.getRrules() != null) {
      final Collection<RecurRuleComponents> rrcs =
              RecurRuleComponents.fromEventRrules(ev);

      form.setRruleComponents(rrcs);
    } else {
      form.setRruleComponents(null);
    }

    final EventFormatter ef =
            new EventFormatter(cl,
                               new IcalTranslator(new IcalCallbackcb(cl)),
                               ei);

    form.setCurEventFmt(ef);

    /*
    gotoDateView(form,
                 ev.getEvent().getDtstart().getDate().substring(0, 8),
                 -1,
                 debug);*/

    if (ev.getScheduleMethod() != ScheduleMethods.methodTypeReply) {
      // Replies have all sorts of missing fields
      /* Only change date if current date is outside range of event. */

      final String cur = mstate.getViewMcDate().getDateDigits();

      /* Get start date in current locale */

      Date evdt = DateTimeUtil.fromISODateTimeUTC(ev.getDtstart().getDate());

      /* Get the date in the current user timezone */
      final String evst = DateTimeUtil.isoDate(evdt).substring(0, 8);

      if (debug) {
        debugMsg("******* evdt=" + evdt + " evst=" + evst +
                 " default tz=" + Timezones.getThreadDefaultTzid());
      }

      evdt = DateTimeUtil.fromISODateTimeUTC(ev.getDtend().getDate());
      final String evend = DateTimeUtil.isoDate(evdt).substring(0, 8);

      /* This doesn't seem altogether correct */
      if ((cur.compareTo(evst) < 0) ||
          (cur.compareTo(evend) > 0)) {
        setViewDate(request, evst);
      }
    }

    if (ev.getScheduleMethod() != ScheduleMethods.methodTypeNone) {
      // Assume we need the collection containing the meeting
      form.setMeetingCal(cl.getCollection(ev.getColPath()));
    }

    return forwardSuccess;
  }
}
