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
package org.bedework.webcommon;

import org.bedework.appcommon.AdminConfig;
import org.bedework.appcommon.ClientError;
import org.bedework.appcommon.client.Client;
import org.bedework.calfacade.BwGroup;
import org.bedework.calfacade.BwPrincipal;
import org.bedework.calfacade.exc.CalFacadeAccessException;
import org.bedework.calfacade.svc.BwAdminGroup;
import org.bedework.calfacade.svc.BwAuthUser;
import org.bedework.calfacade.svc.prefs.BwAuthUserPrefs;
import org.bedework.calfacade.svc.wrappers.BwCalSuiteWrapper;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.struts.Request;

import java.util.Collection;

/**
 * @author Mike Douglass
 *
 */
public class AdminUtil implements ForwardDefs {
  private static BwLogger logger =
          new BwLogger().setLoggedClass(AdminUtil.class);

  /** Called just before action.
   *
   * @param request request object
   * @return int foward index
   * @throws Throwable on fatal error
   */
  public static int actionSetup(final BwRequest request) throws Throwable {
    final BwActionFormBase form = request.getBwForm();
    final Client cl = request.getClient();

    final BwAuthUser au = cl.getAuthUser(cl.getAuthPrincipal());

    if (au == null) {
      return forwardNoAccess;
    }

    // Refresh current auth user prefs.
    final BwAuthUserPrefs prefs = au.getPrefs();

    ((BwSessionImpl)request.getSess()).setCurAuthUserPrefs(prefs);
    form.setCurAuthUserPrefs(prefs);

    if (!cl.getGroupSet()) {
      // Set default access rights.

      form.assignCurUserPublicEvents(au.isPublicEventUser());
      form.assignCurUserContentAdminUser(au.isContentAdminUser());
      form.assignCurUserApproverUser(au.isApproverUser());

      form.assignAuthorisedUser(!au.isUnauthorized());
    }

    if (logger.debug()) {
      logger.info("form.getGroupSet()=" + cl.getGroupSet());
      logger.info("-------- isSuperUser: " + form.getCurUserSuperUser());
    }

    final int temp = checkGroup(request, true);

    if (temp != forwardNoAction) {
      if (logger.debug()) {
        logger.info("form.getGroupSet()=" + cl.getGroupSet());
      }
      return temp;
    }

    if (!form.getAuthorisedUser()) {
      return forwardNoAccess;
    }

    return forwardNoAction;
  }

  /** Return no action if group is chosen else return a forward index.
   *
   * @param request   for pars
   * @param initCheck true if this is a check to see if we're initialised,
   *                  otherwise this is an explicit request to change group.
   * @return int   forward index
   * @throws Throwable on fatal error
   */
  public static int checkGroup(final BwRequest request,
                               final boolean initCheck) throws Throwable {
    final BwActionFormBase form = (BwActionFormBase)request.getForm();

    final Client cl = request.getClient();

    if (cl.getGroupSet()) {
      return forwardNoAction;
    }

    try {
      if (cl.getChoosingGroup()) {
        /* This should be the response to presenting a list of groups.
            We handle it here rather than in a separate action to ensure our
            client is not trying to bypass the group setting.
         */

        final String reqpar = request.getReqPar("adminGroupName");
        if (reqpar == null) {
          // Make them do it again.

          form.assignCalSuites(cl.getContextCalSuites());
//          request.setSessionAttr(BwRequest.bwAdminGroupsInfoName,
//                                 request.getClient().getAdminGroups());

          return forwardChooseGroup;
        }

        final BwAdminGroup adg = (BwAdminGroup)cl.findGroup(reqpar);
        if (adg == null) {
          if (logger.debug()) {
            logger.info("No user admin group with name " + reqpar);
          }

          form.assignCalSuites(cl.getContextCalSuites());
//          request.setSessionAttr(BwRequest.bwAdminGroupsInfoName,
//                                 request.getClient().getAdminGroups());
          // We require a group
          return forwardChooseGroup;
        }

        return setGroup(request, adg);
      }

      /* If the user is in no group or in one group we just go with that,
          otherwise we ask them to select the group
       */

      final Collection<BwGroup> adgs;

      final BwPrincipal p = cl.getAuthPrincipal();
      if (p == null) {
        return forwardNoAccess;
      }

      if (initCheck || !form.getCurUserSuperUser()) {
        // Always restrict to groups of which we are a member
        adgs = cl.getGroups(p);
      } else {
        adgs = cl.getAllGroups(false);
      }

      if (adgs.isEmpty()) {
        /* If we require that all users be in a group we return to an error
            page. The only exception will be superUser.
         */

        final boolean noGroupAllowed =
          ((AdminConfig)form.getConfig()).getNoGroupAllowed();
        if (cl.isSuperUser() || noGroupAllowed) {
          cl.setAdminGroupName(null);
          cl.setGroupSet(true);
          return forwardNoAction;
        }

        return forwardNoGroupAssigned;
      }

      cl.setOneGroup(false);
      if (adgs.size() == 1) {
        cl.setOneGroup(true);
        return setGroup(request,
                        (BwAdminGroup)adgs.iterator().next());
      }

      /* Go ahead and present the possible groups
       */
      request.setSessionAttr(BwRequest.bwUserAdminGroupsInfoName,
                             adgs);

      form.assignCalSuites(cl.getContextCalSuites());
      cl.setChoosingGroup(true); // reset

      return forwardChooseGroup;
    } catch (final Throwable t) {
      form.getErr().emit(t);
      return forwardError;
    }
  }

