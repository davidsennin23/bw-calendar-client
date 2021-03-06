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
import org.bedework.calfacade.BwPrincipal;
import org.bedework.calfacade.DirectoryInfo;
import org.bedework.calfacade.exc.CalFacadeException;
import org.bedework.calfacade.exc.ValidationError;
import org.bedework.calfacade.svc.BwAdminGroup;
import org.bedework.calfacade.svc.BwAuthUser;
import org.bedework.calfacade.svc.UserAuth;
import org.bedework.util.misc.Util;
import org.bedework.webcommon.BwAbstractAction;
import org.bedework.webcommon.BwActionFormBase;
import org.bedework.webcommon.BwRequest;
import org.bedework.webcommon.BwWebUtil;

/** This action updates an admin group
 *
 * <p>Parameters are:<ul>
 *      <li>"delete"           Delete current admin group</li>
 *      <li>"addGroupMember"   Add member to current group</li>
 *      <li>"removeGroupMember"  Remove member from current group.</li>
 *      <li>"kind"             Kind of member, "group" or "user".</li>
 *      <li>"view"             Optional name of view to which we add subscription.</li>
 *      <li>"addtodefaultview" Optional y/n to add to default view.</li>
 * </ul>
 *
 * <p>Forwards to:<ul>
 *      <li>forwardNoAccess     user not authorised.</li>
 *      <li>forwardNotFound     no such event.</li>
 *      <li>forwardContinue     continue on to update page.</li>
 * </ul>
 *
 * @author Mike Douglass   douglm rpi.edu
 */
public class UpdateAGAction extends BwAbstractAction {
  @Override
  public int doAction(final BwRequest request,
                      final BwActionFormBase form) throws Throwable {
    final Client cl = request.getClient();

    /* Check access
     */
    if (!cl.isSuperUser()) {
      return forwardNoAccess;
    }

    if (request.getReqPar("delete") != null) {
      return forwardDelete;
    }

    cl.setChoosingGroup(false); // reset
    final boolean add = form.getAddingAdmingroup();

    final BwAdminGroup updgrp = form.getUpdAdminGroup();

    if (updgrp == null) {
      // That's not right
      return forwardDone;
    }

    try {
      if (request.getReqPar("addGroupMember") != null) {
        /* Add a user to the group we are updating.
         */
        final String mbr = BwWebUtil.checkNull(form.getUpdGroupMember());
        if (mbr == null) {
          return forwardContinue;
        }

        final String kind = request.getReqPar("kind");
        if (!validateKind(kind, form)) {
          return forwardRetry;
        }

        if (updgrp.isMember(mbr, "group".equals(kind))) {
          form.getErr().emit(ClientError.alreadyMember, mbr);
          return forwardRetry;
        }

        final BwPrincipal newMbr;

        if ("user".equals(kind)) {
          BwPrincipal p = cl.getUser(mbr);

          if (p == null) {
            cl.addUser(mbr);
            p = cl.getUser(mbr);
          }

          /* Ensure the authorised user exists - create an entry if not
           *
           * @param val      BwUser account
           */

          BwAuthUser au = cl.getAuthUser(p);

          if ((au != null) && au.isUnauthorized()) {
            return forwardNotAllowed;
          }

          if (au == null) {
            au = BwAuthUser.makeAuthUser(p.getPrincipalRef(),
                                         UserAuth.publicEventUser);
            cl.addAuthUser(au);
          }

          newMbr = p;
        } else {
          // group
          newMbr = cl.findGroup(mbr);

          if (newMbr == null) {
            form.getErr().emit(ClientError.unknownGroup, mbr);
            return forwardRetry;
          }
        }

        cl.addAdminGroupMember(updgrp, newMbr);
        updgrp.addGroupMember(newMbr);
      } else if (request.getReqPar("removeGroupMember") != null) {
        /** Remove a user or group from the group we are updating.
         */
        final String mbr = request.getReqPar("removeGroupMember");

        final String kind = request.getReqPar("kind");
        if (!validateKind(kind, form)) {
          return forwardRetry;
        }

        final BwPrincipal oldMbr;

        if ("user".equals(kind)) {
          oldMbr = cl.getUser(mbr);
        } else {
          // group
          oldMbr = cl.findAdminGroup(mbr);
        }

        if (oldMbr != null) {
          cl.removeAdminGroupMember(updgrp, oldMbr);
          updgrp.removeGroupMember(oldMbr);
        }
      } else if (add) {
        if (!validateNewAdminGroup(cl, form)) {
          return forwardRetry;
        }

        cl.addAdminGroup(updgrp);

        form.assignAddingAdmingroup(false);
      } else {
        if (!validateAdminGroup(cl, form)) {
          return forwardRetry;
        }

        if (debug()) {
          debug("About to update " + updgrp);
        }
        cl.updateAdminGroup(updgrp);
      }

      cl.refreshAdminGroups();
    } catch (final CalFacadeException cfe) {
      final String msg = cfe.getMessage();

      switch (msg) {
        case CalFacadeException.duplicateAdminGroup:
          form.getErr().emit(ClientError.duplicateGroup,
                             updgrp.getAccount());
          return forwardRetry;
        case CalFacadeException.alreadyOnGroupPath:
          form.getErr().emit(ClientError.onGroupPath,
                             updgrp.getAccount());
          return forwardRetry;
        default:
          throw cfe;
      }
    }

    /** Refetch the group
     * /

    updgrp = (BwAdminGroup)adgrps.findGroup(updgrp.getAccount());

    adgrps.getMembers(updgrp);

    form.setUpdAdminGroup(updgrp);
    */

    form.getMsg().emit(ClientMessage.updatedGroup);
    return forwardContinue;
  }

