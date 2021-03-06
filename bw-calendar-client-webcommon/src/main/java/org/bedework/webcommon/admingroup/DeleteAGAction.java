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
package org.bedework.webcommon.admingroup;

import org.bedework.appcommon.ClientError;
import org.bedework.appcommon.ClientMessage;
import org.bedework.appcommon.client.Client;
import org.bedework.calfacade.svc.BwAdminGroup;
import org.bedework.calfacade.svc.wrappers.BwCalSuiteWrapper;
import org.bedework.webcommon.BwAbstractAction;
import org.bedework.webcommon.BwActionFormBase;
import org.bedework.webcommon.BwRequest;

/** This action deletes an admin group
 *
 * <p>Forwards to:<ul>
 *      <li>forwardNoAccess     user not authorised.</li>
 *      <li>forwardNotFound     no such event.</li>
 *      <li>forwardContinue     continue on to update page.</li>
 * </ul>
 *
 * @author Mike Douglass   douglm rpi.edu
 */
public class DeleteAGAction extends BwAbstractAction {
  /* (non-Javadoc)
   * @see org.bedework.webcommon.BwAbstractAction#doAction(org.bedework.webcommon.BwRequest, org.bedework.webcommon.BwActionFormBase)
   */
  public int doAction(BwRequest request,
                      BwActionFormBase form) throws Throwable {
    /** Check access
     */
    if (!form.getCurUserSuperUser()) {
      return forwardNoAccess;
    }

    BwAdminGroup updgrp = form.getUpdAdminGroup();

    if (updgrp == null) {
      // That's not right
      return forwardNotFound;
    }

    Client cl = request.getClient();

    // Ensure group is not a calendar suite admin group.
    BwCalSuiteWrapper csw = cl.getCalSuite(updgrp);

    if (csw != null) {
      // Group already assigned to another cal suite
      form.getErr().emit(ClientError.adminGroupAssignedCS, csw.getName());
      return forwardContinue;
    }

    cl.setChoosingGroup(false); // reset

    cl.removeAdminGroup(updgrp);
    form.setUpdAdminGroup(null);
    form.getMsg().emit(ClientMessage.deletedGroup);
    cl.refreshAdminGroups();

    return forwardContinue;
  }
}

