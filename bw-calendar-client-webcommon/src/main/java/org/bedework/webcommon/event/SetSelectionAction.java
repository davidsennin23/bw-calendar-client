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

import org.bedework.appcommon.BedeworkDefs;
import org.bedework.appcommon.ClientError;
import org.bedework.appcommon.client.Client;
import org.bedework.calfacade.svc.BwView;
import org.bedework.webcommon.BwAbstractAction;
import org.bedework.webcommon.BwActionFormBase;
import org.bedework.webcommon.BwRequest;
import org.bedework.appcommon.client.EventListPars;

import java.util.List;

/**
 * Action to select what will be displayed. If no request par values are found
 * will switch to default view.
 *
 * <p>Request parameters<ul>
 *      <li>"viewName"       Use named view</li>
 *      <li>"calUrl"         URL of calendar</li>
 * </ul>
 * <p>Forwards to:<ul>
 *      <li>forwardNoAccess       user not authorised.</li>
 *      <li>forwardNotFound       no calendar or view was found.</li>
 *      <li>forwardNoViewDefined  no view defined</li>
 *      <li>forwardSuccess        selected ok.</li>
 * </ul>
 */
public class SetSelectionAction extends BwAbstractAction {
  /* (non-Javadoc)
   * @see org.bedework.webcommon.BwAbstractAction#doAction(org.bedework.webcommon.BwRequest, org.bedework.webcommon.BwActionFormBase)
   */
  @Override
  public int doAction(final BwRequest request,
                      final BwActionFormBase form) throws Throwable {
    /* Set up the event list parameters */

    EventListPars elpars = new EventListPars();
    Client cl = request.getClient();

    int forward = setEventListPars(request, elpars);
    if (forward != forwardSuccess) {
      return forward;
    }

    elpars.setForExport(request.getBooleanReqPar("forExport", false));
    elpars.setUseDbSearch(request.getBooleanReqPar("useDbSearch", false));

    cl.setEventListPars(elpars);

    forward = tryCal(request, form);

    if (forward == forwardNoAction) {
      forward = doView(request, form);
    }

    if (elpars.getFromDate() != null) {
      gotoDateView(request,
                   elpars.getFromDate().getDtval(),
                   form.getViewTypeI());
    }

    return forward;
  }

  /* Try for a calendar url. Return with forward or null for not found.
   */
  private int tryCal(final BwRequest request,
                     final BwActionFormBase form) throws Throwable {
    String vpath = request.getReqPar("virtualPath");

    if (vpath == null) {
      return forwardNoAction;
    }

    if (!request.getClient().setVirtualPath(vpath)) {
      form.getErr().emit(ClientError.badVpath, vpath);
      return forwardNoAction;
    }

    form.setSelectionType(BedeworkDefs.selectionTypeCollections);

    form.refreshIsNeeded();
    return forwardSuccess;
  }

  /* Do the view thing. This is the default action
   */
  private int doView(final BwRequest request,
                     final BwActionFormBase form) throws Throwable {
    List<String> vpaths = request.getReqPars("vpath");
    if (vpaths != null) {
      BwView view = new BwView();

      view.setName("--temp--");
      view.setCollectionPaths(vpaths);
      view.setConjunction(request.getBooleanReqPar("conjunction", false));
      request.getClient().setCurrentView(view);

      form.setSelectionType(BedeworkDefs.selectionTypeView);
      form.refreshIsNeeded();

      return forwardSuccess;
    }

    if (!setView(request, request.getReqPar("viewName"))) {
      return forwardNoViewDefined;
    }

    return forwardSuccess;
  }
}
