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
package org.bedework.webcommon.misc;

import org.bedework.appcommon.ClientError;
import org.bedework.appcommon.client.Client;
import org.bedework.calfacade.BwAttendee;
import org.bedework.webcommon.Attendees;
import org.bedework.webcommon.BwAbstractAction;
import org.bedework.webcommon.BwActionFormBase;
import org.bedework.webcommon.BwRequest;

import edu.rpi.cmt.calendar.IcalDefs;

import net.fortuna.ical4j.model.parameter.Role;

/**
 * Action to fetch free busy information for web use.
 * <p>Request parameters - all optional:<ul>
 *      <li>  userid:   whose free busy we want - default to current user</li>.
 *      <li>  subname:  name of the subscription - default to subscriptions
 *                      specified by user</li>.
 *      <li>  start:    start of period - default to beginning of this week</li>.
 *      <li>  end:      end of period - default to end of this week</li>.
 *      <li>  interval: default entire period or a multiplier</li>.
 *      <li>  intunit:  default to hours, "minutes", "hours, "days", "weeks"
 *                      "months"</li>.
 * </ul>
 * <p>e.g interval=30 and intunit="minutes" means half hour intervals
 * <p>Forwards to:<ul>
 *      <li>"noAction"     input error or we want to ignore the request.</li>
 *      <li>"noAccess"     No acccess to free busy</li>
 *      <li>"notFound"     event not found.</li>
 *      <li>"error"        input error - correct and retry.</li>
 *      <li>"success"      fetched OK.</li>
 * </ul>
 *
 * <p>If no period is given return this week. If no interval and intunit is
 * supplied default to 1 hour intervals during the workday.
 *
 * @author Mike Douglass douglm @ rpi.edu
 */
public class FreeBusyAction extends BwAbstractAction {
  /* (non-Javadoc)
   * @see org.bedework.webcommon.BwAbstractAction#doAction(org.bedework.webcommon.BwRequest, org.bedework.webcommon.BwActionFormBase)
   */
  @Override
  public int doAction(final BwRequest request,
                      final BwActionFormBase form) throws Throwable {
    String uri = null;
    Client cl = request.getClient();

    gotoDateView(form, form.getDate(), form.getViewTypeI());

    String userId = request.getReqPar("userid");

    if (userId != null) {
      uri = cl.getCalendarAddress(userId);
    } else if (!form.getGuest()) {
      uri = cl.getCurrentCalendarAddress();
    }

    if (uri == null) {
      form.getErr().emit(ClientError.unknownUser);
      return forwardNotFound;
    }

    String st = request.getReqPar("start");
    String et = request.getReqPar("end");
    String intunitStr = request.getReqPar("intunit");
    int interval = request.getIntReqPar("interval", 1);

    // Make user an attendee
    Attendees atts = new Attendees();
    atts.addRecipient(uri);

    BwAttendee att = new BwAttendee();

    att.setAttendeeUri(uri);
    att.setRole(Role.CHAIR.getValue());
    att.setPartstat(IcalDefs.partstatValAccepted);
    atts.addAttendee(att);

    return doFreeBusy(request, form, atts, st, et, intunitStr, interval);
  }
}