  private static int setGroup(final BwRequest request,
                              final BwAdminGroup adg) throws Throwable {
    final BwActionFormBase form = request.getBwForm();
    final Client cl = request.getClient();

    cl.getMembers(adg);

    if (logger.debug()) {
      logger.info("Set admin group to " + adg);
    }

    cl.setAdminGroupName(adg.getAccount());
    form.assignAdminGroupName(adg.getAccount());
    cl.setGroupSet(true);

    //int access = getAccess(request, getMessages());

    final BwPrincipal p = cl.getPrincipal(adg.getOwnerHref());

    if ((p == null) ||
        !((BwAbstractAction)request.getAction()).checkSvci(request,
                                                           request.getSess(),
                                                           p.getAccount(),
                                                           isMember(adg, form),
                                                           cl.getConf())) {
      return forwardNoAccess;
    }

    return forwardNoAction;
  }

  private static boolean isMember(final BwAdminGroup ag,
                                  final BwActionFormBase form) throws Throwable {
    return ag.isMember(String.valueOf(form.getCurrentUser()), false);
  }

  /** For an administrative user this is how we determine the calendar suite
   * they are administering. We see if there is a suite associated with the
   * administrative group. If so, return.
   *
   * <p>If not we call ourself for each parent group.
   *
   * <p>If none is found we return null.
   *
   * @param request the request object
   * @param cl client
   * @return calendar suite wrapper
   * @throws Throwable on fatal error
   */
  public static BwCalSuiteWrapper findCalSuite(final Request request,
                                               final Client cl) throws Throwable {
    final String groupName = cl.getAdminGroupName();
    if (groupName == null) {
      return null;
    }

    final BwAdminGroup adg = (BwAdminGroup)cl.findGroup(groupName);

    return findCalSuite(request, cl, adg);
  }

  /** For an administrative user this is how we determine the calendar suite
   * they are administering. We see if there is a suite associated with the
   * administrative group. If so, return.
   *
   * <p>If not we call ourself for each parent group.
   *
   * <p>If none is found we return null.
   *
   * @param request the request object
   * @param cl client
   * @param adg admin group
   * @return calendar suite wrapper
   * @throws Throwable on fatal error
   */
  private static BwCalSuiteWrapper findCalSuite(final Request request,
                                                final Client cl,
                                                final BwAdminGroup adg) throws Throwable {
    if (adg == null) {
      return null;
    }

    /* At this point we still require at least authenticated read access to
     * the target calendar suite
     */

    try {
      BwCalSuiteWrapper cs = cl.getCalSuite(adg);
      if (cs != null) {
        return cs;
      }

      for (final BwGroup parent: cl.findGroupParents(adg)) {
        cs = findCalSuite(request, cl, (BwAdminGroup)parent);
        if (cs != null) {
          return cs;
        }
      }
    } catch (final CalFacadeAccessException cfe) {
      // Access is set incorrectly
      request.getErr().emit(ClientError.noCalsuiteAccess, adg.getPrincipalRef());
    }

    return null;
  }
}
