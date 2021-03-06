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

package org.bedework.webcommon.calendars;

import org.bedework.appcommon.client.Client;
import org.bedework.calfacade.BwCalendar;
import org.bedework.webcommon.BwAbstractAction;
import org.bedework.webcommon.BwActionFormBase;
import org.bedework.webcommon.BwRequest;
import org.bedework.webcommon.BwSession;

/** This action sets the state ready for adding a calendar.
 *
 * <p>Parameters are:<ul>
 *      <li>"calPath"       Path of the parent to be</li>
 * </ul>
 *
 * <p>Forwards to:<ul>
 *      <li>"noAccess"      user not authorised.</li>
 *      <li>"notAllowed"    cannot add a calendar to that calendar.</li>
 *      <li>"continue"      continue on to update page.</li>
 * </ul>
 *
 * @author Mike Douglass   douglm@rpi.edu
 */
public class InitAddCalendarAction extends BwAbstractAction {
  /* (non-Javadoc)
   * @see org.bedework.webcommon.BwAbstractAction#doAction(org.bedework.webcommon.BwRequest, org.bedework.webcommon.BwActionFormBase)
   */
  @Override
  public int doAction(final BwRequest request,
                      final BwActionFormBase form) throws Throwable {
    Client cl = request.getClient();

    if (cl.isGuest()) {
      return forwardNoAccess; // First line of defense
    }

    BwSession sess = request.getSess();
    BwCalendar cal = request.getCalendar(true);

    if ((cal == null) || !cal.getCollectionInfo().childrenAllowed) {
      return forwardNotAllowed;
    }

    form.setParentCalendarPath(cal.getPath());

    /* We need the synch info to get information about connectors
     */
    form.setSynchInfo(cl.getSynchInfo());

    /** Set the objects to null so we get new ones.
     */
    form.setCalendar(null);
    form.assignAddingCalendar(true);

    sess.embedCategories(request, false, BwSession.ownersEntity);

    return forwardContinue;
  }
}

