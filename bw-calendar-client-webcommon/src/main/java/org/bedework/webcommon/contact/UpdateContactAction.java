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
package org.bedework.webcommon.contact;

import org.bedework.appcommon.ClientError;
import org.bedework.appcommon.ClientMessage;
import org.bedework.appcommon.client.Client;
import org.bedework.calfacade.BwContact;
import org.bedework.calfacade.BwEventProperty;
import org.bedework.webcommon.BwAbstractAction;
import org.bedework.webcommon.BwActionFormBase;
import org.bedework.webcommon.BwRequest;
import org.bedework.webcommon.BwWebUtil;
import org.bedework.webcommon.BwWebUtil.ValidateResult;

/** This action updates a contact.
 *
 * <p>Forwards to:<ul>
 *      <li>"noAccess"     user not authorised.</li>
 *      <li>"notFound"     no such event.</li>
 *      <li>"success"      update successful.</li>
 * </ul>
 *
 * @author Mike Douglass   douglm   rpi.edu
 */
public class UpdateContactAction extends BwAbstractAction {
  @Override
  public int doAction(final BwRequest request,
                      final BwActionFormBase form) throws Throwable {
    final Client cl = request.getClient();

    /* Check access
     */
    if (cl.isGuest() ||
            (cl.getPublicAdmin() && !form.getAuthorisedUser())) {
      return forwardNoAccess;
    }

    final String reqpar = request.getReqPar("delete");

    if (reqpar != null) {
      return forwardDelete;
    }

    final boolean add = form.getAddingContact();

    /* We are just updating from the current form values.
     */
    final ValidateResult vr = BwWebUtil.validateContact(form);
    if (!vr.ok) {
      return forwardRetry;
    }

    /* if a contact with the same name and creator exist
       in contact table then retrieve its categoryid, otherwise add this
       category to the database and then retrieve its categoryid.
     */

    BwContact c = form.getContact();

    if (cl.isSuperUser()) {
      final String deleted = request.getReqPar("deleted");

      if ("true".equals(deleted)) {
        c.setStatus(BwEventProperty.statusDeleted);
      }
    }

    if (add) {
      c.setPublick(cl.getPublicAdmin());
      if (!cl.findContact(c.getCn()).isNotFound()) {
        form.getErr().emit(ClientError.duplicateContact, c.getCn());
        return forwardDuplicate;
      }

      var cres = cl.ensureContactExists(c, null);
      if (!cres.isOk()) {
        return forwardRetry;
      }
    } else { // CHGTBL if (vr.changed) {
      cl.updateContact(c);
    }

    form.assignAddingContact(false);

    if (add) {
      form.getMsg().emit(ClientMessage.addedContact);
    } else { // CHGTBL if (vr.changed) {
      form.getMsg().emit(ClientMessage.updatedContact);
    }

    return forwardSuccess;
  }
}
