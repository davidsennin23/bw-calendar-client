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
package org.bedework.webcommon.calsuite;

import org.bedework.access.Acl;
import org.bedework.appcommon.AccessXmlUtil;
import org.bedework.appcommon.client.Client;
import org.bedework.calfacade.svc.wrappers.BwCalSuiteWrapper;
import org.bedework.webcommon.BwAbstractAction;
import org.bedework.webcommon.BwActionFormBase;
import org.bedework.webcommon.BwRequest;

/** Update a calendar suite for a user.
 *
 * <p>Parameters are:<ul>
 *      <li>"delete"                   delete current calsuite</li>
 *      </ul>or<<ul>
 *      <li>"name"            Name of calsuite to update</li>
 *      <li>"groupName"       group name for calsuite</li>
 *      <li>"calPath"         root collection path</li>
 *      <li>"subroot"         submissions root path</li>
 * </ul>
 *
 * <p>Forwards to:<ul>
 *      <li>"error"        some form of fatal error.</li>
 *      <li>"noAccess"     user not authorised.</li>
 *      <li>"retry"        try again.</li>
 *      <li>"success"      subscribed ok.</li>
 * </ul>
 *
 * @author Mike Douglass   douglm@rpi.edu
 */
public class UpdateCalSuiteAction extends BwAbstractAction {
  /* (non-Javadoc)
   * @see org.bedework.webcommon.BwAbstractAction#doAction(org.bedework.webcommon.BwRequest, org.bedework.webcommon.BwActionFormBase)
   */
  @Override
  public int doAction(final BwRequest request,
                      final BwActionFormBase form) throws Throwable {
    Client cl = request.getClient();

    if (cl.isGuest()) {
      return forwardNoAccess; // First line of defence
    }

    BwCalSuiteWrapper csw = form.getCalSuite();

    if (csw == null) {
      return forwardError;
    }

    if (request.present("delete")) {
      cl.deleteCalSuite(csw);

      return forwardSuccess;
    }

    cl.updateCalSuite(csw,
                      request.getReqPar("groupName"),
                      request.getReqPar("calPath"),
                      request.getReqPar("subroot"));

    /* -------------------------- Access ------------------------------ */

    String aclStr = request.getReqPar("acl");
    if (aclStr != null) {
      Acl acl = new AccessXmlUtil(null, cl).getAcl(aclStr, true);

      cl.changeAccess(csw, acl.getAces(), true);
    }

    return forwardSuccess;
  }
}
