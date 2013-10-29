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

package org.bedework.webcommon.pref;

import org.bedework.appcommon.client.Client;
import org.bedework.calfacade.BwPreferences;
import org.bedework.webcommon.BwAbstractAction;
import org.bedework.webcommon.BwActionFormBase;
import org.bedework.webcommon.BwRequest;
import org.bedework.webcommon.BwSession;

/** Delete a view.
 *
 * <p>Parameters are:<ul>
 *      <li>"user"            User whos prefs we're changing - superuser only</li>
 * </ul>
 *
 * <p>Forwards to:<ul>
 *      <li>"error"        some form of fatal error.</li>
 *      <li>"noAccess"     user not authorised.</li>
 *      <li>"notFound"     no such user.</li>
 *      <li>"success"     continue on to update page.</li>
 * </ul>
 *
 * @author Mike Douglass   douglm@rpi.edu
 */
public class FetchPrefsAction extends BwAbstractAction {
  /* (non-Javadoc)
   * @see org.bedework.webcommon.BwAbstractAction#doAction(org.bedework.webcommon.BwRequest, org.bedework.webcommon.BwActionFormBase)
   */
  @Override
  public int doAction(final BwRequest request,
                      final BwActionFormBase form) throws Throwable {
    Client cl = request.getClient();

    request.getSess().embedCategories(request, false,
                                      BwSession.ownersEntity);
    request.getSess().embedCategories(request, true,
                                      BwSession.defaultEntity);

    String str = request.getReqPar("user");
    if (str != null) {
      /* Fetch a given users preferences */
      if (!form.getCurUserSuperUser()) {
        return forwardNoAccess; // First line of defence
      }

      BwPreferences prefs = cl.getPreferences(str);
      if (prefs == null) {
        return forwardNoAccess;
      }

      form.setUserPreferences(prefs);
    } else {
      /* Just set this users prefs */
      form.setUserPreferences(cl.getPreferences());
    }

    return forwardSuccess;
  }
}