  private boolean validateNewAdminGroup(final Client cl,
                                        final BwActionFormBase form) throws Throwable {
    boolean ok = true;

    final BwAdminGroup updAdminGroup = form.getUpdAdminGroup();

    if (updAdminGroup == null) {
      // bogus call.
      return false;
    }

    updAdminGroup.setAccount(Util.checkNull(updAdminGroup.getAccount()));

    if (updAdminGroup.getAccount() == null) {
      form.getErr().emit(ValidationError.missingName);
      ok = false;
    } else {
      final DirectoryInfo di =  cl.getDirectoryInfo();
      String href = di.getBwadmingroupPrincipalRoot();
      if (!href.endsWith("/")) {
        href += "/";
      }

      updAdminGroup.setPrincipalRef(href + updAdminGroup.getAccount());
    }

    updAdminGroup.setDescription(Util.checkNull(updAdminGroup.getDescription()));

    if (updAdminGroup.getDescription() == null) {
      form.getErr().emit(ValidationError.missingDescription);
      ok = false;
    }

    final String adminGroupGroupOwner = Util.checkNull(form.getAdminGroupGroupOwner());
    if (adminGroupGroupOwner == null) {
      form.getErr().emit(ValidationError.missingGroupOwner);
      ok = false;
    } else {
      updAdminGroup.setGroupOwnerHref(cl.getUserAlways(adminGroupGroupOwner).getPrincipalRef());
    }

    String adminGroupEventOwner = Util.checkNull(form.getAdminGroupEventOwner());
    if (adminGroupEventOwner == null) {
      adminGroupEventOwner = updAdminGroup.getAccount();
    }
    if (adminGroupEventOwner == null) {
      form.getErr().emit(ValidationError.missingEventOwner);
      ok = false;
    } else {
      final String prefix = cl.getAdminGroupsIdPrefix();

      if (cl.isPrincipal(adminGroupEventOwner)) {
        // XXX For the moment just strip down to the account
        adminGroupEventOwner = adminGroupEventOwner.substring(adminGroupEventOwner.lastIndexOf("/") + 1);
      }

      if (!adminGroupEventOwner.startsWith(prefix)) {
        adminGroupEventOwner = prefix + adminGroupEventOwner;
      }

      updAdminGroup.setOwnerHref(cl.getUserAlways(adminGroupEventOwner).getPrincipalRef());
    }

    return ok;
  }

  private boolean validateAdminGroup(final Client cl,
                                     final BwActionFormBase form) throws Throwable {
    boolean ok = true;

    final BwAdminGroup updAdminGroup = form.getUpdAdminGroup();

    if (updAdminGroup == null) {
      // bogus call.
      return false;
    }

    /* We should see if somebody tried to change the name of the group */

    updAdminGroup.setDescription(Util.checkNull(updAdminGroup.getDescription()));

    if (updAdminGroup.getDescription() == null) {
      form.getErr().emit(ValidationError.missingDescription);
      ok = false;
    }

    final String adminGroupGroupOwner = Util.checkNull(form.getAdminGroupGroupOwner());
    final BwPrincipal updAgowner = cl.getPrincipal(updAdminGroup.getGroupOwnerHref());

    if ((adminGroupGroupOwner != null) &&
        (!adminGroupGroupOwner.equals(updAgowner.getAccount()))) {
      final BwPrincipal aggo = cl.getUser(adminGroupGroupOwner);

      if (aggo == null) {
        form.getErr().emit(ClientError.unknownUser, adminGroupGroupOwner);
        return false;
      }

      updAdminGroup.setGroupOwnerHref(aggo.getPrincipalRef());
    }

    String adminGroupEventOwner = Util.checkNull(form.getAdminGroupEventOwner());
    if (adminGroupEventOwner == null) {
      // no change
      return ok;
    }

    final BwPrincipal ageo = cl.getUser(adminGroupEventOwner);

    final String prefix = cl.getAdminGroupsIdPrefix();

    if (!adminGroupEventOwner.startsWith(prefix)) {
      adminGroupEventOwner = prefix + adminGroupEventOwner;
    }

    if (ageo == null) {
      form.getErr().emit(ClientError.unknownUser, adminGroupEventOwner);
      return false;
    }

    if (ageo.getPrincipalRef().equals(updAdminGroup.getOwnerHref())) {
      // no change
      return ok;
    }

    updAdminGroup.setOwnerHref(ageo.getPrincipalRef());

    return ok;
  }

  private boolean validateKind(final String kind, final BwActionFormBase form) {
    if (kind == null) {
      form.getErr().emit(ClientError.missingRequestPar, "kind");
      return false;
    }

    if ("group".equals(kind) || "user".equals(kind)) {
      return true;
    }

    form.getErr().emit(ClientError.badRequest, kind);
    return false;
  }
}

